package org.langium.antlr;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.antlr.v4.Tool;

public class App extends Tool {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println(
                    "Usage: java -jar langium-antlr-transpiler.jar <antlr4-grammar-file> <langium-grammar-folder>");
            System.exit(1);
            return;
        }
        App app = new App();
        String antlrGrammarFile = args[0];
        String langiumGrammarFolder = args[1];
        try {
            org.antlr.v4.tool.Grammar grammar = app.loadGrammar(antlrGrammarFile);
            LangiumGeneratingVisitor visitor = new LangiumGeneratingVisitor(Path.of(grammar.fileName).getParent().toString());
            var files = visitor.toLangium(grammar);
            for (GrammarFile file : files) {
                var path = Paths.get(langiumGrammarFolder, file.name);
                Files.writeString(path, file.content);
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
