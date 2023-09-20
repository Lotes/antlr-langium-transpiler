package org.langium.antlr.transformers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableNameExtractor {
    public String extractVariableName(String longName) {
        Pattern pattern;
        if (longName.matches(".*[a-z].*")) {
            pattern = Pattern.compile("[A-Z_][a-z0-9_]*|^[a-z][a-z0-9_]*");
        } else {
            pattern = Pattern.compile("[A-Z][A-Z0-9]*");
        }
        Matcher matcher = pattern.matcher(longName);
        String last = null;
        while (matcher.find()) {
            last = longName.substring(matcher.start(), matcher.end()).toLowerCase();
        }
        return last;
    }
}
