package org.langium.antlr;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.v4.tool.ast.GrammarRootAST;
import org.antlr.v4.tool.ast.RuleAST;
import org.antlr.v4.tool.ast.GrammarAST;
import org.langium.antlr.builder.GrammarBuilder;
import org.langium.antlr.builder.GrammarBuilderImpl;
import org.langium.antlr.model.Grammar;
import org.langium.antlr.model.RuleKind;

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


      builder.beginRule(isLexer?RuleKind.Lexer:RuleKind.Parser)
        .name(ruleName)
        .end();
    }
    return builder.build();
  }

  private <T extends CommonTree> Iterable<T> expectChildrenOfType(CommonTree node) {
    var children = node.getChildren().stream().map(c -> {
      @SuppressWarnings("unchecked")
      var casted = (T)c;
      return casted;
    }).toList();
    return children;
  }

  private String expectChildName(CommonTree node, int childIndex) {
    var child = node.getChild(childIndex);
    return child.getText();
  }

  private <T extends CommonTree> T expectChild(CommonTree node, int childIndex) {
    var child = node.getChild(childIndex);
    @SuppressWarnings("unchecked")
    var result = (T)child;
    return result;
  }

  private <T extends CommonTree> T expectNamedChild(CommonTree node, int childIndex, String name) {
    var child = node.getChild(childIndex);
    if(!child.getText().equals(name)) {
      throw new IllegalStateException("Expected child to be '" + name + "'' but got '" + child.getText()+"'");      
    }
    @SuppressWarnings("unchecked")
    var result = (T)child;
    return result;
  }
}
