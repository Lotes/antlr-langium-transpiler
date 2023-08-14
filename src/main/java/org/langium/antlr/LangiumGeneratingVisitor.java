package org.langium.antlr;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.v4.tool.ast.GrammarRootAST;
import org.antlr.v4.tool.ast.RuleAST;
import org.antlr.v4.tool.ast.SetAST;
import org.antlr.v4.Tool;
import org.antlr.v4.tool.ast.AltAST;
import org.antlr.v4.tool.ast.BlockAST;
import org.antlr.v4.tool.ast.GrammarAST;
import org.langium.antlr.builder.GrammarBuilder;
import org.langium.antlr.builder.GrammarBuilderImpl;
import org.langium.antlr.builder.RuleBuilder;
import org.langium.antlr.model.AlternativesRuleExpression;
import org.langium.antlr.model.Grammar;
import org.langium.antlr.model.KeywordExpression;
import org.langium.antlr.model.NamingService;
import org.langium.antlr.model.NamingServiceImpl;
import org.langium.antlr.model.QuantifierExpression;
import org.langium.antlr.model.QuantifierKind;
import org.langium.antlr.model.RangeExpression;
import org.langium.antlr.model.RegexRuleExpression;
import org.langium.antlr.model.RuleCallExpression;
import org.langium.antlr.model.RuleExpression;
import org.langium.antlr.model.RuleKind;
import org.langium.antlr.model.RuleModifier;
import org.langium.antlr.model.SequenceRuleExpression;

public class LangiumGeneratingVisitor {

  public static final String NL = System.lineSeparator();
  private NamingService namingService;
  private String currentWorkingDirectory;
  private AST2XMLGenerator xmlGenerator = new AST2XMLGenerator();

  public LangiumGeneratingVisitor(String currentWorkingDirectory) {
    super();
    this.currentWorkingDirectory = currentWorkingDirectory;
  }

  public Grammar generate(GrammarRootAST root, Collection<Grammar> imports) {
    try {
      namingService = new NamingServiceImpl();
      var grammar = readGrammarRoot(root, imports);
      if (grammar.grammarKind == RuleKind.Parser) {
        addActions(grammar);
        addAssignments(grammar);
      }
      return grammar;
    } catch (Exception e) {
      var fileContent = xmlGenerator.generate(root);
      e.printStackTrace();
      throw new IllegalStateException("Error: " + e.getMessage()+"\n"+fileContent, e);
    }
  }

  private void addActions(Grammar grammar) {
    /*
     * for (Rule rule : grammar.rules.stream().filter(r -> r.body instanceof
     * AlternativesRuleExpression).toList()) {
     * var alternatives = (AlternativesRuleExpression)rule.body;
     * if(alternatives.children.size() > 1) {
     * for (var alt : alternatives.children) {
     * 
     * }
     * }
     * }
     */
  }

  private void addAssignments(Grammar grammar) {

  }

