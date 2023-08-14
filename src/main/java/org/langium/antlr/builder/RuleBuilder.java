package org.langium.antlr.builder;

import org.langium.antlr.model.Rule;
import org.langium.antlr.model.RuleExpression;
import org.langium.antlr.model.RuleModifier;

public interface RuleBuilder {
    RuleBuilder name(String name);
    RuleBuilder body(RuleExpression body);
    RuleBuilder mode(String modeName);
    RuleBuilder setModifier(RuleModifier modifier);
    Rule end();
}
