package com.gabrieljamesbenedict.SyntaxAnalysis;

import com.gabrieljamesbenedict.Exceptions.CompileException;
import com.gabrieljamesbenedict.LexicalAnalysis.Token;
import com.gabrieljamesbenedict.LexicalAnalysis.TokenCategory;
import com.gabrieljamesbenedict.LexicalAnalysis.TokenType;
import com.gabrieljamesbenedict.SyntaxAnalysis.node.*;

import java.util.stream.Stream;

public class SyntaxAnalyzer {

    private static final AbstractSyntaxTree AST = new AbstractSyntaxTree();

    public static AbstractSyntaxTree parse(Stream<Token> tokenStream) throws CompileException {

        TokenIterator it = new TokenIterator(tokenStream);

        Token programToken = new Token();
        programToken.setType(TokenType.PROGRAM);
        programToken.setCategory(TokenCategory.PROGRAM);
        programToken.setLexeme("Program");
        Node programNode = addNode(NodeType.PROGRAM, programToken, null);
        AST.setRoot(programNode);



        parseProgram(it, programNode);

        return AST;
    }

    private static Node addNode(NodeType type, Token token, Node parent) {
        Node node = new Node();
        node.setType(type);
        node.setToken(token);
        node.setParent(parent);

        if (parent != null)
            parent.addChild(node);

        return node;
    }

    private static void parseProgram(TokenIterator it, Node parent) throws CompileException {
        while (!it.eof()) {
            parseStatement(it, parent);
        }
    }

    private static Node parseStatement(TokenIterator it, Node parent) throws CompileException {

        Token current = it.peek();

        // Empty statement
        if (it.match(TokenType.DELIMITER_SEMICOLON)) {
            return addNode(NodeType.EMPTY_STATEMENT, current, parent);
        }

        // Block statement
        if (it.match(TokenType.DELIMITER_LBRACKET)) {
            return parseBlockStatement(it, parent);
        }

        // Everything else is a single statement
        return parseSingleStatement(it, parent);
    }

    private static Node parseBlockStatement(TokenIterator it, Node parent) throws CompileException {

        Node block = addNode(NodeType.BLOCK_STATEMENT, null, parent);

        while (!it.eof() && !it.match(TokenType.DELIMITER_RBRACKET)) {
            parseStatement(it, block);
        }

        return block;
    }

    private static Node parseSingleStatement(TokenIterator it, Node parent) throws CompileException {

        Token current = it.peek();
        Token ahead = it.lookahead(1);

        // Declaration: IDENTIFIER AS ...
        if (current.getCategory() == TokenCategory.IDENTIFIER &&
                ahead.getType() == TokenType.KEYWORD_AS) {
            return parseDeclaration(it, parent);
        }

        // if / else if / else
        if (it.match(TokenType.KEYWORD_IF)) {
            return parseConditional(it, parent, true);
        }

        if (it.match(TokenType.KEYWORD_SWITCH)) {
            return parseSwitch(it, parent);
        }

        // Loops
        if (isLoopKeyword(current.getType())) {
            return parseLoop(it, parent);
        }

        // Expression
        if (canStartExpression(current)) {
            return parseExpression(it, parent);
        }

        if (it.match(TokenType.EOF)) {
            return addNode(NodeType.EOF, current, parent);
        }

        throw new CompileException("Unexpected token: " + current.getLexeme());
    }


    // ---------------------------------------------------------------------------
    // DECLARATIONS
    // ---------------------------------------------------------------------------

    private static Node parseDeclaration(TokenIterator it, Node parent) throws CompileException {

        Node decl = addNode(NodeType.VARIABLE_DECLARATION, null, parent);

        Token identifier = it.expect(TokenType.IDENTIFIER);
        addNode(NodeType.VARIABLE_IDENTIFIER, identifier, decl);

        it.expect(TokenType.KEYWORD_AS);

        Token type = it.expect(
                TokenType.TYPE_INT,
                TokenType.TYPE_FLOAT,
                TokenType.TYPE_CHAR,
                TokenType.TYPE_STRING,
                TokenType.TYPE_BOOLEAN
        );

        addNode(NodeType.VARIABLE_TYPE, type, decl);

        // Optional assignment
        if (it.match(TokenType.OPERATOR_ASSIGN)) {
            parseExpression(it, decl);
        }

        return decl;
    }


    // ---------------------------------------------------------------------------
    // CONDITIONALS
    // ---------------------------------------------------------------------------

