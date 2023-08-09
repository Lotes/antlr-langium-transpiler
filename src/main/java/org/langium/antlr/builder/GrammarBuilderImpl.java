package org.langium.antlr.builder;

import java.util.ArrayList;
import java.util.List;

import org.langium.antlr.model.Grammar;
import org.langium.antlr.model.Rule;
import org.langium.antlr.model.RuleKind;

public class GrammarBuilderImpl implements GrammarBuilder {

    private String name;
    public final List<Rule> rules = new ArrayList<Rule>();

    @Override
    public GrammarBuilder name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Grammar build() {
        if(name == null) {
            throw new IllegalStateException("Grammar name missing");
        }
        return new Grammar(name, rules);
    }

    @Override
    public RuleBuilder beginRule(RuleKind kind) {
        return new RuleBuilderImpl(this, kind);
    }
}