package compiler;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    public HashMap<String, String> variableEntries;

    public SymbolTable() {
        this.variableEntries = new HashMap<>();
    }

    public void addVariableEntry (String identifier, String type) {
        this.variableEntries.put(identifier, type);
    }

    public String checkVariableType(String identifier) {
        return this.variableEntries.get(identifier);
    }
};