    private static Node parseConditional(TokenIterator it, Node parent, boolean isIf) throws CompileException {

        Node cond = addNode(NodeType.CONDITIONAL, null, parent);

        if (isIf) {
            Node ifNode = addNode(NodeType.IF, it.peek(), cond);

            it.expect(TokenType.DELIMITER_LPARENTH);
            parseExpression(it, ifNode);
            it.expect(TokenType.DELIMITER_RPARENTH);

            parseStatement(it, ifNode);

            // else-if chain
            while (it.match(TokenType.KEYWORD_ELSE) && it.match(TokenType.KEYWORD_IF)) {
                Node elif = addNode(NodeType.ELSE_IF, it.peek(), cond);
                it.expect(TokenType.DELIMITER_LPARENTH);
                parseExpression(it, elif);
                it.expect(TokenType.DELIMITER_RPARENTH);
                parseStatement(it, elif);
            }

            // else block
            if (it.match(TokenType.KEYWORD_ELSE)) {
                Node elseNode = addNode(NodeType.ELSE, it.peek(), cond);
                parseStatement(it, elseNode);
            }
        }

        return cond;
    }


    // ---------------------------------------------------------------------------
    // SWITCH
    // ---------------------------------------------------------------------------

    private static Node parseSwitch(TokenIterator it, Node parent) throws CompileException {

        Node switchNode = addNode(NodeType.SWITCH, it.peek(), parent);

        it.expect(TokenType.DELIMITER_LPARENTH);
        parseExpression(it, switchNode);
        it.expect(TokenType.DELIMITER_RPARENTH);

        it.expect(TokenType.DELIMITER_LBRACKET);

        while (!it.eof() && !it.match(TokenType.DELIMITER_RBRACKET)) {
            parseCase(it, switchNode);
        }

        return switchNode;
    }

    private static Node parseCase(TokenIterator it, Node parent) throws CompileException {

        Node caseNode;

        if (it.match(TokenType.KEYWORD_CASE)) {
            caseNode = addNode(NodeType.CASE, it.peek(), parent);
            it.expect(TokenType.DELIMITER_LPARENTH);
            parseExpression(it, caseNode);
            it.expect(TokenType.DELIMITER_RPARENTH);
        } else if (it.match(TokenType.KEYWORD_DEFAULT)) {
            caseNode = addNode(NodeType.DEFAULT, it.peek(), parent);
        } else {
            throw new CompileException("Invalid case in switch");
        }

        it.expect(TokenType.DELIMITER_COLON);
        parseStatement(it, caseNode);

        return caseNode;
    }


    // ---------------------------------------------------------------------------
    // LOOPS
    // ---------------------------------------------------------------------------

    private static Node parseLoop(TokenIterator it, Node parent) throws CompileException {

        Token keyword = it.peek();

        if (keyword.getType() == TokenType.KEYWORD_WHILE) {
            it.next();
            Node loop = addNode(NodeType.WHILE_LOOP, keyword, parent);
            it.expect(TokenType.DELIMITER_LPARENTH);
            parseExpression(it, loop);
            it.expect(TokenType.DELIMITER_RPARENTH);
            parseStatement(it, loop);
            return loop;
        }

        if (keyword.getType() == TokenType.KEYWORD_UNTIL) {
            it.next();
            Node loop = addNode(NodeType.UNTIL_LOOP, keyword, parent);
            it.expect(TokenType.DELIMITER_LPARENTH);
            parseExpression(it, loop);
            it.expect(TokenType.DELIMITER_RPARENTH);
            parseStatement(it, loop);
            return loop;
        }

        if (keyword.getType() == TokenType.KEYWORD_DO) {
            it.next();
            Node loop = addNode(null, keyword, parent);

            parseStatement(it, loop);

            if (it.match(TokenType.KEYWORD_WHILE)) {
                loop.setType(NodeType.DO_WHILE_LOOP);
            } else if (it.match(TokenType.KEYWORD_UNTIL)) {
                loop.setType(NodeType.DO_UNTIL_LOOP);
            } else {
                throw new CompileException("Expected WHILE or UNTIL after DO");
            }

            it.expect(TokenType.DELIMITER_LPARENTH);
            parseExpression(it, loop);
            it.expect(TokenType.DELIMITER_RPARENTH);

            return loop;
        }

        if (keyword.getType() == TokenType.KEYWORD_FOR) {
            it.next();
            Node loop = addNode(NodeType.FOR_LOOP, keyword, parent);

            it.expect(TokenType.DELIMITER_LPARENTH);
            it.expect(TokenType.KEYWORD_EACH);

            Token var = it.expect(TokenType.IDENTIFIER);
            addNode(NodeType.VARIABLE_IDENTIFIER, var, loop);

            it.expect(TokenType.KEYWORD_IN);

            Token arr = it.expect(TokenType.IDENTIFIER);
            addNode(NodeType.ARRAY_IDENTIFIER, arr, loop);

            it.expect(TokenType.DELIMITER_RPARENTH);

            parseStatement(it, loop);

            return loop;
        }

        if (keyword.getType() == TokenType.KEYWORD_REPEAT) {
            it.next();
            Node loop = addNode(NodeType.REPEAT_LOOP, keyword, parent);

            it.expect(TokenType.DELIMITER_LPARENTH);
            Token count = it.expect(TokenType.LITERAL_INT, TokenType.IDENTIFIER);
            addNode(NodeType.REPEAT_AMOUNT, count, loop);
            it.expect(TokenType.DELIMITER_RPARENTH);

            if (it.match(TokenType.KEYWORD_WITH)) {
                Token var = it.expect(TokenType.IDENTIFIER);
                addNode(NodeType.VARIABLE_IDENTIFIER, var, loop);

                it.expect(TokenType.KEYWORD_AS);
                it.expect(TokenType.TYPE_INT, TokenType.TYPE_FLOAT,
                        TokenType.TYPE_CHAR, TokenType.TYPE_STRING,
                        TokenType.TYPE_BOOLEAN);
            }

            parseStatement(it, loop);

            return loop;
        }

        throw new CompileException("Invalid loop");
    }

