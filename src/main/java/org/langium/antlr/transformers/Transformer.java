package org.langium.antlr.transformers;

import org.langium.antlr.model.Grammar;

public interface Transformer {
    boolean canTransform(Grammar grammar);
    void transform(Grammar grammar);
}
