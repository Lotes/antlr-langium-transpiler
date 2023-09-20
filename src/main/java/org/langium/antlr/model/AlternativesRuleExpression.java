package org.langium.antlr.model;

import java.util.List;
import java.util.stream.Collectors;

import org.langium.antlr.Utilities;

public class AlternativesRuleExpression extends RuleExpression {
    public final List<RuleExpression> children;
    public final boolean isRoot;

    public AlternativesRuleExpression(List<RuleExpression> children, boolean isRoot) {
        this.children = children;
        this.isRoot = isRoot;
    }

    @Override
    public String print(final int indent) {
        return this.children.stream().map(c -> c.print(indent+1)).collect(Collectors.joining(Utilities.indent(indent+1)+(isRoot? "\n\t| " : " | ")));
    }

    @Override
    public List<LangiumAST> getChildren() {
        return children.stream().map(m -> (LangiumAST)m).toList();
    }
    @Override
    public int removeChild(LangiumAST child) {
        int index = children.indexOf(child);
        children.remove(child);
        return index;
    }
    @Override
    public void insertChild(LangiumAST child, int index) {
        children.add(index, (RuleExpression)child);
    }
    @Override
    public int replaceChild(LangiumAST oldChild, LangiumAST newChild) {
        int index = children.indexOf(oldChild);
        children.set(index, (RuleExpression)newChild);
        oldChild.setParent(null);
        newChild.setParent(this);
        return index;
    }
}