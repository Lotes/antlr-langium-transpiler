package org.langium.antlr.builder;

import org.langium.antlr.model.Rule;
import org.langium.antlr.model.RuleKind;

public class RuleBuilderImpl implements RuleBuilder {
    private String name;
    private GrammarBuilderImpl parent;
    private RuleKind kind;

    public RuleBuilderImpl(GrammarBuilderImpl parent, RuleKind kind) {
        this.parent = parent;
        this.kind = kind;
    }

    public RuleBuilder name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Rule end() {
        var rule = new Rule(kind, name);
        parent.rules.add(rule);
        return rule;
    }

}