    private static boolean isLoopKeyword(TokenType type) {
        return type == TokenType.KEYWORD_WHILE
                || type == TokenType.KEYWORD_UNTIL
                || type == TokenType.KEYWORD_DO
                || type == TokenType.KEYWORD_FOR
                || type == TokenType.KEYWORD_REPEAT
                ;
    }

    // ---------------------------------------------------------------------------
    // EXPRESSION PARSER
    // ---------------------------------------------------------------------------

    // Entry point
    private static Node parseExpression(TokenIterator it, Node parent) throws CompileException {
        Token tok = it.peek();
        if (tok == null || !canStartExpression(tok)) return null;
        return parseLogicalOr(it, parent);
    }

    // Checks if a token can start an expression
    private static boolean canStartExpression(Token tok) {
        switch (tok.getType()) {
            case LITERAL_INT, LITERAL_FLOAT, LITERAL_STRING, LITERAL_CHAR,
                 LITERAL_TRUE, LITERAL_FALSE, IDENTIFIER,
                 DELIMITER_LPARENTH, OPERATOR_PLUS, OPERATOR_MINUS, OPERATOR_NOT:
                return true;
            default:
                return false;
        }
    }

    // Logical OR (||)
    private static Node parseLogicalOr(TokenIterator it, Node parent) throws CompileException {
        Node left = parseLogicalAnd(it, parent);
        if (left == null) return null;

        while (it.match(TokenType.OPERATOR_OR)) {
            Token op = it.previous();  // Use previous consumed token from iterator
            Node node = addNode(NodeType.BINARY_EXPRESSION, op, parent);
            node.addChild(left);
            node.addChild(parseLogicalAnd(it, node));
            left = node;
        }

        return left;
    }

    // Logical AND (&&)
    private static Node parseLogicalAnd(TokenIterator it, Node parent) throws CompileException {
        Node left = parseEquality(it, parent);
        if (left == null) return null;

        while (it.match(TokenType.OPERATOR_AND)) {
            Token op = it.previous();
            Node node = addNode(NodeType.BINARY_EXPRESSION, op, parent);
            node.addChild(left);
            node.addChild(parseEquality(it, node));
            left = node;
        }

        return left;
    }

    // Equality (==, !=)
    private static Node parseEquality(TokenIterator it, Node parent) throws CompileException {
        Node left = parseComparison(it, parent);
        if (left == null) return null;

        while (it.match(TokenType.OPERATOR_EQUALS) || it.match(TokenType.OPERATOR_NOTEQUALS)) {
            Token op = it.previous();
            Node node = addNode(NodeType.BINARY_EXPRESSION, op, parent);
            node.addChild(left);
            node.addChild(parseComparison(it, node));
            left = node;
        }

        return left;
    }

