package org.langium.antlr.model;

import java.util.LinkedList;
import java.util.List;

public class ModeAction implements LangiumAST {
    public final ModeActionKind kind;
    public final String mode;
    public final String type;

    public static ModeAction PopMode() {
        return new ModeAction(ModeActionKind.PopMode, null, null);
    }
    public static ModeAction PushMode(String mode) {
        return new ModeAction(ModeActionKind.PushMode, mode, null);
    }
    public static ModeAction PushMode(String mode, String type) {
        return new ModeAction(ModeActionKind.PushMode, mode, type);
    }
    
    public static ModeAction PushMode = new ModeAction(ModeActionKind.PopMode, null, null);


    private ModeAction(ModeActionKind kind, String mode, String type) {
        this.kind = kind;
        this.mode = mode;
        this.type = type;
    }

    @Override
    public List<LangiumAST> getChildren() {
        return new LinkedList<LangiumAST>();
    }
}
