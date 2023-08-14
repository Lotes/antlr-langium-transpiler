package org.langium.antlr;

import java.util.stream.Stream;

import org.langium.antlr.model.LangiumAST;
import org.langium.antlr.model.ParentChildPair;

public class Utilities {
    public static String indent(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append("\t");
        }
        return sb.toString();
    }

    public static String capitalize(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static String toUpperCase(String name) {
        return name.toUpperCase();
    }

    public static Stream<ParentChildPair> streamAst(LangiumAST ast) {
        return streamAst(ast, null);
    }

    private static Stream<ParentChildPair> streamAst(LangiumAST ast, LangiumAST parent) {
        return Stream.of(new ParentChildPair(parent, ast)).flatMap(pc -> pc.child.getChildren().stream().flatMap(c -> streamAst(pc.child, c)));
    }
}
