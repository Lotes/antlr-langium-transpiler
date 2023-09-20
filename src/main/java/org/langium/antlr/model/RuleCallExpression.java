package org.langium.antlr.model;

import java.util.LinkedList;
import java.util.List;

public class RuleCallExpression extends RuleExpression {
    public final String ruleName;
    
    public AssigmentOperator assignmentOperator = AssigmentOperator.Single;
    public String assignmentVariable = null;

    private NamingService namingService;

    public RuleCallExpression(NamingService namingService, String ruleName) {
        this.namingService = namingService;
        this.ruleName = ruleName;
    }

    @Override
    public String print(int indent) {
        String assignment = "";
        if(assignmentVariable != null) {
            assignment = assignmentVariable + assignmentOperator.getSymbol();
        }
        return assignment+namingService.get(ruleName);
    }

    @Override
    public List<LangiumAST> getChildren() {
        return new LinkedList<LangiumAST>();
    }
}
