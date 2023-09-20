package org.langium.antlr.model;

import java.util.LinkedList;
import java.util.List;

public class RegexRuleExpression extends RuleExpression {
    public final String pattern;
    public final String flags;

    public RegexRuleExpression(String pattern, String flags) {
        this.pattern = pattern;
        this.flags = flags;
    }

     public RegexRuleExpression(String pattern) {
        this.pattern = pattern;
        this.flags = null;
    }

    @Override
    public String print(int indent) {
        return "/"+pattern+"/"+flags != null ? flags : "";
    }

   @Override
    public List<LangiumAST> getChildren() {
        return new LinkedList<LangiumAST>();
    }
}
