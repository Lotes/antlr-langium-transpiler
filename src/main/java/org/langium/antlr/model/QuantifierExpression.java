package org.langium.antlr.model;

import java.util.LinkedList;
import java.util.List;

public class QuantifierExpression extends RuleExpression {
    public final QuantifierKind kind;
    public RuleExpression expression;

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

    @Override
    public int replaceChild(LangiumAST oldChild, LangiumAST newChild) {
        if(newChild instanceof RuleExpression) {
            if(oldChild == expression) {
                expression = (RuleExpression) newChild;
                oldChild.setParent(null);
                newChild.setParent(this);
                return 0;
            }
        }
        return super.replaceChild(oldChild, newChild);
    }
}
