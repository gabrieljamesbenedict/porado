package com.gabrieljamesbenedict.SyntaxAnalysis;

import com.gabrieljamesbenedict.SyntaxAnalysis.node.Node;
import lombok.Data;

@Data
public class AbstractSyntaxTree {

    private Node root;

    private void print(Node node, int level) {
        final String indent = "--".repeat(Math.max(0, level - 1));

        String label;
        if (node.getToken() != null && node.getChildren().isEmpty()) {
            label = node.getToken().getLexeme();
        } else {
            label = node.getType().toString();
        }

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