    // Comparison (<, <=, >, >=)
    private static Node parseComparison(TokenIterator it, Node parent) throws CompileException {
        Node left = parseAddition(it, parent);
        if (left == null) return null;

        while (it.match(TokenType.OPERATOR_LESSER) || it.match(TokenType.OPERATOR_LESSERQUALS)
                || it.match(TokenType.OPERATOR_GREATER) || it.match(TokenType.OPERATOR_GREATEREQUALS)) {
            Token op = it.previous();
            Node node = addNode(NodeType.BINARY_EXPRESSION, op, parent);
            node.addChild(left);
            node.addChild(parseAddition(it, node));
            left = node;
        }

        return left;
    }

    // Addition/Subtraction (+, -)
    private static Node parseAddition(TokenIterator it, Node parent) throws CompileException {
        Node left = parseMultiplication(it, parent);
        if (left == null) return null;

        while (it.match(TokenType.OPERATOR_PLUS) || it.match(TokenType.OPERATOR_MINUS)) {
            Token op = it.previous();
            Node node = addNode(NodeType.BINARY_EXPRESSION, op, parent);
            node.addChild(left);
            node.addChild(parseMultiplication(it, node));
            left = node;
        }

        return left;
    }

    // Multiplication/Division/Modulo (*, /, %)
    private static Node parseMultiplication(TokenIterator it, Node parent) throws CompileException {
        Node left = parseUnary(it, parent);
        if (left == null) return null;

        while (it.match(TokenType.OPERATOR_TIMES) || it.match(TokenType.OPERATOR_DIVIDE) || it.match(TokenType.OPERATOR_MODULO)) {
            Token op = it.previous();
            Node node = addNode(NodeType.BINARY_EXPRESSION, op, parent);
            node.addChild(left);
            node.addChild(parseUnary(it, node));
            left = node;
        }

        return left;
    }

    // Unary operators (+, -, !)
    private static Node parseUnary(TokenIterator it, Node parent) throws CompileException {
        if (it.match(TokenType.OPERATOR_NOT) || it.match(TokenType.OPERATOR_PLUS) || it.match(TokenType.OPERATOR_MINUS)) {
            Token op = it.previous();
            Node node = addNode(NodeType.UNARY_EXPRESSION, op, parent);
            node.addChild(parseUnary(it, node));
            return node;
        }
        return parsePrimary(it, parent);
    }

    // Primary expressions (literals, identifiers, function calls, parentheses)
    private static Node parsePrimary(TokenIterator it, Node parent) throws CompileException {
        Token tok = it.peek();
        if (tok == null) throw new CompileException("Unexpected EOF in expression");

        Node node;
        switch (tok.getType()) {
            case LITERAL_INT, LITERAL_FLOAT, LITERAL_STRING, LITERAL_CHAR, LITERAL_TRUE, LITERAL_FALSE -> {
                it.next();
                node = addNode(mapLiteralToNodeType(tok.getType()), tok, parent);
            }

            case IDENTIFIER -> {
                it.next();
                node = addNode(NodeType.IDENTIFIER, tok, parent);

                if (it.match(TokenType.DELIMITER_LPARENTH)) {
                    Node callNode = addNode(NodeType.FUNCTION_EXPRESSION, tok, parent);
                    callNode.addChild(node);
                    while (!it.eof() && !it.match(TokenType.DELIMITER_RPARENTH)) {
                        Node arg = parseExpression(it, callNode);
                        if (arg != null) callNode.addChild(arg);
                        it.match(TokenType.DELIMITER_COMMA); // optional
                    }
                    node = callNode;
                }
            }

            case DELIMITER_LPARENTH -> {
                it.next();
                node = parseExpression(it, parent);
                it.expect(TokenType.DELIMITER_RPARENTH);
            }

            default -> throw new CompileException("Unexpected token in expression: " + tok.getLexeme());
        }

        return node;
    }

    private static NodeType mapLiteralToNodeType(TokenType type) {
        return switch (type) {
            case LITERAL_INT -> NodeType.LITERAL_INT;
            case LITERAL_FLOAT -> NodeType.LITERAL_FLOAT;
            case LITERAL_STRING -> NodeType.LITERAL_STRING;
            case LITERAL_CHAR -> NodeType.LITERAL_CHAR;
            case LITERAL_TRUE -> NodeType.LITERAL_TRUE;
            case LITERAL_FALSE -> NodeType.LITERAL_FALSE;
            default -> null;
        };
    }
}