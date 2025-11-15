package com.gabrieljamesbenedict.SyntaxAnalysis;

import lombok.Data;

import java.io.Serializable;

@Data
public class AbstractSyntaxTree {

    private Node root;

    private void print(Node node, int level) {
        final String indent = "--".repeat(Math.max(0, level - 1));
        String label = (node.getText() == null)
                ? (node.getType() != null)? node.getType().toString()
                : "Err"
                : node.getType().toString() + ": " +node.getText();
        System.out.println(indent + label);
        for (Node child : node.getChildren()) {
            print(child, level + 1);
        }
    }

    public void print() {
        if (root != null) {
            print(root, 1);
        } else {
            System.out.println("AST is empty");
        }
    }


}
