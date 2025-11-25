package com.gabrieljamesbenedict.porado.node;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;

@Getter
public class ProgramNode extends Node {

    private final ArrayList<StatementNode> statements = new ArrayList<>();

    @Override
    public void printNode(int level) {
        System.out.println(this.getText());
        for (StatementNode sn : statements) {
            System.out.print(printIndent(level));
            sn.printNode(level+1);
        }
    }
}
