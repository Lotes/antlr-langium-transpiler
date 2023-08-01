package org.langium.antlr;

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

  @Override
  public Object visit(GrammarAST node) {
    return visitChildren(node, "");
  }

  @Override
  public Object visit(GrammarRootAST node) {
    return visitChildren(node, "");
  }

  private String visitChildren(GrammarAST node, String result) {
    if(node.getChildCount() > 0) {
      for (Object child : node.getChildren()) {
        result += ((GrammarAST)child).visit(this);
      }
    }
    return result;
  }

  @Override
  public Object visit(RuleAST node) {
    if(node.getRuleName().startsWith("T__")) {
      return "";
    }
    
    return node.getRuleName()+":"+NL+visitChildren(node, "")+NL;
  }

  @Override
  public Object visit(BlockAST node) {
    return "/* TODO block */";
  }

  @Override
  public Object visit(OptionalBlockAST node) {
    return "/* TODO optional block */";
  }

  @Override
  public Object visit(PlusBlockAST node) {
    return "/* TODO plus block */";
  }

  @Override
  public Object visit(StarBlockAST node) {
    return "/* TODO star block */";
  }

  @Override
  public Object visit(AltAST node) {
    return "/* TODO alt */";
  }

  @Override
  public Object visit(NotAST node) {
    return "/* TODO not */";
  }

  @Override
  public Object visit(PredAST node) {
    return "/* TODO pred */";
  }

  @Override
  public Object visit(RangeAST node) {
    return "/* TODO range */";
  }

  @Override
  public Object visit(SetAST node) {
    return "/* TODO set */";
  }

  @Override
  public Object visit(RuleRefAST node) {
    return "/* TODO rule ref */";
  }

  @Override
  public Object visit(TerminalAST node) {
    return "/* TODO terminal */";
  }
}
