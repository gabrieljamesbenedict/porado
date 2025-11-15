package com.gabrieljamesbenedict.Interpreter;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Symbol {

    String name;
    Type dataType;
    SymbolType symbolType;

    Object data;

}
