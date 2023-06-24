package org.langium.antlr;

import org.antlr.v4.Tool;

public class App extends Tool
{
    public static void main( String[] args )
    {
        if (args.length != 1) {
            System.err.println("Usage: java -jar langium-antlr-transpiler.jar <antlr4-grammar-file>");
            System.exit(1);
        }
        App app = new App();
        app.loadGrammar(args[0]);
    }
}
