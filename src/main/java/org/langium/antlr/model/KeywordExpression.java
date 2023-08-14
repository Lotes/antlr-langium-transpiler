package org.langium.antlr.model;

import java.util.LinkedList;
import java.util.List;

public class KeywordExpression extends RuleExpression {
    public final String text;

    public KeywordExpression(String text) {
        this.text = text;
    }

    @Override
    public String print(int indent) {
        return "'"+ text + "'";
    }

    @Override
    public List<LangiumAST> getChildren() {
        return new LinkedList<LangiumAST>();
    }
}
