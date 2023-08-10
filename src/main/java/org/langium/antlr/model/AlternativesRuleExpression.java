package org.langium.antlr.model;

import java.util.Collection;
import java.util.stream.Collectors;

public class AlternativesRuleExpression extends RuleExpression {
    public final Collection<RuleExpression> children;
    public final boolean isRoot;

    public AlternativesRuleExpression(Collection<RuleExpression> children, boolean isRoot) {
        this.children = children;
        this.isRoot = isRoot;
    }

    @Override
    public String print(final int indent) {
        return this.children.stream().map(c -> c.print(indent+1)).collect(Collectors.joining(Utilities.indent(indent+1)+(isRoot? "\n\t| " : " | ")));
    }
}