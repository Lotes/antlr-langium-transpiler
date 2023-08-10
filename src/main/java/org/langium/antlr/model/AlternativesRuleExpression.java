package org.langium.antlr.model;

import java.util.Collection;
import java.util.stream.Collectors;

public class AlternativesRuleExpression extends RuleExpression {
    public final Collection<RuleExpression> children;

    public AlternativesRuleExpression(Collection<RuleExpression> children) {
        super();
        this.children = children;
    }

    @Override
    public String print(final int indent) {
        return this.children.stream().map(c -> c.print(indent+1)).collect(Collectors.joining("\n| "));
    }
}