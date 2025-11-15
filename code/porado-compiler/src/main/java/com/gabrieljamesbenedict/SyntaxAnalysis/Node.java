package com.gabrieljamesbenedict.SyntaxAnalysis;

import lombok.Data;

import java.util.ArrayList;

@Data
public class Node {

    NodeType type;
    String text;

    transient Node parent;
    ArrayList<Node> children = new ArrayList<>();

    public void addChild(Node node) {
        node.setParent(this);
        children.add(node);
    }
    public void addAllChildren(Node... nodes) {
        for (Node node : nodes) {
            addChild(node);
        }
    }

    public Node getChildById(NodeType type) {
        return children.stream().filter(n -> n.getType() == type).findFirst().orElseThrow(
                () -> new RuntimeException("Error: No such child")
        );
    }

}
