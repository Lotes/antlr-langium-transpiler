package org.langium.antlr.model;

import java.util.LinkedList;
import java.util.List;

public class ActionRuleExpression implements Printable, LangiumAST {
    public static class CurrentAssignment {
        public final String propertyName;
        public final AssigmentOperator assigmentOperator;
        public CurrentAssignment(String propertyName, AssigmentOperator assigmentOperator) {
            this.propertyName = propertyName;
            this.assigmentOperator = assigmentOperator;
        }
    }
    public final String subTypeName;
    public final CurrentAssignment currentAssignment;
    public ActionRuleExpression(String subTypeName, CurrentAssignment currentAssignment) {
        this.subTypeName = subTypeName;
        this.currentAssignment = currentAssignment;
    }
    @Override
    public String print(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append("{infers "+subTypeName);
        if(currentAssignment != null) {
            sb.append("."+currentAssignment.propertyName);
            sb.append(" "+currentAssignment.assigmentOperator.getSymbol());
            sb.append(" current");
        }
        sb.append("}");
        return sb.toString();
    }
    @Override
    public List<LangiumAST> getChildren() {
        return new LinkedList<LangiumAST>();
    }
    @Override
    public int removeChild(LangiumAST child) {
        throw new UnsupportedOperationException("Unimplemented method 'removeChild'");
    }
    @Override
    public void insertChild(LangiumAST child, int index) {
        throw new UnsupportedOperationException("Unimplemented method 'insertChild'");
    }
    @Override
    public int replaceChild(LangiumAST oldChild, LangiumAST newChild) {
        throw new UnsupportedOperationException("Unimplemented method 'replaceChild'");
    }
}
