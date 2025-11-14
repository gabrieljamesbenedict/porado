package com.gabrieljamesbenedict.SyntaxAnalysis;

import com.gabrieljamesbenedict.Exceptions.CompileException;
import com.gabrieljamesbenedict.LexicalAnalysis.Token;
import com.gabrieljamesbenedict.LexicalAnalysis.TokenCategory;
import com.gabrieljamesbenedict.LexicalAnalysis.TokenType;

import java.util.ArrayList;
import java.util.stream.Stream;

public class SyntaxAnalyzer {

    private static final AbstractSyntaxTree AST = new AbstractSyntaxTree();

    public static AbstractSyntaxTree parse(Stream<Token> tokenStream) throws CompileException {

        TokenIterator it = new TokenIterator(tokenStream);

        Token programToken = new Token();
        programToken.setType(TokenType.PROGRAM);
        programToken.setCategory(TokenCategory.PROGRAM);
        programToken.setLexeme("Program");
        Node programNode = addNode(NodeType.PROGRAM, null, null);
        AST.setRoot(programNode);

        parseProgram(it, programNode);

        return AST;
    }

    private static Node addNode(NodeType type, String text, Node parent) {
        Node node = new Node();
        node.setType(type);
        node.setText(text);
        if (parent != null) parent.addChild(node);
        return node;
    }


    private static void parseProgram(TokenIterator it, Node parent) throws CompileException {
        while (!it.eof()) {
            System.out.println("\nNew Statement:");
            parseStatement(it, parent);
        }
    }


    private static Node parseStatement(TokenIterator it, Node parent) throws CompileException {

        if (it.match(TokenType.EOF)) {
            return addNode(NodeType.EOF, null, parent);
        }

        if (it.match(TokenType.DELIMITER_SEMICOLON)) {
            System.out.println("Empty Statement");
            return addNode(NodeType.EMPTY_STATEMENT, null, parent);
        }

        if (it.match(TokenType.DELIMITER_LBRACE)) {
            System.out.println("Block Statement");
            return parseBlockStatement(it, parent);
        }

        System.out.println("Single Statement");
        Node stmt = parseSingleStatement(it, parent);
        // Only expect semicolon if it is a statement that needs it

        if (it.previous().getType() != TokenType.DELIMITER_RBRACE
        && it.previous().getType() != TokenType.EOF
        && it.previous().getType() != TokenType.DELIMITER_SEMICOLON)
        {
            it.expect(TokenType.DELIMITER_SEMICOLON);
        }

        return stmt;
    }



    private static Node parseBlockStatement(TokenIterator it, Node parent) throws CompileException {

        Node block = addNode(NodeType.BLOCK_STATEMENT, null, parent);

        while (!it.eof() && it.peek().getType() != TokenType.DELIMITER_RBRACE) {
            Node node = parseStatement(it, null);
            block.addChild(node);
        }

        it.expect(TokenType.DELIMITER_RBRACE);

        return block;
    }


    private static boolean doLogging = true;

