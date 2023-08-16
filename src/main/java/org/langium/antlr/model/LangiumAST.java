package org.langium.antlr.model;

import java.util.List;

public interface LangiumAST {
    List<LangiumAST> getChildren();
    int removeChild(LangiumAST child);
    void insertChild(LangiumAST child, int index);
    int replaceChild(LangiumAST oldChild, LangiumAST newChild);
}
