package com.gabrieljamesbenedict.porado.node;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BinaryExpressionNode extends ExpressionNode {

    ExpressionNode operand1, operand2;

    @Override
    public void printNode(int level) {
        System.out.println(this.getText());

        System.out.print(printIndent(level));
        operand1.printNode(level + 1);

        System.out.print(printIndent(level));
        operand2.printNode(level + 1);

    }

}
