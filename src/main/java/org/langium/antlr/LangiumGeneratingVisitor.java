package org.langium.antlr;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.v4.tool.ast.GrammarRootAST;
import org.antlr.v4.tool.ast.RangeAST;
import org.antlr.v4.tool.ast.RuleAST;
import org.antlr.v4.tool.ast.SetAST;
import org.antlr.v4.tool.ast.TerminalAST;
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
import org.langium.antlr.transformers.Transformer;
import org.langium.antlr.transformers.Transformers;

public class LangiumGeneratingVisitor {

  private NamingService namingService;
  private Map<String, Grammar> grammarMap = new HashMap<String, Grammar>();
  private Map<String, String> errorMap = new HashMap<String, String>();
  private String workingDirectory;

  public LangiumGeneratingVisitor(String workingDirectory) {
    super();
    this.workingDirectory = workingDirectory;
    namingService = new NamingServiceImpl();
  }

  public List<GrammarFile> toLangium(org.antlr.v4.tool.Grammar grammar) {
    var errors = errorMap.entrySet().stream().map(g -> new GrammarFile(g.getKey()+".error.xml", g.getValue()));
    try {
      generate(grammar);
      var output = grammarMap.values().stream().map(g -> new GrammarFile(g.name+".langium", g.print(0)));
      return Stream.concat(output, errors).toList();
    } catch(Exception e) {
      return errors.toList();
    }
  }

  private Grammar generate(org.antlr.v4.tool.Grammar grammar) {
    Grammar lexerGrammar = null;
    if (grammar.implicitLexer != null && grammar.implicitLexer.ast != null) {
        lexerGrammar = generate(grammar.implicitLexer.ast);
    }
    Grammar parserGrammar = generate(grammar.ast);
    if(lexerGrammar != null) {
      parserGrammar.imports.add(lexerGrammar);
    }
    return parserGrammar;
  }

  private Grammar generate(GrammarRootAST root) {
    var name = expectChildName(root, 0);
    var astMaker = new AST2XMLGenerator();
    var tree = astMaker.generate(root);
    errorMap.put(name, tree);
    try {
      var grammar = readGrammarRoot(root);
      for (Transformer transformer : Transformers.list) {
        if(transformer.canTransform(grammar)) {
          transformer.transform(grammar);
        }
      }
      return grammar;
    } catch(Exception e) {
      errorMap.remove(name);
      errorMap.put(name, e.getMessage()+"\n"+tree);
      e.printStackTrace();
      throw e;
    }
  }

  private Grammar importFromFile(String grammarName) {
    if(grammarMap.containsKey(grammarName)) {
      return grammarMap.get(grammarName);
    }
    var path = Path.of(workingDirectory, grammarName+".g4");
    var app = new Tool(new String[0]);
    var antlrGrammar = app.loadGrammar(path.toString());
    var grammar = generate(antlrGrammar);
    grammarMap.put(grammarName, grammar);
    return grammar;
  }

  private Grammar readGrammarRoot(GrammarRootAST root) {
    var grammarKindText = root.getText();
    var isLexer = grammarKindText.equals("LEXER_GRAMMAR");
    GrammarBuilder builder = new GrammarBuilderImpl(namingService, isLexer ? RuleKind.Lexer : RuleKind.Parser);

    String grammarName = expectChildName(root, 0);
    builder.name(grammarName);

    for (int index = 1; index < root.getChildCount(); index++) {
      var child = (GrammarAST) root.getChild(index);
      var childType = child.getText();
      switch (childType) {
        case "OPTIONS":
          parseGrammarOptions(builder, child);
          break;
        case "import":
          var name = expectChildName(child, 0);
          var next = importFromFile(name);
          builder.importing(next);
          break;
        case "tokens {":
          for (var token : expectChildrenOfType(child)) {
            var tokenText = token.getText();
            if (!namingService.has(tokenText)) {
              namingService.add(tokenText, tokenText);
            }
          }
          break;
        case "channels {":
          // Ignore
          break;
        case "RULES":
          getRules(isLexer, builder, child);
          break;
        case "mode":
          var modeName = expectChildName(child, 0);
          getRules(isLexer, builder, (GrammarAST) child, modeName);
          break;
        default:
          throw new IllegalStateException("Unexpected grammar child: " + childType);
      }
    }

    var result = builder.build();
    grammarMap.put(result.name, result);
    return result;
  }

  private void getRules(boolean isLexer, GrammarBuilder builder, GrammarAST rulesNode) {
    getRules(isLexer, builder, rulesNode, null);
  }

  private void getRules(boolean isLexer, GrammarBuilder builder, GrammarAST rulesNode, String modeName) {
    Iterable<RuleAST> rules = this.expectChildrenOfType(rulesNode, 1);
    boolean hasStartRule = isLexer; // lexer grammar has no entry rule!
    Map<RuleAST, RuleBuilder> ruleBuilders = new HashMap<RuleAST, RuleBuilder>();
    for (RuleAST rule : rules) {
      var ruleName = expectChildName(rule, 0);
      if (ruleName.startsWith("T__")) {
        continue;
      }

      var ruleBuilder = builder.beginRule(isLexer ? RuleKind.Lexer : RuleKind.Parser).mode(modeName);

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
  }

  private void parseGrammarOptions(GrammarBuilder builder, GrammarAST node) {
    Collection<GrammarAST> options = expectChildrenOfType(node);
    for (GrammarAST option : options) {
      var optionName = expectChildName(option, 0);
      var optionValue = expectChildName(option, 1);
      switch (optionName) {
        case "tokenVocab":
          builder.importing(importFromFile(optionValue));
          break;
        case "superClass":
          // Ignore
          break;
        default:
          throw new IllegalStateException("Unexpected option: " + optionName + "=" + optionValue);
      }
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
          return readRange((RangeAST)child);
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
          return readTerminal((TerminalAST)child);
        }
        case "RuleRefAST":
          return new RuleCallExpression(namingService, child.getText());
        case "GrammarAST": {
          var text = child.getText();
          if(text == "SET") {
            List<RuleExpression> expressions = new LinkedList<RuleExpression>();
            for (var ast : child.getChildren()) {
              RuleExpression expression = null;
              switch(ast.getClass().getSimpleName()) {
                case "RangeAST": {
                  expression = readRange((RangeAST)ast);
                  break;
                }
                case "TerminalAST": {
                  expression = readTerminal((TerminalAST)ast);
                  break;
                }
                default:
                  throw new RuntimeException("Unexpected child: " + ast.getClass().getSimpleName() + " text='" + ((CommonTree)ast).getText());
              }
              if(expression != null) {
                expressions.add(expression);
              }
            }
            return new AlternativesRuleExpression(expressions, false);
          }
          return new RegexRuleExpression(text);
        }
        case "NotAST": {
          SetAST set = expectNamedChild(child, 0, "SET");
          String regex = expectChildName(set, 0);
          return new RegexRuleExpression("("+regex+")");
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

  private RuleExpression readTerminal(TerminalAST child) {
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

  private RuleExpression readRange(RangeAST child) {
    var left = readTerminal((TerminalAST)child.getChild(0));
    var right = readTerminal((TerminalAST)child.getChild(1));
    return new RangeExpression(left, right);
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
    return expectChildrenOfType(node, 0);
  }

  private <T extends CommonTree> Collection<T> expectChildrenOfType(CommonTree node, int skipItems) {
    var children = node.getChildren().stream().skip(skipItems).map(c -> {
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