    private static Node parseSingleStatement(TokenIterator it, Node parent) throws CompileException {

        Token current = it.peek();
        Token ahead = it.lookahead(1);

        Node stmt = null;

        // Declarations
        if (current.getCategory() == TokenCategory.IDENTIFIER && ahead.getType() == TokenType.KEYWORD_AS) {
            if (doLogging) System.out.println("Parsing Declaration");
            stmt = parseDeclaration(it, parent);
        }

        // Conditional
        else if (current.getType() == TokenType.KEYWORD_IF) {
            if (doLogging) System.out.println("Parsing Conditional");
            stmt = parseConditional(it, parent);
        }

        // Switch
        else if (current.getType() == TokenType.KEYWORD_SWITCH) {
            if (doLogging) System.out.println("Parsing Switch");
            stmt = parseSwitch(it, parent);
        }

        // Loops
        else if (isLoopKeyword(current.getType())) {
            if (doLogging) System.out.println("Parsing Loop");
            stmt = parseLoop(it, parent);
        }

        // Return
        else if (current.getType() == TokenType.KEYWORD_RETURN) {
            it.next();
            if (doLogging) System.out.println("Parsing Return Statement");
            stmt = addNode(NodeType.RETURN_STATEMENT, it.previous().getLexeme(), parent);
            if (it.peek().getType() == TokenType.DELIMITER_SEMICOLON)
                addNode(NodeType.NO_RETURN, null, stmt);
            else
                stmt.addChild(parseExpression(it));
        }

        // Expression
        else if (isExpression(current)) {
            if (doLogging) System.out.println("Parsing Expression");
            stmt = parseExpression(it);
            parent.addChild(stmt);
        }

        // EOF
        else if (current.getType() == TokenType.EOF) {
            it.next();
            if (doLogging) System.out.println("Parsing EOF");
            stmt = addNode(NodeType.EOF, "EOF", parent);
        }

        if (stmt != null) {
            return stmt;
        }


        throw new CompileException("Syntax Error: Unexpected statement " + current.getLexeme());

    }

    private static boolean isLoopKeyword(TokenType type) {
        return type == TokenType.KEYWORD_WHILE
                || type == TokenType.KEYWORD_UNTIL
                || type == TokenType.KEYWORD_DO
                || type == TokenType.KEYWORD_FOR
                || type == TokenType.KEYWORD_REPEAT
                ;
    }

    private static Node parseLiteralArray(TokenIterator it) throws CompileException {
        Node loopArr = addNode(NodeType.LITERAL_ARRAY, "ARRAY", null);
        while (it.peek().getType() != TokenType.DELIMITER_RBRACKET) {
            Token token = it.next();
            Node arrEl = addNode(NodeType.ARRAY_ELEMENT, token.getLexeme(), loopArr);
            if (it.match(TokenType.DELIMITER_COMMA)) {
                // Do nothing
            } else if (it.match(TokenType.DELIMITER_RBRACKET)) {
                break;
            } else {
                throw new CompileException("Syntax Error: Unexpected symbol in array literal " + it.peek().getLexeme());
            }
        }
        return loopArr;
    }

    private static boolean isExpression(Token tok) {
        if (tok == null) return false;

        return switch (tok.getType()) {
            // Literals
            case LITERAL_INT, LITERAL_FLOAT, LITERAL_STRING, LITERAL_CHAR, LITERAL_TRUE, LITERAL_FALSE -> true;

            // Identifiers can start variables or function calls
            case IDENTIFIER -> true;

            // Parentheses start grouped expressions
            case DELIMITER_LPARENTH -> true;

            // Unary operators
            case OPERATOR_PLUS, OPERATOR_MINUS, OPERATOR_NOT -> true;
            default -> false;
        };
    }


    // ---------------------------------------------------------------------------
    // DECLARATIONS
    // ---------------------------------------------------------------------------

