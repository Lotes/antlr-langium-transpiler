package org.langium.antlr.model;

public class Rule implements Printable {
    public final String name;
    public final RuleKind kind;

    public Rule(RuleKind kind, String name) {
        this.name = name;
        this.kind = kind;
    }

    @Override
    public String print(int indent) {
        return "rule " + name + ";";
    }
}
