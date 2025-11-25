package com.gabrieljamesbenedict.porado.util;

import com.gabrieljamesbenedict.porado.node.*;
import com.gabrieljamesbenedict.porado.token.Token;
import com.gabrieljamesbenedict.porado.token.TokenType;

public class ExpressionParser {

    public static ExpressionParser instance;
    public static ExpressionParser getInstance() {
        if (instance == null) {
            instance = new ExpressionParser();
        }
        return instance;
    }
    private ExpressionParser () {}

    private static PeekableIterator<Token> it = null;
    public void setIterator(PeekableIterator<Token> it) {
        ExpressionParser.it = it;
    }

        public ExpressionNode parse() {
            return parseAssignment();
        }


        private ExpressionNode parseAssignment() {
            ExpressionNode left = parseOrExpression();

            TokenType tt = it.peek().getType();
            if (tt == TokenType.OPERATOR_ASSIGN ||
                    tt == TokenType.OPERATOR_ASSIGN_ADD ||
                    tt == TokenType.OPERATOR_ASSIGN_SUBTRACT ||
                    tt == TokenType.OPERATOR_ASSIGN_MULTIPLY ||
                    tt == TokenType.OPERATOR_ASSIGN_DIVIDE ||
                    tt == TokenType.OPERATOR_ASSIGN_MODULO) {

                Token t = it.next();
                ExpressionNode right = parseAssignment();

                AssignmentNode node = new AssignmentNode();
                node.setText(t.getLexeme());
                node.setType(NodeType.ASSIGNMENT);
                node.setOperand1(left);
                node.setOperand2(right);

                return node;
            }

            return left;
        }


        private ExpressionNode parseOrExpression() {
            ExpressionNode left = parseXorExpression();

            while (it.peek().getType() == TokenType.OPERATOR_OR ||
                    it.peek().getType() == TokenType.OPERATOR_NOR) {

                Token t = it.next();
                ExpressionNode right = parseXorExpression();

                OrNode node = new OrNode();
                node.setText(t.getLexeme());
                node.setType(NodeType.OR); //TODO
                node.setOperand1(left);
                node.setOperand2(right);

                left = node;
            }

            return left;
        }


        private ExpressionNode parseXorExpression() {
            ExpressionNode left = parseAndExpression();

            while (it.peek().getType() == TokenType.OPERATOR_XOR ||
                    it.peek().getType() == TokenType.OPERATOR_XNOR) {

                Token t = it.next();
                ExpressionNode right = parseAndExpression();

                XorNode node = new XorNode();
                node.setText(t.getLexeme());
                node.setType(NodeType.XOR);//TODO
                node.setOperand1(left);
                node.setOperand2(right);

                left = node;
            }

            return left;
        }


        private ExpressionNode parseAndExpression() {
            ExpressionNode left = parseEqualityExpression();

            while (it.peek().getType() == TokenType.OPERATOR_AND ||
                    it.peek().getType() == TokenType.OPERATOR_NAND) {

                Token t = it.next();
                ExpressionNode right = parseEqualityExpression();

                AndNode node = new AndNode();
                node.setText(t.getLexeme());
                node.setType(NodeType.AND);//TODO
                node.setOperand1(left);
                node.setOperand2(right);

                left = node;
            }

            return left;
        }


        private ExpressionNode parseEqualityExpression() {
            ExpressionNode left = parseInequalityExpression();

            while (it.peek().getType() == TokenType.OPERATOR_EQUAL ||
                    it.peek().getType() == TokenType.OPERATOR_NOT_EQUAL) {

                Token t = it.next();
                ExpressionNode right = parseInequalityExpression();

                EqualityNode node = new EqualityNode();
                node.setText(t.getLexeme());
                node.setType(NodeType.EQUALS);//TODO
                node.setOperand1(left);
                node.setOperand2(right);

                left = node;
            }

            return left;
        }


