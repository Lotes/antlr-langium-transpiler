package org.langium.antlr;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameSplitter {
    public String[] splitName(String longName) {
        Pattern pattern;
        if (longName.matches(".*[a-z].*")) {
            pattern = Pattern.compile("[A-Z_][a-z0-9_]*|^[a-z][a-z0-9_]*");
        } else {
            pattern = Pattern.compile("[A-Z][A-Z0-9]*");
        }
        Matcher matcher = pattern.matcher(longName);
        LinkedList<String> names = new LinkedList<String>();
        while (matcher.find()) {
            String name = longName.substring(matcher.start(), matcher.end()).toLowerCase();
            names.add(name);
        }
        return names.toArray(new String[0]);
    }
}
