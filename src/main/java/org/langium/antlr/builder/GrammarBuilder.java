package org.langium.antlr.builder;

import org.langium.antlr.model.Grammar;
import org.langium.antlr.model.RuleKind;

public interface GrammarBuilder {
    GrammarBuilder name(String name);
    RuleBuilder beginRule(RuleKind kind);
    GrammarBuilder importing(Grammar grammar);
    Grammar build();
}