package org.langium.antlr.builder;

import java.util.Collection;

import org.langium.antlr.model.Rule;
import org.langium.antlr.model.RuleExpression;
import org.langium.antlr.model.RuleModifier;

public interface RuleBuilder {
    RuleBuilder name(String name);
    RuleBuilder body(RuleExpression body);
    Rule end();
    RuleBuilder modifiers(Collection<RuleModifier> modifiers);
    RuleBuilder setHidden(boolean b);
}
