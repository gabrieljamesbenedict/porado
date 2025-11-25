package com.gabrieljamesbenedict.porado.node;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Node {

    NodeType type;
    String text = "";

    public void printNode(int level) {
        throw new UnsupportedOperationException("Error: Not yet implemented printNode method");
    }

    protected String printIndent(int level) {
        String indentSymbol = ">";
        int indentMultiplier = 2;
        return indentSymbol.repeat(level * indentMultiplier);
    }
}