    private static Node parseDeclaration(TokenIterator it, Node parent) throws CompileException {
        Node decl = addNode(null, null, parent);
        Token token;

        token = it.expect(TokenType.IDENTIFIER);
        Node identifier = addNode(null, token.getLexeme(), decl);

        it.expect(TokenType.KEYWORD_AS);

        token = it.peek();
        if (token.getType() == TokenType.KEYWORD_ARRAY) {
            identifier.setType(NodeType.ARRAY_NAME);
            decl.setType(NodeType.ARRAY_DECLARATION);
            it.next();
        } else if (token.getType() == TokenType.KEYWORD_FUNCTION) {
            identifier.setType(NodeType.FUNCTION_NAME);
            decl.setType(NodeType.FUNCTION_DECLARATION);
            it.next();
        } else if (token.getType() == TokenType.TYPE_INT || token.getType() == TokenType.TYPE_FLOAT
                || token.getType() == TokenType.TYPE_CHAR || token.getType() == TokenType.TYPE_STRING
                || token.getType() == TokenType.TYPE_BOOLEAN) {
            identifier.setType(NodeType.VARIABLE_NAME);
            decl.setType(NodeType.VARIABLE_DECLARATION);
        } else {
            throw new CompileException("Syntax Error: Expected array, function, or type, found " + token.getLexeme());
        }

        if (decl.getType() == NodeType.ARRAY_DECLARATION) {

            it.expect(TokenType.KEYWORD_OF);

            if (it.peek().getCategory() != TokenCategory.TYPE) {
                Node arrSize = addNode(NodeType.ARRAY_SIZE, null, null);
                arrSize.addChild(parseExpression(it));
                decl.addChild(arrSize);
            }

            Token t1 = it.expect(TokenType.TYPE_INT, TokenType.TYPE_FLOAT, TokenType.TYPE_STRING, TokenType.TYPE_CHAR, TokenType.TYPE_BOOLEAN);
            Node elType = addNode(NodeType.ARRAY_ELEMENT_TYPE, t1.getLexeme(), decl);

            if (it.match(TokenType.OPERATOR_ASSIGN)) {
                Node arrBody = addNode(NodeType.ARRAY_BODY, null, decl);
                Token t = it.peek();
                if (it.match(TokenType.DELIMITER_LBRACKET)) {
                    Node arrEls = parseLiteralArray(it);
                    arrBody.addChild(arrEls);
                } else if (it.match(TokenType.IDENTIFIER)) {
                    Node arrEls = addNode(NodeType.ARRAY_NAME, t.getLexeme(), null);
                    arrBody.addChild(arrEls);
                } else {
                    throw new CompileException("Syntax Error: Expected Array Literal or Identifier");
                }
            }
        }

        if (decl.getType() == NodeType.FUNCTION_DECLARATION) {

            System.out.println("Check Parameter");
            boolean hasParam = it.match(TokenType.KEYWORD_ACCEPTS);
            if (hasParam) {
                it.expect(TokenType.DELIMITER_LPARENTH);
                Node params = addNode(NodeType.FUNCTION_PARAMETERS, null, decl);
                while (!it.eof() && it.peek().getType() != TokenType.DELIMITER_RPARENTH) {
                    Token t1 = it.expect(TokenType.IDENTIFIER);
                    it.expect(TokenType.KEYWORD_AS);
                    Token t2 = it.next();
                    if (t2.getCategory() != TokenCategory.TYPE) throw new CompileException("Syntax Error: Expected type in parameter declaration");
                    it.match(TokenType.DELIMITER_COMMA);
                    Node param = addNode(NodeType.FUNCTION_PARAMETER, null, params);
                    addNode(NodeType.FUNCTION_PARAMETER_NAME, t1.getLexeme(), param);
                    addNode(NodeType.FUNCTION_PARAMETER_TYPE, t2.getLexeme(), param);
                }
                it.expect(TokenType.DELIMITER_RPARENTH);
            }

            System.out.println("Check Return Type");
            boolean hasReturn = it.match(TokenType.KEYWORD_RETURNS);
            if (hasReturn) {
                Token t2 = it.next();
                if (t2.getCategory() != TokenCategory.TYPE) throw new CompileException("Syntax Error: Expected type in return type declaration");
                Node t1 = addNode(NodeType.FUNCTION_RETURN_TYPE, null, decl);
                NodeType type = switch (t2.getType()) {
                    case TYPE_INT -> NodeType.TYPE_INT;
                    case TYPE_FLOAT -> NodeType.TYPE_FLOAT;
                    case TYPE_CHAR -> NodeType.TYPE_CHAR;
                    case TYPE_STRING -> NodeType.TYPE_STRING;
                    case TYPE_BOOLEAN -> NodeType.TYPE_BOOLEAN;
                    default -> throw new CompileException("Syntax Error: Expected type on function return type");
                };
                addNode(type, t2.getLexeme(), t1);
            }

            System.out.println("Check function body");
            Node functionBody = addNode(NodeType.FUNCTION_BODY, null, decl);
            parseStatement(it, functionBody);

        }

        if (decl.getType() == NodeType.VARIABLE_DECLARATION) {
            Node varType;
            Token t1 = it.expect(TokenType.TYPE_INT
                    , TokenType.TYPE_FLOAT
                    , TokenType.TYPE_CHAR, TokenType.TYPE_STRING
                    , TokenType.TYPE_BOOLEAN);
            varType = addNode(NodeType.VARIABLE_TYPE, t1.getLexeme(), decl);

            if (it.match(TokenType.OPERATOR_ASSIGN)) {
                Node varVal = addNode(NodeType.VARIABLE_BODY, null, decl);
                varVal.addChild(parseExpression(it));
            }
        }

        System.out.println("Done Declaration");
        return decl;
    }

