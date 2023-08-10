package org.langium.antlr.model;

public enum AssigmentOperator {
    Single("="),
    Multiple("+="),
    Flag("?=");

    private String symbol;
    AssigmentOperator(String symbol) {
        this.symbol = symbol;
    }
    public String getSymbol() {
        return symbol;
    }
}
