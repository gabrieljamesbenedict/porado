package com.gabrieljamesbenedict.porado.node;

import lombok.Getter;
import lombok.Setter;

public class PrintNode extends SingleStatementNode {

    @Getter
    @Setter
    private ExpressionNode printExpression;

}