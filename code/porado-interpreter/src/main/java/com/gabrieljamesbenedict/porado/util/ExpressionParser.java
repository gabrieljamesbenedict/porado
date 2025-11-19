package com.gabrieljamesbenedict.porado.util;

import com.gabrieljamesbenedict.porado.node.ExpressionNode;

public class ExpressionParser {

    private ExpressionParser() {}

    public static ExpressionNode parse() {
        ExpressionParser parser = new ExpressionParser();

        return parser.parseAssignment();
    }

    private ExpressionNode parseAssignment() {
        ExpressionNode left = parseOrExpression();

        return left;
    }

    private ExpressionNode parseOrExpression() {
        ExpressionNode left = parseXorExpression();

        return left;
    }

    private ExpressionNode parseXorExpression() {
        ExpressionNode left = parseAndExpression();

        return left;
    }

    private ExpressionNode parseAndExpression() {
        ExpressionNode left = parseEqualityExpression();

        return left;
    }

    private ExpressionNode parseEqualityExpression() {
        ExpressionNode left = parseInequalityExpression();

        return left;
    }

    private ExpressionNode parseInequalityExpression() {
        ExpressionNode left = parseAdditiveExpression();

        return left;
    }

    private ExpressionNode parseAdditiveExpression() {
        ExpressionNode left = parseMultiplicativeExpression();

        return left;
    }

    private ExpressionNode parseMultiplicativeExpression() {
        ExpressionNode left = parseUnaryExpression();

        return left;
    }

    private ExpressionNode parseUnaryExpression() {
        ExpressionNode left = parseAtomicExpression();

        return left;
    }

    private ExpressionNode parseAtomicExpression() {
        ExpressionNode left = parseOrExpression();

        return left;
    }

}
