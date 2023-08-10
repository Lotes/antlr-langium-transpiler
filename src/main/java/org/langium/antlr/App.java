package org.langium.antlr;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.antlr.v4.Tool;
import org.langium.antlr.model.Grammar;

public class App extends Tool {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java -jar langium-antlr-transpiler.jar <antlr4-grammar-file> <langium-grammar-folder>");
            System.exit(1);
            return;
        }
        App app = new App();
        String antlrGrammarFile = args[0];
        String langiumGrammarFolder = args[1];
        try {
            org.antlr.v4.tool.Grammar grammar = app.loadGrammar(antlrGrammarFile);
            var files = app.toLangium(grammar);
            for (GrammarFile file : files) {
                var path = Paths.get(langiumGrammarFolder, file.name);
                Files.writeString(path, file.content);
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

     public GrammarFile[] toLangium(org.antlr.v4.tool.Grammar grammar) {
        LangiumGeneratingVisitor visitor = new LangiumGeneratingVisitor();
        List<GrammarFile> result = new LinkedList<GrammarFile>();
        List<Grammar> grammars = new LinkedList<Grammar>();
        if(grammar.implicitLexer.ast != null) {
            var lexer = visitor.generate(grammar.implicitLexer.ast, null);
            grammars.add(lexer);
            result.add(new GrammarFile(lexer.name+".langium", lexer.print(0)));
        }
        Grammar parser = visitor.generate(grammar.ast, grammars);
        result.add(new GrammarFile(parser.name+".langium", parser.print(0)));
        return result.toArray(new GrammarFile[0]);
    }
}
