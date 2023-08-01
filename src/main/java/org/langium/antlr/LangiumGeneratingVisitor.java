package org.langium.antlr;

import java.util.HashMap;
import java.util.Map;

import org.antlr.runtime.tree.Tree;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.AltAST;
import org.antlr.v4.tool.ast.BlockAST;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.GrammarASTVisitor;
import org.antlr.v4.tool.ast.GrammarRootAST;
import org.antlr.v4.tool.ast.NotAST;
import org.antlr.v4.tool.ast.OptionalBlockAST;
import org.antlr.v4.tool.ast.PlusBlockAST;
import org.antlr.v4.tool.ast.PredAST;
import org.antlr.v4.tool.ast.RangeAST;
import org.antlr.v4.tool.ast.RuleAST;
import org.antlr.v4.tool.ast.RuleRefAST;
import org.antlr.v4.tool.ast.SetAST;
import org.antlr.v4.tool.ast.StarBlockAST;
import org.antlr.v4.tool.ast.TerminalAST;

public class LangiumGeneratingVisitor implements GrammarASTVisitor {

  public static final String NL = System.lineSeparator();
  private StringBuilder result = new StringBuilder();
  private Map<String, String> ruleNameMappings = new HashMap<String, String>();
  private Grammar grammar;

  public LangiumGeneratingVisitor(Grammar grammar) {
    super();
    this.grammar = grammar;
  }

  @Override
  public Object visit(GrammarAST node) {
    if(node.getChildCount() == 0 ) {
      String text = node.toString();
      if(text.startsWith("[")) {
        result.append("/");
        result.append(text);
        result.append("/");
      }
    } else {
      visitChildren(node);
    }
    return null;
  }

  @Override
  public Object visit(GrammarRootAST node) {
    visitChildren(node);
    return null;
  }

  private void visitChildren(GrammarAST node) {
    visitChildren(node, "");
  }

  private void visitChildren(GrammarAST node, String glue) {
    int count = node.getChildCount();
    if (count > 0) {
      int index = 0;
      for (Object child : node.getChildren()) {
        ((GrammarAST) child).visit(this);
        if (index < count - 1 && glue != null && !glue.isEmpty()) {
          result.append(glue);
        }
        index++;
      }
    }
  }

  @Override
  public Object visit(RuleAST node) {
    if (node.getRuleName().startsWith("T__")) {
      return null;
    }

    String originalRuleName = node.getRuleName();
    String finalRuleName = !node.isLexerRule()
        ? getParserRuleName(originalRuleName)
        : getLexerRuleName(originalRuleName);

    Rule rule = !node.isLexerRule() ? grammar.getRule(node.atnState.ruleIndex) : null;
    boolean isStartRule = rule != null ? rule.isStartRule : false;
    String start = isStartRule ? "entry " : "";

    if (finalRuleName != null) {
      //TODO hidden is not correct (skip or hidden channel)
      String hidden = hasModifier(node, "hidden") ? "hidden " : "";
      String fragment = hasModifier(node, "fragment") ? "fragment " : "";
      String terminal = node.isLexerRule() ? hidden+"terminal "+fragment : "";
      String returns = node.isLexerRule() ? " returns string" : "";
      result.append(start + terminal + finalRuleName + returns + ": ");
      visitChildren(node);
      result.append(";" + NL);
    }

    return null;
  }

  private boolean hasModifier(RuleAST node, String modifier) {
    if(!node.isLexerRule()) {
      return false;
    }
    Tree ruleModifiers = node.getChild(1);
    for(int index=0; index<ruleModifiers.getChildCount(); index++) {
      Tree ruleModifier = ruleModifiers.getChild(index);
      if(ruleModifier.getText().toLowerCase().equals(modifier)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Object visit(BlockAST node) {
    if(node.getChildCount() == 1 || node.parent instanceof RuleAST) {
      visitChildren(node, " | ");
    } else {
      result.append("(");
      visitChildren(node, " | ");
      result.append(")");
    }
    return null;
  }

  @Override
  public Object visit(OptionalBlockAST node) {
    if(node.getChildCount() == 1) {
      visitChildren(node);
      result.append("?");
    } else {
      result.append("(");
      visitChildren(node);
      result.append(")?");
    }
    return null;
  }

  @Override
  public Object visit(PlusBlockAST node) {
     if(node.getChildCount() == 1) {
      visitChildren(node);
      result.append("+");
    } else {
      result.append("(");
      visitChildren(node);
      result.append(")+");
    }
    return null;
  }

  @Override
  public Object visit(StarBlockAST node) {
    if(node.getChildCount() == 1) {
      visitChildren(node);
      String text = result.toString();
      char lastChar = text.charAt(text.length() - 1);
      if(lastChar == '/') {
        result.append(" ");
      }
      result.append("*");
    } else {
      result.append("(");
      visitChildren(node);
      result.append(")*");
    }
    return null;
  }

  @Override
  public Object visit(AltAST node) {
   if(node.getChildCount() == 1 || node.parent instanceof RuleAST || (node.parent instanceof BlockAST && node.parent.parent instanceof RuleAST)) {
      visitChildren(node, " ");
    } else {
      result.append("(");
      visitChildren(node, " ");
      result.append(")");
    }
    return null;
  }

  @Override
  public Object visit(NotAST node) {
    boolean hasOnlyOneChild = node.getChildCount() == 1;
    result.append("!");
    if(!hasOnlyOneChild) {result.append("(");}
    visitChildren(node);
    if(!hasOnlyOneChild) {result.append(")");}
    return "/* TODO not */";
  }

  @Override
  public Object visit(PredAST node) {
    visitChildren(node);
    return null;
  }

  @Override
  public Object visit(RangeAST node) {
    visitChildren(node);
    return null;
  }

  @Override
  public Object visit(SetAST node) {
    visitChildren(node);
    return null;
  }

  @Override
  public Object visit(RuleRefAST node) {
    String newName = getParserRuleName(node.getText());
    result.append(newName);
    return null;
  }

  private String getParserRuleName(String originalName) {
    if (!ruleNameMappings.containsKey(originalName)) {
      String nameBase = originalName.substring(0, 1).toUpperCase() + originalName.substring(1);
      String newName = nameBase;
      int index = 1;
      while (ruleNameMappings.containsValue(newName)) {
        nameBase = nameBase + index;
        index++;
      }
      ruleNameMappings.put(originalName, newName);
    }
    String newName = ruleNameMappings.get(originalName);
    return newName;
  }

  private String getLexerRuleName(String originalName) {
    if (originalName.equals("EOF")) {
      return "/* EOF */";
    }
    if (!ruleNameMappings.containsKey(originalName)) {
      if (originalName.startsWith("'")) {
        ruleNameMappings.put(originalName, originalName);
      } else {
        String nameBase = originalName.toUpperCase();
        String newName = nameBase;
        int index = 1;
        while (ruleNameMappings.containsValue(newName)) {
          nameBase = nameBase + index;
          index++;
        }
        ruleNameMappings.put(originalName, newName);
      }
    }
    return ruleNameMappings.get(originalName);
  }

  @Override
  public Object visit(TerminalAST node) {
    String newName = getLexerRuleName(node.getText());
    if (newName != null) {
      result.append(newName);
    }
    return null;
  }

  @Override
  public String toString() {
    return result.toString();
  }
}
