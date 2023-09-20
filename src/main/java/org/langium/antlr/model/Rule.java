package org.langium.antlr.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.langium.antlr.Utilities;

public class Rule extends AbstractLangiumAST implements Printable {
    public final String name;
    public final RuleKind kind;
    public RuleExpression body;
    public final Collection<RuleModifier> modifiers;
    public final String mode;
    public final ModeAction action = null;
    private NamingService namingService;
    public String getLangiumName() {
        return namingService.get(name);
    }

    public Rule(NamingService namingService, RuleKind kind, String name, RuleExpression body, Collection<RuleModifier> modifiers) {
        this(namingService, kind, name, body, modifiers, null);
    }

    public Rule(NamingService namingService, RuleKind kind, String name, RuleExpression body, Collection<RuleModifier> modifiers, String mode) {
        this.namingService = namingService;
        this.name = name;
        this.kind = kind;
        this.body = body;
        this.modifiers = modifiers;
        this.mode = mode;
        String actualName = buildName(name);
        namingService.add(name, actualName);
    }

    private String buildName(String name) {
        if(kind == RuleKind.Parser) {
            return Utilities.capitalize(name);
        }
        var upper = Utilities.toUpperCase(name);
        if(modifiers.contains(RuleModifier.fragment)) {
            return "_" + upper;
        } 
        if(mode != null) {
            return mode + "__" + upper;
        }
        return upper;
    }

    @Override
    public String print(int indent) {
        if(kind == RuleKind.Lexer) {
            String comment = "";
            if(mode != null) {
                comment = "/** @mode " + mode + " */\n";
            }
            String fragment = "terminal ";
            if(modifiers.contains(RuleModifier.fragment)) {
                fragment += "fragment ";
            }
            if(modifiers.contains(RuleModifier.hidden)) {
                fragment = "hidden " + fragment;
            }
            return comment + fragment + getLangiumName() + ": "+body.print(0)+";";
        } else {
            String entry = "";
            if(modifiers.contains(RuleModifier.entry)) {
                entry = "entry ";
            }            
            return entry + getLangiumName() + ": "+body.print(0) + ";";
        }
    }

   @Override
    public List<LangiumAST> getChildren() {
        var list = new LinkedList<LangiumAST>();
        list.add(body);
        return list;
    }

    @Override
    public int replaceChild(LangiumAST oldChild, LangiumAST newChild) {
        if(newChild instanceof RuleExpression) {
            if(oldChild == body) {
                body = (RuleExpression) newChild;
                oldChild.setParent(null);
                newChild.setParent(this);
                return 0;
            }
        }
        return super.replaceChild(oldChild, newChild);
    }
}
