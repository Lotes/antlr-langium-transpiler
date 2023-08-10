package org.langium.antlr.model;

import java.util.Collection;

public class Grammar implements Printable {
    public final String name;
    public final Iterable<Rule> rules;
    public final RuleKind grammarKind;
    public final Collection<Grammar> imports;
    public final NamingService namingService;

    public Grammar(NamingService namingService, String name, Iterable<Rule> rules, RuleKind grammarKind, Collection<Grammar> imports) {
        this.name = name;
        this.rules = rules;
        this.grammarKind = grammarKind;
        this.imports = imports;
        this.namingService = namingService;
    }

    @Override
    public String print(int indent) {
        StringBuilder sb = new StringBuilder();
        if (grammarKind == RuleKind.Parser) {
            sb.append("grammar ").append(name).append("\n\n");
        }
        if (!imports.isEmpty()) {
            for (Grammar string : imports) {
                sb.append("import \"").append(string.name).append("\"\n");
            }
            sb.append("\n");
        }
        for (Rule rule : rules) {
            sb.append(rule.print(indent)).append("\n\n");
        }
        return sb.toString();
    }
}