    // ---------------------------------------------------------------------------
    // CONDITIONALS
    // ---------------------------------------------------------------------------

    private static Node parseConditional(TokenIterator it, Node parent) throws CompileException {
        Node cond = addNode(NodeType.CONDITIONAL, null, parent);

        if (it.match(TokenType.KEYWORD_IF)) {
            Node ifNode = addNode(NodeType.IF, null, cond);
            Node ifCondition = addNode(NodeType.IF_CONDITION, null, ifNode);
            ifCondition.addChild(parseExpression(it));
            it.expect(TokenType.KEYWORD_THEN);
            Node ifBody = addNode(NodeType.IF_BODY, null, ifNode);
            ifBody.addChild(parseStatement(it, null));
        } else {
            throw new CompileException("Syntax Error: Expected if keyword");
        }

        while (!it.eof() && it.match(TokenType.KEYWORD_ELSEIF)) {
            Node elseifNode = addNode(NodeType.ELSE_IF, null, cond);
            Node elseifCondition = addNode(NodeType.ELSE_IF_CONDITION, null, elseifNode);
            elseifCondition.addChild(parseExpression(it));
            it.expect(TokenType.KEYWORD_THEN);
            Node elseifBody = addNode(NodeType.ELSE_IF_BODY, null, elseifNode);
            elseifBody.addChild(parseStatement(it, null));
        }

        if (it.match(TokenType.KEYWORD_ELSE)) {
            Node elseNode = addNode(NodeType.ELSE, null, cond);
            it.expect(TokenType.KEYWORD_THEN);
            Node elseifBody = addNode(NodeType.ELSE_BODY, null, elseNode);
            elseifBody.addChild(parseStatement(it, null));
        }

        return cond;
    }

    // ---------------------------------------------------------------------------
    // SWITCH
    // ---------------------------------------------------------------------------

    private static Node parseSwitch(TokenIterator it, Node parent) throws CompileException {
        Node switchNode = addNode(NodeType.SWITCH, null, parent);

        if (!it.match(TokenType.KEYWORD_SWITCH)) {
            throw new CompileException("Syntax Error: Expected switch keyword");
        }

        Node switchExpression = addNode(NodeType.SWITCH_EXPRESSION, null, switchNode);
        switchExpression.addChild(parseExpression(it));

        it.expect(TokenType.DELIMITER_LBRACE);

        Node casesNode = addNode(NodeType.CASES, null, switchNode);
        while (!it.eof() && it.match(TokenType.KEYWORD_CASE)) {
            Node caseNode = addNode(NodeType.CASE, null, casesNode);
            Node caseExpression = addNode(NodeType.CASE_EXPRESSION, null, caseNode);
            caseExpression.addChild(parseExpression(it));
            it.expect(TokenType.DELIMITER_COLON);
            Node caseBody = addNode(NodeType.CASE_BODY, null, caseNode);
            caseBody.addChild(parseStatement(it, caseBody));
        }

        if (it.match(TokenType.KEYWORD_DEFAULT)) {
            Node defaultNode = addNode(NodeType.DEFAULT, null, casesNode);
            it.expect(TokenType.DELIMITER_COLON);
            Node caseBody = addNode(NodeType.DEFAULT_BODY, null, defaultNode);
            caseBody.addChild(parseStatement(it, caseBody));
        }

        it.expect(TokenType.DELIMITER_RBRACE);

        return switchNode;
    }

