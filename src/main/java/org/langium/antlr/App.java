package org.langium.antlr;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.antlr.v4.Tool;
import org.langium.antlr.model.Grammar;

public class App extends Tool {
    public static void main(String[] args) {
        if (args.length != 2) {
            //System.err.println("Usage: java -jar langium-antlr-transpiler.jar <antlr4-grammar-file>");
            //System.exit(1);
            args = new String[2];
            args[0] = "JSON.g4";
            args[1] = "JSON.langium";
        }
        App app = new App();
        String antlrFileName = args[0];
        String langiumFileName = args[1];
        try {
            org.antlr.v4.tool.Grammar grammar = app.loadGrammar(antlrFileName);
            String content = app.toLangium2(grammar);
            FileWriter fileWriter = new FileWriter(langiumFileName);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print(content);
            printWriter.close();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    public String toLangium(org.antlr.v4.tool.Grammar grammar) {
        LangiumGeneratingVisitor visitor = new LangiumGeneratingVisitor(grammar);
        grammar.ast.visit(visitor);
        grammar.implicitLexer.ast.visit(visitor);
        return "grammar "+grammar.name + LangiumGeneratingVisitor.NL + LangiumGeneratingVisitor.NL
            + visitor.toString()    
            ;
    }

     public String toLangium2(org.antlr.v4.tool.Grammar grammar) {
        LangiumGeneratingVisitor2 visitor = new LangiumGeneratingVisitor2();
        Grammar lexer = visitor.generate(grammar.implicitLexer.ast, null);
        Grammar parser = visitor.generate(grammar.ast, new Grammar[] { lexer });
        return lexer.print(0)+"\n----------\n"+parser.print(0);
    }
}
