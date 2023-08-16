package org.langium.antlr.model;

import java.util.LinkedList;
import java.util.List;

public class RangeExpression extends RuleExpression {
    public RuleExpression left;
    public RuleExpression right;

    public RangeExpression(RuleExpression left, RuleExpression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String print(int indent) {
        return left.print(0) + ".." + right.print(0);
    }

   @Override
    public List<LangiumAST> getChildren() {
        var list = new LinkedList<LangiumAST>();
        list.add(left);
        list.add(right);
        return list;
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