    // ---------------------------------------------------------------------------
    // LOOPS
    // ---------------------------------------------------------------------------

    private static Node parseLoop(TokenIterator it, Node parent) throws CompileException {
        Node loop = addNode(NodeType.LOOP, null, parent);

        if (it.match(TokenType.KEYWORD_WHILE)) {
            Node loopNode = addNode(NodeType.WHILE, null, loop);
            Node loopCondition = addNode(NodeType.LOOP_CONDITION, null, loopNode);
            loopCondition.addChild(parseExpression(it));
            it.expect(TokenType.KEYWORD_THEN);
            Node loopBody = addNode(NodeType.LOOP_BODY, null, loopNode);
            parseStatement(it, loopBody);
        }

        else if (it.match(TokenType.KEYWORD_UNTIL)) {
            Node loopNode = addNode(NodeType.UNTIL, null, loop);
            Node loopCondition = addNode(NodeType.LOOP_CONDITION, null, loopNode);
            loopCondition.addChild(parseExpression(it));
            it.expect(TokenType.KEYWORD_THEN);
            Node loopBody = addNode(NodeType.LOOP_BODY, null, loopNode);
            parseStatement(it, loopBody);
        }

        else if (it.match(TokenType.KEYWORD_DO)) {
            Node loopNode = addNode(null, null, loop);
            Node loopBody = addNode(NodeType.LOOP_BODY, null, loopNode);
            parseStatement(it, loopBody);
            if (it.match(TokenType.KEYWORD_WHILE)) {
                loopNode.setType(NodeType.DO_WHILE);
            } else if (it.match(TokenType.KEYWORD_UNTIL)) {
                loopNode.setType(NodeType.UNTIL);
            } else {
                throw new CompileException("Syntax Error: Expected when or until keyword");
            }
            Node loopCondition = addNode(NodeType.LOOP_CONDITION, null, loopNode);
            loopCondition.addChild(parseExpression(it));
        }

        else if (it.match(TokenType.KEYWORD_FOR)) {
            Node loopNode = addNode(NodeType.FOR, null, loop);
            it.expect(TokenType.DELIMITER_LPARENTH);
            it.expect(TokenType.KEYWORD_EACH);
            Token t1 = it.expect(TokenType.IDENTIFIER);
            it.expect(TokenType.KEYWORD_IN);
            Token t2 = it.expect(TokenType.IDENTIFIER);
            it.expect(TokenType.DELIMITER_RPARENTH);
            Node loopVar = addNode(NodeType.FOR_VARIABLE, null, loopNode);
            Node loopArr = addNode(NodeType.FOR_ARRAY, null, loopNode);
            Node loopBody = addNode(NodeType.LOOP_BODY, null, loopNode);
            parseStatement(it, loopBody);
        }

        else if (it.match(TokenType.KEYWORD_REPEAT)) {
            Node loopNode = addNode(NodeType.REPEAT, null, loop);
            Node loopAmount = addNode(NodeType.REPEAT_AMOUNT, null, loopNode);
            loopAmount.addChild(parseExpression(it));
            if (it.match(TokenType.KEYWORD_WITH)) {
                Token t1 = it.expect(TokenType.IDENTIFIER);
                it.expect(TokenType.KEYWORD_AS);
                Token t2 = it.next(); if (t2.getCategory() != TokenCategory.TYPE) throw new CompileException("Syntax Error: Expected type in variable declaration");
                Node repVar = addNode(NodeType.REPEAT_VARIABLE, t1.getLexeme(), loopNode);
                if (it.match(TokenType.OPERATOR_ASSIGN)) {
                    Token t3 = it.expect(TokenType.IDENTIFIER, TokenType.LITERAL_INT);
                    Node repVarStart = addNode(NodeType.REPEAT_VARIABLE_START, t3.getLexeme(), loopNode);
                }
            }
            Node loopBody = addNode(NodeType.LOOP_BODY, null, loopNode);
            parseStatement(it, loopBody);
        }

        return loop;
    }