  private Grammar readGrammarRoot(GrammarRootAST root, Collection<Grammar> imports) {
    var grammarKindText = root.getText();
    var isLexer = grammarKindText.equals("LEXER_GRAMMAR");
    GrammarBuilder builder = new GrammarBuilderImpl(namingService, isLexer ? RuleKind.Lexer : RuleKind.Parser);

    builder.name(expectChildName(root, 0));

    if (imports != null) {
      imports.forEach(i -> {
        importGrammar(i, builder);
      });
    }

    GrammarAST rulesNode = null;
    for(int index=1; index < root.getChildCount(); index++) {
      var child = (GrammarAST)root.getChild(index);
      var childType = child.getText();
      switch(childType) {
        case "OPTIONS":
          parseGrammarOptions(imports, builder, child);
          break;
        case "import":
          var name = expectChildName(child, 0);
          var grammar = loadAntlr4Grammar(imports, name);
          imports.add(grammar);
          importGrammar(grammar, builder);
          break;
        case "channels {":
        case "tokens {":
          //Ignore
          break;
        case "RULES":
          rulesNode = child;
          break;
        default:
          throw new IllegalStateException("Unexpected grammar child: " + childType);
      }
    }
    Iterable<RuleAST> rules = this.expectChildrenOfType(rulesNode);
    boolean hasStartRule = isLexer; // lexer grammar has no entry rule!
    Map<RuleAST, RuleBuilder> ruleBuilders = new HashMap<RuleAST, RuleBuilder>();
    for (RuleAST rule : rules) {
      var ruleName = expectChildName(rule, 0);
      if (ruleName.startsWith("T__")) {
        continue;
      }

      var ruleBuilder = builder.beginRule(isLexer ? RuleKind.Lexer : RuleKind.Parser);

      if (!hasStartRule) {
        ruleBuilder.setModifier(RuleModifier.entry);
        hasStartRule = true;
      }
      ruleBuilders.put(rule, ruleBuilder);
    }

    for (RuleAST rule : ruleBuilders.keySet()) {
      var ruleBuilder = ruleBuilders.get(rule);
      var ruleName = expectChildName(rule, 0);

      RuleExpression body = null;
      Collection<RuleModifier> modifiers = new LinkedList<RuleModifier>();
      switch (rule.getChildCount()) {
        case 2: {
          GrammarAST block = expectNamedChild(rule, 1, "BLOCK");
          body = this.readBlock(ruleBuilder, block, true);
        }
          break;
        case 3: {
          GrammarAST modifiersParent = expectNamedChild(rule, 1, "RULEMODIFIERS");
          GrammarAST block = expectNamedChild(rule, 2, "BLOCK");
          modifiers = this.readModifier(modifiersParent);
          body = this.readBlock(ruleBuilder, block, true);
        }
          break;
        default:
          throw new IllegalStateException("Unexpected rule child count: " + rule.getChildCount());
      }

      modifiers.forEach(m -> ruleBuilder.setModifier(m));
      ruleBuilder.name(ruleName)
          .body(body)
          .end();
    }
    return builder.build();
  }

  private void parseGrammarOptions(Collection<Grammar> imports, GrammarBuilder builder, GrammarAST node) {
    Collection<GrammarAST> options = expectChildrenOfType(node);
    for (GrammarAST option : options) {
      var optionName = expectChildName(option, 0);
      var optionValue = expectChildName(option, 1);
      switch (optionName) {
        case "tokenVocab":
          var grammar = loadAntlr4Grammar(imports, optionValue);
          imports.add(grammar);
          importGrammar(grammar, builder);
          break;
        case "superClass":
          //Ignore
          break;
        default:
          throw new IllegalStateException("Unexpected option: " + optionName + "=" + optionValue);
      }
    }
  }

  private Grammar loadAntlr4Grammar(Collection<Grammar> imports, String optionValue) {
    String fileName = Path.of(currentWorkingDirectory, optionValue + ".g4").toAbsolutePath().toString();
    org.antlr.v4.tool.Grammar antlr4Grammar = new Tool().loadGrammar(fileName);
    var visitor = new LangiumGeneratingVisitor(currentWorkingDirectory);
    var grammar = visitor.generate(antlr4Grammar.ast, imports);
    return grammar;
  }

  private void importGrammar(Grammar i, GrammarBuilder builder) {
    builder.importing(i);
    for (String name : i.namingService.allNames()) {
      namingService.add(name, i.namingService.get(name));
    }
  }

  private RuleExpression readBlock(RuleBuilder ruleBuilder, GrammarAST block, boolean isRoot) {
    Collection<AltAST> children = expectChildrenOfType(block);
    return new AlternativesRuleExpression(children.stream().map(c -> {
      if (c.getText().equals("ALT")) {
        return this.readAlternative(ruleBuilder, c);
      } else if (c.getText().equals("LEXER_ALT_ACTION")) {
        AltAST alt = expectNamedChild(c, 0, "ALT");
        String action = expectChildName(c, 1);
        if (action.equals("skip")) {
          ruleBuilder.setModifier(RuleModifier.hidden);
        }
        return readAlternative(ruleBuilder, alt);
      } else {
        throw new IllegalStateException("Unexpected child: " + c.getClass().getSimpleName() + " text='" + c.getText()
            + "' (line " + c.getLine() + ")");
      }
    }).toList(), isRoot);
  }

