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
}
