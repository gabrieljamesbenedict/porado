package com.gabrieljamesbenedict.porado;

import com.gabrieljamesbenedict.porado.node.ProgramNode;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class AbstractSyntaxTree {

    private ProgramNode programNode;

    private AbstractSyntaxTree() {}

    public static AbstractSyntaxTree createNewTree(ProgramNode programNode) {
        AbstractSyntaxTree ast = new AbstractSyntaxTree();
        ast.programNode = programNode;
        return ast;
    }

}
