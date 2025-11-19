package com.gabrieljamesbenedict.porado.node;

import com.gabrieljamesbenedict.porado.type.DataType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VariableNode extends DeclarationNode {

    DataType dataType;
    Object dataValue;

    boolean isDataFromIdentifier = false;

}
