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
    public int removeChild(LangiumAST child) {
        throw new UnsupportedOperationException("Unimplemented method 'removeChild'");
    }

    @Override
    public void insertChild(LangiumAST child, int index) {
        throw new UnsupportedOperationException("Unimplemented method 'insertChild'");
    }

    @Override
    public int replaceChild(LangiumAST oldChild, LangiumAST newChild) {
        if(newChild instanceof RuleExpression) {
            if(oldChild == expression) {
                expression = (RuleExpression) newChild;
                return 0;
            }
        }
        throw new UnsupportedOperationException("Unimplemented method 'replaceChild'");
    }
    
}
