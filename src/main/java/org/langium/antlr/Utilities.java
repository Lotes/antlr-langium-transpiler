package org.langium.antlr;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.langium.antlr.model.LangiumAST;

public class Utilities {
    public static void replace(LangiumAST child, List<LangiumAST> replacement) {
        LangiumAST parent = child.getParent();
        if (parent == null) {
            return;
        }
        int index = parent.removeChild(child);
        if (index >= 0) {
            Collections.reverse(replacement);
            for (LangiumAST ast : replacement) {
                parent.insertChild(ast, index);
            }
        }
    }

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

    public static Stream<LangiumAST> streamAst(LangiumAST root) {
        var children =  root.getChildren().size() > 0 ? root.getChildren().stream().flatMap(c -> streamAst(c)) : null;
        if(children != null) {
            return Stream.concat(Stream.of(root), children);
        } else {
            return Stream.of(root);
        }
    }
}
