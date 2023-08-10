package org.langium.antlr.model;

public class KeywordExpression extends RuleExpression {
    public final String text;

    public KeywordExpression(String text) {
        this.text = text;
    }

    @Override
    public String print(int indent) {
        return "'"+ text + "'";
    }
}
