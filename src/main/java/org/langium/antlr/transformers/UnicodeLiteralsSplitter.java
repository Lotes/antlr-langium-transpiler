package org.langium.antlr.transformers;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.langium.antlr.Utilities;
import org.langium.antlr.model.Grammar;
import org.langium.antlr.model.KeywordExpression;
import org.langium.antlr.model.LangiumAST;
import org.langium.antlr.model.RangeExpression;
import org.langium.antlr.model.RegexRuleExpression;
import org.langium.antlr.model.RuleKind;

public class UnicodeLiteralsSplitter implements Transformer {
    private Predicate<String> UnicodePattern = Pattern.compile("^\\\\u[0-9A-Fa-f]{4}$").asPredicate();

    public static class ReplacementAction {
        public final LangiumAST parent;
        public final LangiumAST affectedChild;
        public final List<LangiumAST> replacement;
        public ReplacementAction(LangiumAST parent, LangiumAST affectedChild, List<LangiumAST> replacement) {
            this.parent = parent;
            this.affectedChild = affectedChild;
            this.replacement = replacement;
        }
    }

    @Override
    public boolean canTransform(Grammar grammar) {
        return grammar.grammarKind == RuleKind.Lexer;
    }

    @Override
    public void transform(Grammar grammar ) {
       Utilities.streamAst(grammar).filter(i -> i instanceof RangeExpression).forEach(r -> {
            RangeExpression range = (RangeExpression) r;
            if (!(range.left instanceof KeywordExpression) || !(range.right instanceof KeywordExpression)) {
                return;
            }
            var left = ((KeywordExpression) range.left).text;
            var right = ((KeywordExpression) range.right).text;

            if (UnicodePattern.test(left) && UnicodePattern.test(right)) {
                var regex = new RegexRuleExpression("["+left+"-"+right+"]", "u");
                Utilities.replace(r, List.of(regex));
            }
        });
    }
}
