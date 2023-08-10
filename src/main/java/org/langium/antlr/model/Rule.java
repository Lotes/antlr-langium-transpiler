package org.langium.antlr.model;

import java.util.Collection;

public class Rule implements Printable {
    public final String name;
    public final RuleKind kind;
    public final RuleExpression body;
    public final Collection<RuleModifier> modifiers;

    public Rule(RuleKind kind, String name, RuleExpression body, Collection<RuleModifier> modifiers) {
        this.name = name;
        this.kind = kind;
        this.body = body;
        this.modifiers = modifiers;
    }

    @Override
    public String print(int indent) {
        if(kind == RuleKind.Lexer) {
            String fragment = "terminal ";
            if(modifiers.contains(RuleModifier.fragment)) {
                fragment += "fragment ";
            }
            if(modifiers.contains(RuleModifier.hidden)) {
                fragment = "hidden " + fragment;
            }
            return fragment + name + ": "+body.print(0)+";";
        } else {
            String entry = "";
            if(modifiers.contains(RuleModifier.entry)) {
                entry = "entry ";
            }            
            return entry + name + ": "+body.print(0) + ";";
        }
    }
}