    // ---------------------------------------------------------------------------
    // EXPRESSION PARSER
    // ---------------------------------------------------------------------------

    // This is the most confusing thing ever

    /*
        Assignment
        Logical OR
        Logical XOR
        Logical AND
        Equality
        Comparison
        Addition
        Multiplication
        Unary
        Atomic
     */

    // ---------------------------------------------------------------------------
    // EXPRESSION PARSER
    // ---------------------------------------------------------------------------

    // Entry point for expressions
    private static Node parseExpression(TokenIterator it) throws CompileException {
        ArrayList<Token> tokens = new ArrayList<>();

        while (true) {
            Token next = it.peek();
            if (next == null) break;
            if (
                    next.getCategory() == TokenCategory.KEYWORD
                    || next.getCategory() == TokenCategory.TYPE
                    || (next.getCategory() == TokenCategory.DELIMITER
                    && next.getType() != TokenType.DELIMITER_LPARENTH
                    && next.getType() != TokenType.DELIMITER_RPARENTH)) {
                break;
            }
            tokens.add(next);
            System.out.println("Adding to stream: " + next.getLexeme());
            it.next();
        }

        TokenIterator expressionStream = new TokenIterator(tokens.stream());
        Node expressionNode = parseAssignment(expressionStream);
        System.out.println("All Done");
        return expressionNode;
    }

    // Assignment is right-associative
    private static Node parseAssignment(TokenIterator it) throws CompileException {
        Node left = parseLogicalOr(it);
        if (left == null) return null;

        Token op = null;
        NodeType type = null;

        if (it.match(TokenType.OPERATOR_ASSIGN)) {
            op = it.previous();
            type = NodeType.ASSIGNMENT;
        } else if (it.match(TokenType.OPERATOR_ASSIGNPLUS)) {
            op = it.previous();
            type = NodeType.ADDITION_ASSIGNMENT;
        } else if (it.match(TokenType.OPERATOR_ASSIGNMINUS)) {
            op = it.previous();
            type = NodeType.SUBTRACTION_ASSIGNMENT;
        } else if (it.match(TokenType.OPERATOR_ASSIGNTIMES)) {
            op = it.previous();
            type = NodeType.MULTIPLICATION_ASSIGNMENT;
        } else if (it.match(TokenType.OPERATOR_ASSIGNDIVIDE)) {
            op = it.previous();
            type = NodeType.DIVISION_ASSIGNMENT;
        } else if (it.match(TokenType.OPERATOR_ASSIGNMODULO)) {
            op = it.previous();
            type = NodeType.MODULO_ASSIGNMENT;
        }

        if (type != null) {
            Node right = parseAssignment(it);
            if (right == null)
                throw new CompileException("Expected expression after " + op.getLexeme());

            Node node = new Node();
            node.setType(type);
            node.setText(op.getLexeme());
            node.addChild(left);
            node.addChild(right);
            return node;
        }

        return left;
    }

