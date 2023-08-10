package org.langium.antlr.model;

public class QuantifierExpression extends RuleExpression {
    public final QuantifierKind kind;
    public final RuleExpression expression;

    public QuantifierExpression(QuantifierKind kind, RuleExpression expression) {
        this.kind = kind;
        this.expression = expression;
    }

    @Override
    public String print(int indent) {
        return String.format("%s(%s)%s", Utilities.indent(indent), expression.print(indent), kind.getSymbol());
    }
}
