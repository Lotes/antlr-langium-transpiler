package org.langium.antlr;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.langium.antlr.model.Grammar;
import org.langium.antlr.model.LangiumAST;

public class Utilities {
    public static void linkToParents(LangiumAST node, LangiumAST parent) {
        node.setParent(parent);
        for (LangiumAST child : node.getChildren()) {
            linkToParents(child, node);
        }
    }

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
        if (name == null || name.length() == 0) {
            return name;
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static String toUpperCase(String name) {
        return name.toUpperCase();
    }

    public static Stream<LangiumAST> streamAst(LangiumAST root) {
        var nonGrammars = root.getChildren().stream().filter(c -> !(c instanceof Grammar)).collect(Collectors.toList());
        var children =  nonGrammars.size() > 0 ? nonGrammars.stream().flatMap(c -> streamAst(c)) : null;
        if(children != null) {
            return Stream.concat(Stream.of(root), children);
        } else {
            return Stream.of(root);
        }
    }

    public static LangiumAST getRoot(LangiumAST node) {
        while(node.getParent() != null) {
            node = node.getParent();
        }
        return node;
    }
}
