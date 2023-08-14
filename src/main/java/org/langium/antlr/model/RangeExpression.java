package org.langium.antlr.model;

public class RangeExpression extends RuleExpression {
    private RuleExpression left;
    private RuleExpression right;

    public RangeExpression(RuleExpression left, RuleExpression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String print(int indent) {
        return left.print(0) + ".." + right.print(0);
    }
}
