package org.langium.antlr.model;

import java.util.LinkedList;
import java.util.List;

public class NegationExpression extends RuleExpression {
    public RuleExpression expression;

    public NegationExpression(RuleExpression expression) {
        this.expression = expression;
    }

    @Override
    public String print(int indent) {
        return "!"+expression.print(0);
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
