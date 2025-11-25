package com.gabrieljamesbenedict.porado.node;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnaryExpressionNode extends ExpressionNode{

    ExpressionNode operand1;

    @Override
    public void printNode(int level) {
        System.out.println(this.getText());

        System.out.print(printIndent(level));
        operand1.printNode(level + 1);

    }

}
