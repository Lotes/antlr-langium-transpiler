package org.langium.antlr.builder;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.langium.antlr.model.Rule;
import org.langium.antlr.model.RuleExpression;
import org.langium.antlr.model.RuleKind;
import org.langium.antlr.model.RuleModifier;

public class RuleBuilderImpl implements RuleBuilder {
    private String name;
    private GrammarBuilderImpl parent;
    private RuleKind kind;
    private RuleExpression body;
    private List<RuleModifier> modifiers = new LinkedList<RuleModifier>();

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
        var rule = new Rule(kind, name, body, modifiers);
        parent.rules.add(rule);
        return rule;
    }

    @Override
    public RuleBuilder body(RuleExpression body) {
        this.body = body;
        return this;
    }

    @Override
    public RuleBuilder modifiers(Collection<RuleModifier> modifiers) {
        this.modifiers.addAll(modifiers);
        return this;
    }

    @Override
    public RuleBuilder setHidden(boolean hidden) {
        if(hidden) {
            this.modifiers.add(RuleModifier.hidden);
        } else {
            this.modifiers.remove(RuleModifier.hidden);
        }
        return this;
    }

}
