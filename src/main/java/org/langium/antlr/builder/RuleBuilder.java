package org.langium.antlr.builder;

import org.langium.antlr.model.Rule;

public interface RuleBuilder {
    RuleBuilder name(String name);
    Rule end();
}
