package com.gabrieljamesbenedict.porado.node;

public enum NodeType {
    Program,

    BLOCK_STATEMENT,

    PRINT,
    DECLARATION,

    ASSIGNMENT,
    OR,
    NOR,
    XOR,
    XNOR,
    AND,
    NAND,
    EQUALS,
    UNEQUALS,
    GREATER,
    GREATHERTHAN,
    LESS,
    LESSTHAN,

    ADDITIVE,
    MULTIPLICATIVE,

    UNARY,

    LITERAL,
    IDENTIFIER,

    VARIABLE_DECLARATION, EOF
}
