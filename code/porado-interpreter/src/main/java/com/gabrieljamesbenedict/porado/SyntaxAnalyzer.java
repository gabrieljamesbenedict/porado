package com.gabrieljamesbenedict.porado;

import com.gabrieljamesbenedict.porado.node.*;
import com.gabrieljamesbenedict.porado.token.Token;
import com.gabrieljamesbenedict.porado.token.TokenType;
import com.gabrieljamesbenedict.porado.type.DataType;
import com.gabrieljamesbenedict.porado.util.ExpressionParser;
import com.gabrieljamesbenedict.porado.util.PeekableIterator;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class SyntaxAnalyzer {

    private final List<Token> tokenList;
    private PeekableIterator<Token> it;

    private ExpressionParser expressionParser;

    public AbstractSyntaxTree parse() {
        it = new PeekableIterator<>(tokenList);

        expressionParser = ExpressionParser.getInstance();
        expressionParser.setIterator(it);

        ProgramNode program = parseProgram();
        return AbstractSyntaxTree.createNewTree(program);
    }





    private ProgramNode parseProgram() {
        ProgramNode programNode = new ProgramNode();
        programNode.setType(NodeType.Program);
        programNode.setText("Program");
        it.next();

        if (!match(TokenType.EOF)) {
            StatementNode statementNode =  parseStatement();
            programNode.getStatements().add(statementNode);
        }

        return programNode;
    }





    private StatementNode parseStatement() {
        StatementNode statementNode;

        if (match(TokenType.EOF)) {
            it.next();
            statementNode = new EOFNode();
            statementNode.setType(NodeType.EOF);
            statementNode.setText("EOF");
        }

        else if (match(TokenType.DELIMITER_LBRACE)) {
            it.next();
            statementNode = parseBlockStatement();
        }

        else {
            statementNode = parseSingleStatement();
        }

        return statementNode;
    }





    private BlockStatementNode parseBlockStatement() {
        BlockStatementNode blockStatementNode = new BlockStatementNode();
        blockStatementNode.setType(NodeType.BLOCK_STATEMENT);

        while (!match(TokenType.DELIMITER_RBRACE)) {
            SingleStatementNode singleStatementNode = parseSingleStatement();
            blockStatementNode.getStatements().add(singleStatementNode);
        }

        if (!match(TokenType.DELIMITER_RBRACE)) throw new IllegalStateException("Expected closing brace symbol after block statement, found: " + it.peek().getType());
        else it.next();
        return blockStatementNode;
    }





    private SingleStatementNode parseSingleStatement() {
        SingleStatementNode singleStatementNode;

        // Declaration Statement
        if (match(TokenType.IDENTIFIER) &&
                matchAhead(TokenType.KEYWORD_AS, 1)) {
            singleStatementNode = parseDeclaration();
        }

        // Print Statement
        else if (match(TokenType.KEYWORD_PRINT)) {
            singleStatementNode = parsePrint();
        }

        // Everything else is an expression
        else singleStatementNode = parseExpression();

        if (!match(TokenType.DELIMITER_SEMICOLON)) throw new IllegalStateException("Expected semicolon symbol after statement, found: " + it.peek().getType());
        else it.next();
        return singleStatementNode;
    }





    private PrintNode parsePrint() {
        PrintNode printNode = new PrintNode();
        printNode.setPrintExpression(parseExpression());
        printNode.setType(NodeType.PRINT);
        printNode.setText("Print");
        return  printNode;
    }





    private DeclarationNode parseDeclaration() {
        VariableNode declarationNode = null;

        Token id = it.next();
        it.next(); // KEYWORD_AS

        Token type;
        if (match(TokenType.KEYWORD_ARRAY)) {
            type = it.next();
            throw new UnsupportedOperationException("TODO: create array implementation for Porado");
        }

        else if (match(TokenType.KEYWORD_FUNCTION)) {
            type = it.next();
            throw new UnsupportedOperationException("TODO: create function implementation for Porado");
        }

        else if (matchAll(TokenType.getAllTypes())) {
            type = it.next();
            declarationNode = new VariableNode();
            declarationNode.setType(NodeType.VARIABLE_DECLARATION);
            declarationNode.setText("Variable Declaration");

            DataType dt = switch (type.getType()) {
                case TYPE_INT -> DataType.INT;
                case TYPE_FLOAT -> DataType.FLOAT;
                case TYPE_CHAR -> DataType.CHAR;
                case TYPE_STRING -> DataType.STRING;
                case TYPE_BOOLEAN -> DataType.BOOLEAN;
                default -> throw new IllegalStateException("Expected valid data type in variable declaration, found: " + type.getType());
            };
            declarationNode.setIdentifier(id.getLexeme());
            declarationNode.setDataType(dt);

            if (match(TokenType.OPERATOR_ASSIGN)) {
                it.next();
                // TODO: figure this out in the future lol
//                declarationNode.setDataValue(it.next().getLexeme());
//                if (matchAll(TokenType.getAllLiterals())) {
//                    declarationNode.setDataFromIdentifier(false);
//                } else if (match(TokenType.IDENTIFIER)) {
//                    declarationNode.setDataFromIdentifier(true);
//                }
                declarationNode.setDataValue(expressionParser.parse());
            }
        }

        else throw new IllegalStateException("Expected array, function, or data type keyword after \"as\", found: " + it.peek().getType());

        return declarationNode;
    }





    private ExpressionNode parseExpression() {
        return expressionParser.parse();
    }




    
    private boolean match(TokenType type) {
        return it.peek().getType() == type;
    }

    private boolean matchAll(TokenType... types) {
        TokenType peekType = it.peek().getType();
        for (TokenType t : types) {
            if (peekType == t) return true;
        }
        return false;
    }

    private boolean matchAhead(TokenType type, int ahead) {
        return it.ahead(ahead).getType() == type;
    }

}
