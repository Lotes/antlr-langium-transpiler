package org.langium.antlr;

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
import org.langium.antlr.model.RegexRuleExpression;
import org.langium.antlr.model.RuleCallExpression;
import org.langium.antlr.model.RuleExpression;
import org.langium.antlr.model.RuleKind;
import org.langium.antlr.model.RuleModifier;
import org.langium.antlr.model.SequenceRuleExpression;

public class LangiumGeneratingVisitor {

  public static final String NL = System.lineSeparator();
  private NamingService namingService;

  public Grammar generate(GrammarRootAST root, Collection<Grammar> imports) {
    namingService = new NamingServiceImpl();
    return readGrammarRoot(root, imports);
  }

  private Grammar readGrammarRoot(GrammarRootAST root, Collection<Grammar> imports) {
    var grammarKindText = root.getText();
    var isLexer = grammarKindText.equals("LEXER_GRAMMAR");
    GrammarBuilder builder = new GrammarBuilderImpl(namingService, isLexer ? RuleKind.Lexer : RuleKind.Parser);

    builder.name(expectChildName(root, 0));

    if (imports != null) {
      imports.forEach(i -> {
        builder.importing(i);
        for (String name : i.namingService.allNames()) {
          namingService.add(name, i.namingService.get(name));
        }
      });
    }

    GrammarAST rulesNode = this.expectNamedChild(root, 1, "RULES");
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
          if(text.equals(".")) {
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
