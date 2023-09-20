package org.langium.antlr.model;

import java.util.List;
import java.util.stream.Stream;

public class Grammar extends AbstractLangiumAST implements Printable {
    public final String name;
    public final List<Rule> rules;
    public final RuleKind grammarKind;
    public final List<Grammar> imports;
    public final NamingService namingService;

    public Grammar(NamingService namingService, String name, List<Rule> rules, RuleKind grammarKind, List<Grammar> imports) {
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

    @Override
    public int removeChild(LangiumAST child) {
        if(child instanceof Rule) {
            var index = rules.indexOf(child);
            rules.remove(child);
            return index;
        } else {
            var index = imports.indexOf(child);
            imports.remove(child);
            return index;
        }
    }

    @Override
    public void insertChild(LangiumAST child, int index) {
        if(child instanceof Rule) {
            rules.add(index, (Rule) child);
        } else {
            imports.add(index, (Grammar) child);
        }
    }

    @Override
    public int replaceChild(LangiumAST oldChild, LangiumAST newChild) {
        if(oldChild instanceof Rule) {
            var index = rules.indexOf(oldChild);
            rules.set(index, (Rule) newChild);
            oldChild.setParent(null);
            newChild.setParent(this);
            return index;
        } else {
            var index = imports.indexOf(oldChild);
            imports.set(index, (Grammar) newChild);
            oldChild.setParent(null);
            newChild.setParent(this);
            return index;
        }
    }
}
