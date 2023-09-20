package org.langium.antlr.transformers;

import org.langium.antlr.Utilities;
import org.langium.antlr.model.Grammar;
import org.langium.antlr.model.RuleCallExpression;
import org.langium.antlr.model.RuleKind;

public class PropertyAssigner implements Transformer {
    private static final VariableNameExtractor variableNameExtractor = new VariableNameExtractor();

    @Override
    public boolean canTransform(Grammar grammar) {
        return grammar.grammarKind == RuleKind.Parser;
    }

    @Override
    public void transform(Grammar grammar) {
        Utilities.streamAst(grammar).filter(i -> i instanceof RuleCallExpression).forEach(r -> {
            RuleCallExpression ruleCall = (RuleCallExpression) r;
            ruleCall.assignmentVariable = variableNameExtractor.extractVariableName(ruleCall.ruleName);
        });
    }

}
