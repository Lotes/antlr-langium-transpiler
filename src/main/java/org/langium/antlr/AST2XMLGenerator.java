package org.langium.antlr;

import java.util.List;

import org.antlr.runtime.tree.CommonTree;

public class AST2XMLGenerator {
    private StringBuilder stringBuilder;

    public String generate(CommonTree tree) {
        stringBuilder = new StringBuilder();
        return generate(tree, 0);
    }
    private String generate(CommonTree tree, int level) {
        printIdent(level);
        stringBuilder.append("<"+tree.getClass().getSimpleName()+" text=\""+tree.getText().replace("\"", "&quote;")+"\"");
        if(tree.getChildCount() == 0) {
            stringBuilder.append("/>\n");
        } else {
            stringBuilder.append(">\n");
            var nextLevel = level + 1;
            List<CommonTree> children = tree.getChildren().stream().map(c -> (CommonTree) c).toList();
            for (CommonTree child : children) {
                generate(child, nextLevel);
            }
            printIdent(level);
            stringBuilder.append("</"+tree.getClass().getSimpleName()+">\n");
        }
        return stringBuilder.toString();
    }

    private void printIdent(int level) {
        for (int i = 0; i < level; i++) {
            stringBuilder.append("\t");
        }
    }
}
