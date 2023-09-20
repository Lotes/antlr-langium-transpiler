package org.langium.antlr;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;
import org.langium.antlr.transformers.NameSplitter;

public class NameSplitterTest {
    private static final NameSplitter splitter = new NameSplitter();

    @Test
    public void test() {
        check("ATOM_ROFL_MAO",new String[]{"atom", "rofl", "mao"});
        check("SINGLE", new String[]{"single"});
        check("Single", new String[]{"single"});
        check("single", new String[]{"single"});
        check("GameStateManager", new String[]{"game","state","manager"});
    }

    private void check(String original, String[] expected) {
        assertArrayEquals(expected, splitter.splitName(original));
    }
}
