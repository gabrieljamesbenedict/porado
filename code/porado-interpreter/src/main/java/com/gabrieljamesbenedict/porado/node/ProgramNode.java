package com.gabrieljamesbenedict.porado.node;

import lombok.Getter;

import java.util.ArrayList;

@Getter
public class ProgramNode extends Node {

    private final ArrayList<StatementNode> statements = new ArrayList<>();

}
