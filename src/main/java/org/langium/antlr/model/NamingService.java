package org.langium.antlr.model;

import java.util.Collection;

public interface NamingService {
    String add(String originalName, String actualName);
    String get(String originalName);
    boolean has(String originalName);
    Collection<String> allNames();
}
