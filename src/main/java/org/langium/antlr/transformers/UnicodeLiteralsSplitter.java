package org.langium.antlr.transformers;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.langium.antlr.Utilities;
import org.langium.antlr.model.Grammar;
import org.langium.antlr.model.KeywordExpression;
import org.langium.antlr.model.RangeExpression;
import org.langium.antlr.model.RuleKind;

public class UnicodeLiteralsSplitter implements Transformer {
    private Predicate<String> UnicodePattern = Pattern.compile("^\\\\u[0-9A-Fa-f]{4}$").asPredicate();

    private static class CodePoint {
        public final byte high;
        public CodePoint(byte high, byte low) {
            this.high = high;
            this.low = low;
        }
        public final byte low;
    }


    @Override
    public boolean canTransform(Grammar grammar) {
        return grammar.grammarKind ==  RuleKind.Lexer;
    }

    @Override
    public void transform(Grammar grammar) {
        Utilities.streamAst(grammar).filter(i -> i.child instanceof RangeExpression).forEach(r -> {
            RangeExpression range = (RangeExpression) r.child;
            if(!(range.left instanceof KeywordExpression) || !(range.right instanceof KeywordExpression)) {
                return;
            }
            var left = ((KeywordExpression)range.left).text;
            var right = ((KeywordExpression)range.right).text;

            if(UnicodePattern.test(left) && UnicodePattern.test(right)) {
                //var from = splitUnicode(left);
               // var to = splitUnicode(right);
                
            }
        });
    }
    
    CodePoint splitUnicode(String unicode) {
        var high = Integer.parseInt(unicode.substring(2, 2), 16);
        var low = Integer.parseInt(unicode.substring(2, 2), 16);
        return new CodePoint((byte)high, (byte)low);
    }
}