    // Binary logical operators (OR, XOR, AND) - left-associative
    private static Node parseLogicalOr(TokenIterator it) throws CompileException {
        Node left = parseLogicalXor(it);
        if (left == null) return null;

        while (true) {
            NodeType type = null;
            if (it.match(TokenType.OPERATOR_OR)) type = NodeType.OR;
            else if (it.match(TokenType.OPERATOR_NOR)) type = NodeType.NOR;
            else break;

            Node right = parseLogicalXor(it);
            if (right == null) throw new CompileException("Expected expression after logical operator");

            Node node = new Node();
            node.setType(type);
            node.addChild(left);
            node.addChild(right);
            left = node;
        }

        return left;
    }

    private static Node parseLogicalXor(TokenIterator it) throws CompileException {
        Node left = parseLogicalAnd(it);
        if (left == null) return null;

        while (true) {
            NodeType type = null;
            if (it.match(TokenType.OPERATOR_XOR)) type = NodeType.XOR;
            else if (it.match(TokenType.OPERATOR_XNOR)) type = NodeType.XNOR;
            else break;

            Node right = parseLogicalAnd(it);
            if (right == null) throw new CompileException("Expected expression after logical operator");

            Node node = new Node();
            node.setType(type);
            node.addChild(left);
            node.addChild(right);
            left = node;
        }

        return left;
    }

    private static Node parseLogicalAnd(TokenIterator it) throws CompileException {
        Node left = parseEquality(it);
        if (left == null) return null;

        while (true) {
            NodeType type = null;
            if (it.match(TokenType.OPERATOR_AND)) type = NodeType.AND;
            else if (it.match(TokenType.OPERATOR_NAND)) type = NodeType.NAND;
            else break;

            Node right = parseEquality(it);
            if (right == null) throw new CompileException("Expected expression after logical operator");

            Node node = new Node();
            node.setType(type);
            node.addChild(left);
            node.addChild(right);
            left = node;
        }

        return left;
    }

    // Equality and comparison
    private static Node parseEquality(TokenIterator it) throws CompileException {
        Node left = parseComparison(it);
        if (left == null) return null;

        while (true) {
            NodeType type = null;
            if (it.match(TokenType.OPERATOR_EQUALS)) type = NodeType.EQUALS;
            else if (it.match(TokenType.OPERATOR_NOTEQUALS)) type = NodeType.NOTEQUALS;
            else break;

            Node right = parseComparison(it);
            if (right == null) throw new CompileException("Expected expression after equality operator");

            Node node = new Node();
            node.setType(type);
            node.addChild(left);
            node.addChild(right);
            left = node;
        }

        return left;
    }

    private static Node parseComparison(TokenIterator it) throws CompileException {
        Node left = parseAddition(it);
        if (left == null) return null;

        while (true) {
            NodeType type = null;
            if (it.match(TokenType.OPERATOR_LESSER)) type = NodeType.LESS;
            else if (it.match(TokenType.OPERATOR_LESSERQUALS)) type = NodeType.LESSEQUALS;
            else if (it.match(TokenType.OPERATOR_GREATER)) type = NodeType.GREATER;
            else if (it.match(TokenType.OPERATOR_GREATEREQUALS)) type = NodeType.GREATEREQUALS;
            else break;

            Node right = parseAddition(it);
            if (right == null) throw new CompileException("Expected expression after comparison operator");

            Node node = new Node();
            node.setType(type);
            node.addChild(left);
            node.addChild(right);
            left = node;
        }

        return left;
    }

    // Addition/Subtraction
    private static Node parseAddition(TokenIterator it) throws CompileException {
        Node left = parseMultiplication(it);
        if (left == null) return null;

        while (true) {
            NodeType type = null;
            if (it.match(TokenType.OPERATOR_PLUS)) type = NodeType.ADDITION;
            else if (it.match(TokenType.OPERATOR_MINUS)) type = NodeType.SUBTRACTION;
            else break;

            Node right = parseMultiplication(it);
            if (right == null) throw new CompileException("Expected expression after + or -");

            Node node = new Node();
            node.setType(type);
            node.addChild(left);
            node.addChild(right);
            left = node;
        }

        return left;
    }

