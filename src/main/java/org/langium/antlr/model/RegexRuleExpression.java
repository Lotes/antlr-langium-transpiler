package org.langium.antlr.model;

public class RegexRuleExpression extends RuleExpression {
    public final String pattern;

    public RegexRuleExpression(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String print(int indent) {
        return pattern;
    }
}
