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
        throw new UnsupportedOperationException("Unimplemented method 'replaceChild'");
    }
}
