package com.gabrieljamesbenedict.porado.node;

public class LiteralNode extends ExpressionNode{

    @Override
    public void printNode(int level) {
        System.out.println(this.getText());
    }
}
