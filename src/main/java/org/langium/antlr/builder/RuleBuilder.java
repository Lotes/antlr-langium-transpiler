package org.langium.antlr.builder;

import org.langium.antlr.model.Rule;
import org.langium.antlr.model.RuleExpression;
import org.langium.antlr.model.RuleModifier;

public interface RuleBuilder {
    RuleBuilder name(String name);
    RuleBuilder body(RuleExpression body);
    Rule end();
    RuleBuilder modifiers(Iterable<RuleModifier> modifiers);
}
