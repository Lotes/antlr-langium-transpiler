package org.langium.antlr.model;

public class Rule implements Printable {
    public final String name;
    public final RuleKind kind;
    public final RuleExpression body;
    public final Iterable<RuleModifier> modifiers;

    public Rule(RuleKind kind, String name, RuleExpression body, Iterable<RuleModifier> modifiers) {
        this.name = name;
        this.kind = kind;
        this.body = body;
        this.modifiers = modifiers;
    }

    @Override
    public String print(int indent) {
        return (modifiers != null ? modifiers.toString()+" ": "")+kind+" rule " + name + " = "+body.print(indent);
    }
}
