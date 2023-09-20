package org.langium.antlr;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.langium.antlr.transformers.VariableNameExtractor;

public class VariableNameExtractorTest {
    private static final VariableNameExtractor variableNameExtractor = new VariableNameExtractor();

    @Test
    public void test() {
        check("ATOM_ROFL_MAO","mao");
        check("SINGLE", "single");
        check("Single", "single");
        check("single", "single");
        check("GameStateManager", "manager");
    }

    private void check(String original, String expected) {
        assertEquals(expected, variableNameExtractor.extractVariableName(original));
    }
}
