package org.langium.antlr.model;

import java.util.LinkedList;
import java.util.List;

public class QuantifierExpression extends RuleExpression {
    public final QuantifierKind kind;
    public final RuleExpression expression;

    public QuantifierExpression(QuantifierKind kind, RuleExpression expression) {
        this.kind = kind;
        this.expression = expression;
    }

    @Override
    public String print(int indent) {
        return String.format("(%s)%s", expression.print(0), kind.getSymbol());
    }

   @Override
    public List<LangiumAST> getChildren() {
        var list = new LinkedList<LangiumAST>();
        list.add(expression);
        return list;
    }
}
