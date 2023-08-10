package org.langium.antlr;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
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
        AST2XMLGenerator xmlGenerator = new AST2XMLGenerator();
        LangiumGeneratingVisitor visitor = new LangiumGeneratingVisitor();
        List<GrammarFile> result = new LinkedList<GrammarFile>();
        List<Grammar> grammars = new LinkedList<Grammar>();
        if(grammar.implicitLexer.ast != null) {
            result.add(new GrammarFile(grammar.name+".lexer.xml", xmlGenerator.generate(grammar.implicitLexer.ast)));
            try {
                var lexerGrammar = visitor.generate(grammar.implicitLexer.ast, null);
                grammars.add(lexerGrammar);
                result.add(new GrammarFile(lexerGrammar.name+".langium", lexerGrammar.print(0)));
            } catch (Exception e) {
                result.add(new GrammarFile(grammar.name+".lexer.error", e.getMessage()+"\n"+e.getStackTrace()));
            }
        }
        result.add(new GrammarFile(grammar.name+".parser.xml", xmlGenerator.generate(grammar.ast)));
        try {
            Grammar parserGrammar = visitor.generate(grammar.ast, grammars);
            result.add(new GrammarFile(parserGrammar.name+".langium", parserGrammar.print(0)));    
        } catch (Exception e) {
            result.add(new GrammarFile(grammar.name+".parser.error", e.getMessage()+"\n"+e.getStackTrace()));
        }
        return result.toArray(new GrammarFile[0]);
    }
}
