package org.langium.antlr.model;

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
}
