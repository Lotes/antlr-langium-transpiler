package org.langium.antlr.model;

import java.util.LinkedList;
import java.util.List;

public class RegexRuleExpression extends RuleExpression {
    public final String pattern;

    public RegexRuleExpression(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String print(int indent) {
        return "/"+pattern+"/";
    }

   @Override
    public List<LangiumAST> getChildren() {
        return new LinkedList<LangiumAST>();
    }
}
