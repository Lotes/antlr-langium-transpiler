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
}
