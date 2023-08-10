package org.langium.antlr;

import java.util.List;

import org.antlr.runtime.tree.CommonTree;

public class AST2XMLGenerator {
    public static void generate(CommonTree tree, int level) {
        printIdent(level);
        System.out.print("<"+tree.getClass().getSimpleName()+" text=\""+tree.getText()+"\"");
        if(tree.getChildCount() == 0) {
            System.out.println("/>");
        } else {
            System.out.println(">");
            var nextLevel = level + 1;
            List<CommonTree> children = tree.getChildren().stream().map(c -> (CommonTree) c).toList();
            for (CommonTree child : children) {
                generate(child, nextLevel);
            }
            printIdent(level);
            System.out.println("</"+tree.getClass().getSimpleName()+">");
        }
    }

    public static void printIdent(int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("\t");
        }
    }
}