  private RuleExpression readAlternative(RuleBuilder ruleBuilder, AltAST alt) {
    return new SequenceRuleExpression(alt.getChildren().stream().map(c -> (GrammarAST) c).map(child -> {
      switch (child.getClass().getSimpleName()) {
        case "RangeAST": {
          var left = expectChildName(child, 0);
          var right = expectChildName(child, 1);
          assert left.startsWith("'");
          assert right.startsWith("'");
          return new RangeExpression(
            new KeywordExpression(left.substring(1, left.length()-1)),
            new KeywordExpression(right.substring(1, right.length()-1))
          );
        }
        case "BlockAST": {
          return this.readBlock(ruleBuilder, child, false);
        }
        case "PlusBlockAST": {
          BlockAST block = expectNamedChild(child, 0, "BLOCK");
          RuleExpression expression = this.readBlock(ruleBuilder, block, false);
          return new QuantifierExpression(QuantifierKind.OneOrMore, expression);
        }
        case "StarBlockAST": {
          BlockAST block = expectNamedChild(child, 0, "BLOCK");
          RuleExpression expression = this.readBlock(ruleBuilder, block, false);
          return new QuantifierExpression(QuantifierKind.ZeroOrMore, expression);
        }
        case "OptionalBlockAST": {
          BlockAST block = expectNamedChild(child, 0, "BLOCK");
          RuleExpression expression = this.readBlock(ruleBuilder, block, false);
          return new QuantifierExpression(QuantifierKind.Optional, expression);
        }
        case "TerminalAST": {
          var text = child.getText();
          if (text.startsWith("\'")) {
            text = text.substring(1, text.length() - 1);
            return new KeywordExpression(text);
          }
          if (text.equals("EOF")) {
            return null;
          }
          if (text.equals(".")) {
            return new RegexRuleExpression(text);
          }
          return new RuleCallExpression(namingService, text);
        }
        case "RuleRefAST":
          return new RuleCallExpression(namingService, child.getText());
        case "GrammarAST": {
          var text = child.getText();
          return new RegexRuleExpression(text);
        }
        case "NotAST": {
          SetAST set = expectNamedChild(child, 0, "SET");
          String regex = expectChildName(set, 0);
          return new RegexRuleExpression(regex);
        }
        case "PredAST":
        case "ActionAST": {
          return null;
        }
        default:
          throw new IllegalStateException("Unexpected alternative child: " + child.getClass().getSimpleName()
              + " text='" + child.getText() + "' (line " + child.getLine() + ")");
      }
    }).filter(c -> c != null).toList());
  }

  public static boolean isValidIdentifier(String identifier) {
    String regex = "^([a-zA-Z_][a-zA-Z\\d_]*)$";
    Pattern p = Pattern.compile(regex);
    if (identifier == null) {
      return false;
    }
    Matcher m = p.matcher(identifier);
    return m.matches();
  }

  private Collection<RuleModifier> readModifier(GrammarAST modifiersParent) {
    List<RuleModifier> result = new LinkedList<RuleModifier>();
    Iterable<GrammarAST> children = expectChildrenOfType(modifiersParent);
    for (GrammarAST child : children) {
      var text = child.getText();
      try {
        var modifier = RuleModifier.valueOf(text);
        result.add(modifier);
      } catch (IllegalArgumentException e) {
        throw new IllegalStateException("Unexpected rule modifier: " + text);
      }
    }
    return result;
  }

  private <T extends CommonTree> Collection<T> expectChildrenOfType(CommonTree node) {
    var children = node.getChildren().stream().map(c -> {
      @SuppressWarnings("unchecked")
      var casted = (T) c;
      return casted;
    }).toList();
    return children;
  }

  private String expectChildName(CommonTree node, int childIndex) {
    var child = node.getChild(childIndex);
    return child.getText();
  }

  private <T extends CommonTree> T expectNamedChild(CommonTree node, int childIndex, String name) {
    var child = node.getChild(childIndex);
    if (!child.getText().equals(name)) {
      throw new IllegalStateException("Expected child to be '" + name + "'' but got '" + child.getText() + "'");
    }
    @SuppressWarnings("unchecked")
    var result = (T) child;
    return result;
  }
}