        private ExpressionNode parseInequalityExpression() {
            ExpressionNode left = parseAdditiveExpression();

            while (it.peek().getType() == TokenType.OPERATOR_LESS_THAN ||
                    it.peek().getType() == TokenType.OPERATOR_LESS_THAN_EQUAL ||
                    it.peek().getType() == TokenType.OPERATOR_GREATER_THAN ||
                    it.peek().getType() == TokenType.OPERATOR_GREATER_THAN_EQUAL) {

                Token t = it.next();
                ExpressionNode right = parseAdditiveExpression();

                InequalityNode node = new InequalityNode();
                node.setText(t.getLexeme());
                node.setType(NodeType.GREATER);//TODO
                node.setOperand1(left);
                node.setOperand2(right);

                left = node;
            }

            return left;
        }


        private ExpressionNode parseAdditiveExpression() {
            ExpressionNode left = parseMultiplicativeExpression();

            while (it.peek().getType() == TokenType.OPERATOR_ADD ||
                    it.peek().getType() == TokenType.OPERATOR_SUBTRACT) {

                Token t = it.next();
                ExpressionNode right = parseMultiplicativeExpression();

                AddNode node = new AddNode();
                node.setText(t.getLexeme());
                node.setType(NodeType.ADDITIVE);
                node.setOperand1(left);
                node.setOperand2(right);

                left = node;
            }

            return left;
        }


        private ExpressionNode parseMultiplicativeExpression() {
            ExpressionNode left = parseUnaryExpression();

            while (it.peek().getType() == TokenType.OPERATOR_MULTIPLY ||
                    it.peek().getType() == TokenType.OPERATOR_DIVIDE ||
                    it.peek().getType() == TokenType.OPERATOR_MODULO) {

                Token t = it.next();
                ExpressionNode right = parseUnaryExpression();

                MultiplyNode node = new MultiplyNode();
                node.setText(t.getLexeme());
                node.setType(NodeType.MULTIPLICATIVE);
                node.setOperand1(left);
                node.setOperand2(right);

                left = node;
            }

            return left;
        }


        private ExpressionNode parseUnaryExpression() {
            TokenType tt = it.peek().getType();

            if (tt == TokenType.OPERATOR_NOT ||
                    tt == TokenType.OPERATOR_SUBTRACT ||
                    tt == TokenType.OPERATOR_ADD) {

                Token t = it.next();
                ExpressionNode expr = parseUnaryExpression();

                UnaryExpressionNode node = new UnaryExpressionNode();
                node.setText(t.getLexeme());
                node.setType(NodeType.UNARY);//TODO
                node.setOperand1(expr);

                return node;
            }

            return parseAtomicExpression();
        }


        private ExpressionNode parseAtomicExpression() {
            Token t = it.peek();

            // literal
            if (t.getType() == TokenType.LITERAL_INT ||
                    t.getType() == TokenType.LITERAL_FLOAT ||
                    t.getType() == TokenType.LITERAL_CHAR ||
                    t.getType() == TokenType.LITERAL_STRING ||
                    t.getType() == TokenType.LITERAL_TRUE ||
                    t.getType() == TokenType.LITERAL_FALSE) {

                it.next();
                LiteralNode literalNode = new LiteralNode();
                literalNode.setType(NodeType.LITERAL);
                literalNode.setText(t.getLexeme());
                return literalNode;
            }

            // identifier
            if (t.getType() == TokenType.IDENTIFIER) {
                it.next();
                IdentifierNode identifierNode = new IdentifierNode();
                identifierNode.setType(NodeType.IDENTIFIER);
                identifierNode.setText(t.getLexeme());
                return identifierNode;
            }

            // parenthesis
            if (t.getType() == TokenType.DELIMITER_LPARENTH) {
                it.next();
                ExpressionNode expr = parseAssignment();
                if (it.peek().getType() == TokenType.DELIMITER_RPARENTH) {
                    it.next();
                    return expr;
                }
            }

            throw new RuntimeException("Unexpected token: " + t.getLexeme());
        }

}
