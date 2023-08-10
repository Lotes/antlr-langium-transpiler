package org.langium.antlr.model;

public class RuleCallExpression extends RuleExpression {
    public final String ruleName;
    private NamingService namingService;

    public RuleCallExpression(NamingService namingService, String ruleName) {
        this.namingService = namingService;
        this.ruleName = ruleName;
    }

    @Override
    public String print(int indent) {
        return namingService.get(ruleName);
    }
}
