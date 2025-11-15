package com.gabrieljamesbenedict.Interpreter;

import java.util.HashSet;

public class SymbolTable {

    private final HashSet<Symbol> table = new HashSet<>();


    public boolean add(Symbol symbol) {
        boolean alreadyExists = table.stream().map(Symbol::getName).toList().contains(symbol.getName());
        if (alreadyExists) throw new RuntimeException("Symbol already exists: " + symbol.getName());
        return table.add(symbol);
    }


    public boolean exists(Symbol symbol) {
        return table.contains(symbol);
    }


    public Symbol lookup(String name) {
        for (Symbol s : table) {
            if (s.getName().equals(name)) {
                return s;
            }
        }
        return null;
    }


    public boolean remove(Symbol symbol) {
        return table.remove(symbol);
    }

    public boolean removeByName(String name) {
        Symbol s = lookup(name);
        if (s != null) {
            return table.remove(s);
        }
        return false;
    }


    public int size() {
        return table.size();
    }


    public void clear() {
        table.clear();
    }


    public void printAll() {
        for (Symbol s : table) {
            System.out.println(s);
        }
    }
}

