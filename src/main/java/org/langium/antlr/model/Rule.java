package org.langium.antlr.model;

import java.util.Collection;

public class Rule implements Printable {
    public final String name;
    public final RuleKind kind;
    public final RuleExpression body;
    public final Collection<RuleModifier> modifiers;
    public String LexerMode = null;
    private NamingService namingService;
    public String getLangiumName() {
        return namingService.get(name);
    }

    public Rule(NamingService namingService, RuleKind kind, String name, RuleExpression body, Collection<RuleModifier> modifiers) {
        this.namingService = namingService;
        namingService.add(name, kind == RuleKind.Parser ? Utilities.capitalize(name) : Utilities.toUpperCase(name));
        this.name = name;
        this.kind = kind;
        this.body = body;
        this.modifiers = modifiers;
    }

    @Override
    public String print(int indent) {
        if(kind == RuleKind.Lexer) {
            String comment = "";
            if(LexerMode != null) {
                comment = "/** @mode " + LexerMode + "\n";
            }
            String fragment = "terminal ";
            if(modifiers.contains(RuleModifier.fragment)) {
                fragment += "fragment ";
            }
            if(modifiers.contains(RuleModifier.hidden)) {
                fragment = "hidden " + fragment;
            }
            return comment + fragment + getLangiumName() + ": "+body.print(0)+";";
        } else {
            String entry = "";
            if(modifiers.contains(RuleModifier.entry)) {
                entry = "entry ";
            }            
            return entry + getLangiumName() + ": "+body.print(0) + ";";
        }
    }
}
