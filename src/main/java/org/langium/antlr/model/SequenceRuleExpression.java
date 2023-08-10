package org.langium.antlr.model;

import java.util.Collection;
import java.util.stream.Collectors;

public class SequenceRuleExpression extends RuleExpression {
    public final Collection<RuleExpression> children;

    public SequenceRuleExpression(Collection<RuleExpression> children) {
        this.children = children;
    }

    @Override
    public String print(final int indent) {
        return this.children.stream().map(c -> c.print(0)).collect(Collectors.joining(" "));
    }
}