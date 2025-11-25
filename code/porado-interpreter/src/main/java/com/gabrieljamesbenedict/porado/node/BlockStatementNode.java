package com.gabrieljamesbenedict.porado.node;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;


@Getter
@Setter
public class BlockStatementNode extends StatementNode {

    private final ArrayList<SingleStatementNode> statements = new ArrayList<>();

    @Override
    public void printNode(int level) {
        System.out.println(this.getText());
        for (SingleStatementNode sn : statements) {
            System.out.print(printIndent(level));
            sn.printNode(level+1);
        }
    }

}
