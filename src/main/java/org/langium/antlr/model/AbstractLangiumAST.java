package org.langium.antlr.model;

import java.util.List;

public abstract class AbstractLangiumAST implements LangiumAST {
    private LangiumAST parent;
    public final LangiumAST getParent() {
        return parent;
    }
    public void setParent(LangiumAST parent) {
        this.parent = parent;
    }
    public abstract List<LangiumAST> getChildren();
    public int removeChild(LangiumAST child) {
        throw new UnsupportedOperationException("Unimplemented method 'removeChild'");
    }
    public void insertChild(LangiumAST child, int index) {
        throw new UnsupportedOperationException("Unimplemented method 'insertChild'");
    }
    public int replaceChild(LangiumAST oldChild, LangiumAST newChild) {
        throw new UnsupportedOperationException("Unimplemented method 'replaceChild'");
    }
}
