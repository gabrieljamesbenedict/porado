package com.gabrieljamesbenedict.porado.node;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpressionNode extends SingleStatementNode{

    ExpressionNode operand1, operand2;

}
