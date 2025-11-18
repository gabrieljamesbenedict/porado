package com.gabrieljamesbenedict.porado.node;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;


@Getter
@Setter
public class BlockStatementNode extends StatementNode {

    private final ArrayList<StatementNode> statements = new ArrayList<>();

}
