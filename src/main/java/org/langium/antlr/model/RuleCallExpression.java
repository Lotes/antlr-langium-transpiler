package org.langium.antlr.model;

public class RuleCallExpression extends RuleExpression {
    public final String ruleName;

    public RuleCallExpression(String ruleName) {
        this.ruleName = ruleName;
    }

    @Override
    public String print(int indent) {
        return ruleName;
    }
}
