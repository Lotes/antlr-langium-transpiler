package org.langium.antlr;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
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
import org.langium.antlr.model.QuantifierExpression;
import org.langium.antlr.model.QuantifierKind;
import org.langium.antlr.model.RegexRuleExpression;
import org.langium.antlr.model.RuleCallExpression;
import org.langium.antlr.model.RuleExpression;
import org.langium.antlr.model.RuleKind;
import org.langium.antlr.model.RuleModifier;
import org.langium.antlr.model.SequenceRuleExpression;

public class LangiumGeneratingVisitor2 {

  public static final String NL = System.lineSeparator();

  public Grammar generate(GrammarRootAST root, Grammar[] imports) {
    return readGrammarRoot(root, imports);
  }

  private Grammar readGrammarRoot(GrammarRootAST root, Grammar[] imports) {
    var grammarKindText = root.getText();
    var isLexer = grammarKindText.equals("LEXER_GRAMMAR");

    GrammarBuilder builder = new GrammarBuilderImpl();

    builder.name(expectChildName(root, 0));

    GrammarAST rulesNode = this.expectNamedChild(root, 1, "RULES");
    Iterable<RuleAST> rules = this.expectChildrenOfType(rulesNode);
    for (RuleAST rule : rules) {
      var ruleName = expectChildName(rule, 0);
      var ruleBuilder = builder.beginRule(isLexer ? RuleKind.Lexer : RuleKind.Parser);

      RuleExpression body = null;
      Collection<RuleModifier> modifiers = new LinkedList<RuleModifier>();
      switch (rule.getChildCount()) {
        case 2: {
          GrammarAST block = expectNamedChild(rule, 1, "BLOCK");
          body = this.readBlock(ruleBuilder, block);
        }
          break;
        case 3: {
          GrammarAST modifiersParent = expectNamedChild(rule, 1, "RULEMODIFIERS");
          GrammarAST block = expectNamedChild(rule, 2, "BLOCK");
          modifiers = this.readModifier(modifiersParent);
          body = this.readBlock(ruleBuilder, block);
        }
          break;
        default:
          throw new IllegalStateException("Unexpected rule child count: " + rule.getChildCount());
      }
      ruleBuilder.name(ruleName)
          .body(body)
          .modifiers(modifiers)
          .end();
    }
    return builder.build();
  }

  private RuleExpression readBlock(RuleBuilder ruleBuilder, GrammarAST block) {
    Collection<AltAST> children = expectChildrenOfType(block);
    return new AlternativesRuleExpression(children.stream().map(c -> {
      if(c.getText().equals("ALT")) {
        return this.readAlternative(ruleBuilder, c);
      } else if(c.getText().equals("LEXER_ALT_ACTION")) {
        AltAST alt = expectNamedChild(c, 0, "ALT");
        String action = expectChildName(c, 1);
        if(action.equals("skip")) {
          ruleBuilder.setHidden(true);
        }
        return readAlternative(ruleBuilder, alt);
      } else {
        throw new IllegalStateException("Unexpected child: " + c.getClass().getSimpleName()+" text='"+c.getText()+"' (line "+c.getLine()+")");
      }
    }).toList());
  }

  private RuleExpression readAlternative(RuleBuilder ruleBuilder, AltAST alt) {
    return new SequenceRuleExpression(alt.getChildren().stream().map(c -> (GrammarAST) c).map(child -> {
      switch(child.getClass().getSimpleName()) {
        case "BlockAST": {
          return this.readBlock(ruleBuilder, child);
        }
        case "PlusBlockAST": {
          BlockAST block = expectNamedChild(child, 0, "BLOCK");
          RuleExpression expression = this.readBlock(ruleBuilder, block);
          return new QuantifierExpression(QuantifierKind.OneOrMore, expression);
        }
        case "StarBlockAST": {
          BlockAST block = expectNamedChild(child, 0, "BLOCK");
          RuleExpression expression = this.readBlock(ruleBuilder, block);
          return new QuantifierExpression(QuantifierKind.ZeroOrMore, expression);
        } 
        case "OptionalBlockAST": {
          BlockAST block = expectNamedChild(child, 0, "BLOCK");
          RuleExpression expression = this.readBlock(ruleBuilder, block);
          return new QuantifierExpression(QuantifierKind.Optional, expression);
        }
        case "TerminalAST": {
          var text = child.getText();
          if(text.startsWith("\'")) {
            text = text.substring(1, text.length()-1);
            return new KeywordExpression(text);
          }
          return new RuleCallExpression(text);
        }
        case "RuleRefAST":
          return new RuleCallExpression(child.getText());
        case "GrammarAST": {
          var text = child.getText();
          return new RegexRuleExpression(text);
        }
        case "NotAST": {
          SetAST set = expectNamedChild(child, 0, "SET");
          String regex = expectChildName(set, 0);
          return new RegexRuleExpression(regex);
        }
        default:
          throw new IllegalStateException("Unexpected alternative child: " + child.getClass().getSimpleName()+" text='"+child.getText()+"' (line "+child.getLine()+")");
      }
    }).toList());
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
