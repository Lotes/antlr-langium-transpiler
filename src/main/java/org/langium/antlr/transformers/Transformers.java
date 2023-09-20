package org.langium.antlr.transformers;

import org.langium.antlr.model.NamingService;

public class Transformers {
    public static final Transformer[] createAll(NamingService namingService) {
        return new Transformer[] {
            new UnicodeLiteralsSplitter(),
            new PropertyAssigner(),
        };
    };
}
