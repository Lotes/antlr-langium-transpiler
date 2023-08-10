package org.langium.antlr.model;

public enum QuantifierKind {
    Optional('?'),
    ZeroOrMore('*'),
    OneOrMore('+');

    private char symbol;
    QuantifierKind(char symbol) {
        this.symbol = symbol;
    }
    public char getSymbol() {
        return symbol;
    }
}
