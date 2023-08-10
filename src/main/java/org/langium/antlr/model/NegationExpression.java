package org.langium.antlr.model;

public class NegationExpression extends RuleExpression {
    public final RuleExpression expression;


    public NegationExpression(RuleExpression expression) {
        this.expression = expression;
    }

    @Override
    public String print(int indent) {
        return "!"+expression.print(0);
    }
    
}
