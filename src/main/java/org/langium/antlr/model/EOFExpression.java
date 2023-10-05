package org.langium.antlr.model;

import java.util.List;

public class EOFExpression extends RuleExpression {
    public static final EOFExpression INSTANCE = new EOFExpression();

    private EOFExpression() {
        super();
    }

    @Override
    public String print(int indent) {
        return "EOF";
    }

    @Override
    public List<LangiumAST> getChildren() {
        return List.of();
    }
}
