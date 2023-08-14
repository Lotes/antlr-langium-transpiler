package org.langium.antlr.builder;

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
    private String modeName = null;

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
        var rule = new Rule(parent.namingService, kind, name, body, modifiers);
        rule.LexerMode = modeName;
        parent.rules.add(rule);
        return rule;
    }

    @Override
    public RuleBuilder body(RuleExpression body) {
        this.body = body;
        return this;
    }

    @Override
    public RuleBuilder setModifier(RuleModifier modifier) {
        this.modifiers.add(modifier);
        return this;
    }

    @Override
    public RuleBuilder mode(String modeName) {
        this.modeName = modeName;
        return this;
    }
}
