package org.langium.antlr.model;

public class Grammar implements Printable {
    public final String name;
    public final Iterable<Rule> rules;

    public Grammar(String name, Iterable<Rule> rules) {
        this.name = name;
        this.rules = rules;
    }

    @Override
    public String print(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append("grammar ").append(name).append(";\n\n");
        for (Rule rule : rules) {
            sb.append(rule.print(indent)).append("\n\n");
        }
        return sb.toString();
    }
}
