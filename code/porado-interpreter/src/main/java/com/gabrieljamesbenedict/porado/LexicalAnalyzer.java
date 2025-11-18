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
    private final ArrayList<Token> tokenArrayList = new ArrayList<>();

    public List<Token> tokenize() {
        int col = 0;
        int row = 0;

        tokenArrayList.clear();
        tokenArrayList.add(new Token("PROGRAM", TokenType.PROGRAM, 0, 0));

        PeekableIterator<Character> it = new PeekableIterator<>(charList);
        StringBuilder sb = new StringBuilder(); // buffer
        while (it.hasNext()) {
            char c = it.next();

            col++;
            if (c == '\n') {
                col = 0;
                row++;
            }

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
                createToken(current, col-current.length(), row);
                sb.setLength(0);
                createToken(c, col, row);
                continue;
            }

            sb.append(c);
        }

        tokenArrayList.add(new Token("EOF", TokenType.EOF, col, row));

        TokenPostProcessor postProcessor = new TokenPostProcessor();
        return postProcessor.startProcess(tokenArrayList.stream())
                .convertToLiteral()
                .removeTokenType(TokenType.WHITESPACE)
                .removeTokenType(TokenType.CARRIAGE_RETURN)
                .removeTokenType(TokenType.LINEFEED)
                .mergeTokens("++", TokenType.OPERATOR_INCREMENT, TokenType.OPERATOR_ADD, TokenType.OPERATOR_ADD)
                .mergeTokens("--", TokenType.OPERATOR_DECREMENT, TokenType.OPERATOR_SUBTRACT, TokenType.OPERATOR_SUBTRACT)
                .mergeTokens("==", TokenType.OPERATOR_ASSIGN, TokenType.OPERATOR_ASSIGN, TokenType.OPERATOR_ASSIGN)
                .mergeTokens("+=", TokenType.OPERATOR_ASSIGN_ADD, TokenType.OPERATOR_ADD, TokenType.OPERATOR_ASSIGN)
                .mergeTokens("-=", TokenType.OPERATOR_ASSIGN_SUBTRACT, TokenType.OPERATOR_SUBTRACT, TokenType.OPERATOR_ASSIGN)
                .mergeTokens("*=", TokenType.OPERATOR_ASSIGN_MULTIPLY, TokenType.OPERATOR_MULTIPLY, TokenType.OPERATOR_ASSIGN)
                .mergeTokens("/=", TokenType.OPERATOR_ASSIGN_DIVIDE, TokenType.OPERATOR_DIVIDE, TokenType.OPERATOR_ASSIGN)
                .mergeTokens("%=", TokenType.OPERATOR_ASSIGN_MODULO, TokenType.OPERATOR_MODULO, TokenType.OPERATOR_ASSIGN)
                .collect()
                .toList();
    }

    private void createToken(String s, int col, int row) {
        if (s.isEmpty()) return;
        Token token = new Token(s, TokenType.mapToType(s), col, row);
        tokenArrayList.add(token);
    }

    private void createToken(char c, int col, int row) {
        switch (c) {
            case '\t' -> createToken("\\tn", col, row);
            case '\n' -> createToken("\\n", col, row);
            case '\r' -> createToken("\\r", col, row);
            default -> createToken(String.valueOf(c), col, row);
        }
    }

}
