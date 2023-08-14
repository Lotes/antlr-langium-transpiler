package org.langium.antlr.model;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class Grammar implements Printable, LangiumAST {
    public final String name;
    public final Collection<Rule> rules;
    public final RuleKind grammarKind;
    public final Collection<Grammar> imports;
    public final NamingService namingService;

    public Grammar(NamingService namingService, String name, Collection<Rule> rules, RuleKind grammarKind, Collection<Grammar> imports) {
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
                System.out.println("> importing "+string);
                sb.append("import \"").append(string.name).append("\"\n");
            }
            sb.append("\n");
        }
        for (Rule rule : rules) {
            sb.append(rule.print(indent)).append("\n\n");
        }
        return sb.toString();
    }

    @Override
    public List<LangiumAST> getChildren() {
        return Stream.concat(imports.stream(), rules.stream()).map(i -> (LangiumAST)i).toList();
    }
}
