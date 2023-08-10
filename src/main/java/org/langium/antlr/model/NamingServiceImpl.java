package org.langium.antlr.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NamingServiceImpl implements NamingService {
    private Map<String, String> nameMap = new HashMap<String, String>();

    @Override
    public String add(String originalName, String actualName) {
        if(has(originalName))
            return get(originalName);
        else {
            nameMap.put(originalName, actualName);
            return actualName;
        }
    }

    @Override
    public String get(String originalName) {
        var firstGuess = nameMap.get(originalName);
        if(firstGuess == null) {
            throw new RuntimeException("Name not found: " + originalName);
        }
        return firstGuess;
    }

    @Override
    public boolean has(String originalName) {
        return nameMap.containsKey(originalName);
    }

    @Override
    public Collection<String> allNames() {
        return nameMap.keySet();
    }

}
