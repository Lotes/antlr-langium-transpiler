package org.langium.antlr.model;

import java.util.List;

public interface LangiumAST {
    List<LangiumAST> getChildren();
}
