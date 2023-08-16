package org.langium.antlr.model;

import java.util.List;

public class ParenthesesExpression extends RuleExpression {
    public RuleExpression expression;

    public ParenthesesExpression(RuleExpression expression) {
        this.expression = expression;
    }

    @Override
    public String print(int indent) {
        return "("+expression.print(0)+")";
    }

    @Override
    public List<LangiumAST> getChildren() {
        return List.of(expression);
    }

    @Override
    public int removeChild(LangiumAST child) {
        throw new UnsupportedOperationException("Unimplemented method 'removeChild'");
    }

    @Override
    public void insertChild(LangiumAST child, int index) {
        throw new UnsupportedOperationException("Unimplemented method 'insertChild'");
    }

    @Override
    public int replaceChild(LangiumAST oldChild, LangiumAST newChild) {
        throw new UnsupportedOperationException("Unimplemented method 'replaceChild'");
    }
}
