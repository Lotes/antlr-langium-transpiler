package org.langium.antlr.model;

public class ParentChildPair {
    public final LangiumAST parent;
    public final LangiumAST child;
    public ParentChildPair(LangiumAST parent, LangiumAST child) {
        this.parent = parent;
        this.child = child;
    }
}
