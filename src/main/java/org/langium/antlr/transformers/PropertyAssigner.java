package org.langium.antlr.transformers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.langium.antlr.NameSplitter;
import org.langium.antlr.Utilities;
import org.langium.antlr.model.AlternativesRuleExpression;
import org.langium.antlr.model.Grammar;
import org.langium.antlr.model.RuleCallExpression;
import org.langium.antlr.model.RuleExpression;
import org.langium.antlr.model.RuleKind;

public class PropertyAssigner implements Transformer {
    private static final NameSplitter NameSplitter = new NameSplitter();

    @Override
    public boolean canTransform(Grammar grammar) {
        return grammar.grammarKind == RuleKind.Parser;
    }

    @Override
    public void transform(Grammar grammar) {
        Utilities.streamAst(grammar).filter(i -> i instanceof AlternativesRuleExpression).forEach(r -> {
            AlternativesRuleExpression alternatives = (AlternativesRuleExpression) r;
            for (RuleExpression child : alternatives.children) {
                LinkedList<RuleCallExpression> ruleCalls = new LinkedList<RuleCallExpression>();
                Map<String, List<RuleCallExpression>> calls = new HashMap<String, List<RuleCallExpression>>();
                Map<String, String[]> callNames = new HashMap<String, String[]>();

                //determine names
                Utilities.streamAst(child).filter(i -> i instanceof RuleCallExpression).forEach(i -> {
                    RuleCallExpression ruleCall = (RuleCallExpression) i;
                    if(!calls.containsKey(ruleCall.ruleName)) {
                        calls.put(ruleCall.ruleName, new LinkedList<RuleCallExpression>());
                    }
                    ruleCalls.add(ruleCall);
                    calls.get(ruleCall.ruleName).add(ruleCall);
                    callNames.put(ruleCall.ruleName, NameSplitter.splitName(ruleCall.ruleName));
                });

                //compute unique names
                Map<String, Integer> maxLengths = new HashMap<String, Integer>();
                Map<String, Integer> lengths = new HashMap<String, Integer>();
                callNames.forEach((longName,names) -> {
                    maxLengths.put(longName, names.length);
                    lengths.put(longName, 1);
                });

                Map<String, String> variables;
                boolean foundCollision;
                do {
                    foundCollision = false;
                    final Map<String, List<String>> collisions = new HashMap<String, List<String>>();
                    variables = callNames
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                            e -> e.getKey(),
                            e -> {
                                String[] names = e.getValue();
                                Integer length = lengths.get(e.getKey());
                                String name = "";
                                while(length>0){
                                    name = names[names.length - length] + Utilities.capitalize(name);
                                    length--;
                                }
                                if(!collisions.containsKey(name)) {
                                    collisions.put(name, new LinkedList<String>());
                                }
                                collisions.get(name).add(e.getKey());
                                return name;
                            }
                        ));
                        if(collisions.size() < variables.size()) {
                            foundCollision = true;
                            for (Map.Entry<String, List<String>> e : collisions.entrySet()) {
                                if(e.getValue().size() > 1) {
                                    e.getValue().forEach(longName -> {
                                        lengths.put(longName, Math.min(maxLengths.get(longName), lengths.get(longName)+1));
                                    });
                                    break;
                               }
                            }
                        }
                } while(foundCollision);

                //assign names
                for (RuleCallExpression ruleCall : ruleCalls) {
                    System.out.println("assigning "+ruleCall.ruleName+" to "+variables.get(ruleCall.ruleName));
                    ruleCall.assignmentVariable = variables.get(ruleCall.ruleName);                    
                }
            }
        });
    }

}