    // Multiplication/Division/Modulo
    private static Node parseMultiplication(TokenIterator it) throws CompileException {
        Node left = parseUnary(it);
        if (left == null) return null;

        while (true) {
            NodeType type = null;
            if (it.match(TokenType.OPERATOR_TIMES)) type = NodeType.MULTIPLICATION;
            else if (it.match(TokenType.OPERATOR_DIVIDE)) type = NodeType.DIVISION;
            else if (it.match(TokenType.OPERATOR_MODULO)) type = NodeType.MODULO;
            else break;

            Node right = parseUnary(it);
            if (right == null) throw new CompileException("Expected expression after *, /, or %");

            Node node = new Node();
            node.setType(type);
            node.addChild(left);
            node.addChild(right);
            left = node;
        }

        return left;
    }

    // Unary operators
    private static Node parseUnary(TokenIterator it) throws CompileException {
        if (it.match(TokenType.OPERATOR_NEGATIVE)) {
            Node node = new Node();
            node.setType(NodeType.NEGATIVE);
            node.setText("-");
            Node child = parseUnary(it);
            if (child == null) throw new CompileException("Expected expression after -");
            node.addChild(child);
            return node;
        } else if (it.match(TokenType.OPERATOR_NOT)) {
            Node node = new Node();
            node.setType(NodeType.NOT);
            node.setText("!");
            Node child = parseUnary(it);
            if (child == null) throw new CompileException("Expected expression after !");
            node.addChild(child);
            return node;
        } else {
            System.out.println("Going atomic.");
            return parseAtomic(it);
        }
    }


    // Atomic literals, identifiers, parentheses, arrays, function calls
    private static Node parseAtomic(TokenIterator it) throws CompileException {
        Token token = it.peek();
        if (token == null) return null;

        Node node = new Node();

        // Literals
        if (it.match(TokenType.LITERAL_INT)) {
            node.setType(NodeType.LITERAL_INT);
        } else if (it.match(TokenType.LITERAL_FLOAT)) {
            node.setType(NodeType.LITERAL_FLOAT);
        } else if (it.match(TokenType.LITERAL_CHAR)) {
            node.setType(NodeType.LITERAL_CHAR);
        } else if (it.match(TokenType.LITERAL_STRING)) {
            node.setType(NodeType.LITERAL_STRING);
        } else if (it.match(TokenType.LITERAL_TRUE)) {
            node.setType(NodeType.LITERAL_TRUE);
        } else if (it.match(TokenType.LITERAL_FALSE)) {
            node.setType(NodeType.LITERAL_FALSE);
        }

        // Identifiers (variables, arrays, function calls)
        else if (it.match(TokenType.IDENTIFIER)) {
            node.setType(NodeType.VARIABLE_TYPE);

            // Array access
            if (it.match(TokenType.DELIMITER_LBRACE)) {
                Node index = parseExpression(it);
                node.addChild(index);
                node.setType(NodeType.ARRAY_NAME);
            }
            // Function call
            else if (it.match(TokenType.DELIMITER_LPARENTH)) {
                Node args = new Node();
                args.setType(NodeType.FUNCTION_ARGUMENTS);
                args.setText("ARGUMENTS");

                while (!it.eof() && it.peek().getType() != TokenType.DELIMITER_RPARENTH) {
                    Node arg = parseExpression(it);
                    args.addChild(arg);
                    it.match(TokenType.DELIMITER_COMMA);
                }

                node.addChild(args);
                node.setType(NodeType.FUNCTION_NAME);
            }
        }

        // Parenthesized expressions
        else if (it.match(TokenType.DELIMITER_LPARENTH)) {
            Node expr = parseExpression(it);
            //it.expect(TokenType.DELIMITER_RPARENTH);
            return expr;
        }

        else {
            throw new CompileException("Unexpected token " + token.getLexeme());
        }

        return node;
    }

}