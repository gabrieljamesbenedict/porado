package com.gabrieljamesbenedict.porado;

import com.gabrieljamesbenedict.porado.token.Token;
import com.gabrieljamesbenedict.porado.token.TokenPostProcessor;
import com.gabrieljamesbenedict.porado.token.TokenType;
import com.gabrieljamesbenedict.porado.util.PeekableIterator;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class LexicalAnalyzer {

    private final List<Character> charList;
    ArrayList<Token> tokenArrayList = new ArrayList<>();

    public Stream<Token> tokenize() {
        tokenArrayList.clear();
        tokenArrayList.add(new Token("PROGRAM", TokenType.PROGRAM));

        PeekableIterator<Character> it = new PeekableIterator<>(charList);
        StringBuilder sb = new StringBuilder(); // buffer
        while (it.hasNext()) {
            char c = it.next();

            // Comments
            if (c == '/' && it.peek() == '/') {
                while (!sb.toString().endsWith("\n")) {
                    sb.append(it.next());
                }
                sb.setLength(0);
                continue;

            } else if (c == '/' && it.peek() == '*') {
                while (!sb.toString().endsWith("*/")) {
                    sb.append(it.next());
                }
                sb.setLength(0);
                continue;
            }

            boolean isWhitespace = Set.of(' ', '\t', '\n', '\r').contains(c);
            boolean isDelimiter = Set.of('(',')','[',']','{','}',',',';').contains(c);
            boolean isOperator = Set.of('+','-','*','/','%','=').contains(c);

            String current = sb.toString();

            if (isWhitespace || isDelimiter || isOperator) {
                createToken(current);
                sb.setLength(0);
                createToken(c);
                continue;
            }

            sb.append(c);
        }

        tokenArrayList.add(new Token("EOF", TokenType.EOF));

        Token incrementToken = new Token("++", TokenType.OPERATOR_INCREMENT);
        Token decrementToken = new Token("--", TokenType.OPERATOR_DECREMENT);
        Token assignmentToken = new Token("==", TokenType.OPERATOR_ASSIGN);
        Token addAssignmentToken = new Token("+=", TokenType.OPERATOR_ASSIGN_ADD);
        Token subtAssignmentToken = new Token("-=", TokenType.OPERATOR_ASSIGN_SUBTRACT);
        Token multAssignmentToken = new Token("*=", TokenType.OPERATOR_ASSIGN_MULTIPLY);
        Token divAssignmentToken = new Token("/=", TokenType.OPERATOR_ASSIGN_DIVIDE);
        Token modAssignmentToken = new Token("%=", TokenType.OPERATOR_ASSIGN_MODULO);

        TokenPostProcessor postProcessor = new TokenPostProcessor();
        return postProcessor.startProcess(tokenArrayList.stream())
                .convertToLiteral()
                .removeTokenType(TokenType.WHITESPACE)
                .removeTokenType(TokenType.CARRIAGE_RETURN)
                .removeTokenType(TokenType.LINEFEED)
                .mergeTokens(incrementToken, TokenType.OPERATOR_ADD, TokenType.OPERATOR_ADD)
                .mergeTokens(decrementToken, TokenType.OPERATOR_SUBTRACT, TokenType.OPERATOR_SUBTRACT)
                .mergeTokens(assignmentToken, TokenType.OPERATOR_ASSIGN, TokenType.OPERATOR_ASSIGN)
                .mergeTokens(addAssignmentToken, TokenType.OPERATOR_ADD, TokenType.OPERATOR_ASSIGN)
                .mergeTokens(subtAssignmentToken, TokenType.OPERATOR_SUBTRACT, TokenType.OPERATOR_ASSIGN)
                .mergeTokens(multAssignmentToken, TokenType.OPERATOR_MULTIPLY, TokenType.OPERATOR_ASSIGN)
                .mergeTokens(divAssignmentToken, TokenType.OPERATOR_DIVIDE, TokenType.OPERATOR_ASSIGN)
                .mergeTokens(modAssignmentToken, TokenType.OPERATOR_MODULO, TokenType.OPERATOR_ASSIGN)
                .collect();
    }

    private void createToken(String s) {
        if (s.isEmpty()) return;
        Token token = new Token(s, TokenType.mapToType(s));
        tokenArrayList.add(token);
    }

    private void createToken(char c) {
        switch (c) {
            case '\t' -> createToken("\\tn");
            case '\n' -> createToken("\\n");
            case '\r' -> createToken("\\r");
            default -> createToken(String.valueOf(c));
        }
    }

}
