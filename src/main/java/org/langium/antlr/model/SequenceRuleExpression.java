package org.langium.antlr.model;

import java.util.List;
import java.util.stream.Collectors;

public class SequenceRuleExpression extends RuleExpression {
    public final List<RuleExpression> children;

    public SequenceRuleExpression(List<RuleExpression> children) {
        this.children = children;
    }

    public void insertExpression(RuleExpression expression, int index) {
        this.children.add(index, expression);
    }

    @Override
    public String print(final int indent) {
        return this.children.stream().map(c -> c.print(0)).collect(Collectors.joining(" "));
    }

    @Override
    public List<LangiumAST> getChildren() {
        return this.children.stream().map(i -> (LangiumAST) i).toList();
    }

    @Override
    public int removeChild(LangiumAST child) {
        int index = children.indexOf(child);
        children.remove(child);
        child.setParent(null);
        return index;
    }

    @Override
    public void insertChild(LangiumAST child, int index) {
        children.add(index, (RuleExpression) child);
        child.setParent(this);
    }

    @Override
    public int replaceChild(LangiumAST oldChild, LangiumAST newChild) {
        int index = children.indexOf(oldChild);
        children.set(index, (RuleExpression) newChild);
        oldChild.setParent(null);
        newChild.setParent(this);
        return index;
    }
}