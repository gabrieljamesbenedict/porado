package com.gabrieljamesbenedict.SyntaxAnalysis.node;

import com.gabrieljamesbenedict.LexicalAnalysis.Token;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Node {

    NodeType type;
    Token token;

    Node parent;
    ArrayList<Node> children = new ArrayList<>();

    public void addChild(Node node) {
        children.add(node);
    }
    public void addAllChildren(Node... node) {
        children.addAll(List.of(node));
    }

}
