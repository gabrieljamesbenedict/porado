package com.gabrieljamesbenedict.porado.node;

import com.gabrieljamesbenedict.porado.type.DataType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VariableNode extends DeclarationNode {

    DataType dataType;
    ExpressionNode dataValue = null;

    @Override
    public void printNode(int level) {
        System.out.println(this.text);
        System.out.println(printIndent(level) + "Identifier: " + this.getIdentifier());
        if (dataValue != null) {
            System.out.print(printIndent(level) + "Value: ");
            dataValue.printNode(level+1);
        }
    }

}
