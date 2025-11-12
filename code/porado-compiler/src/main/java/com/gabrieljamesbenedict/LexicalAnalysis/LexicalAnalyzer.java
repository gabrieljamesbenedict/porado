package com.gabrieljamesbenedict.LexicalAnalysis;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import static java.util.Map.entry;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class LexicalAnalyzer {

    private final BufferedReader codeReader;

    private static final Map<String, TokenType> KEYWORDS = Map.<String, TokenType>ofEntries(
            // Keywords
            entry("as", TokenType.KEYWORD_AS),
            entry("strict", TokenType.KEYWORD_STRICT),
            entry("fixed", TokenType.KEYWORD_FIXED),
            entry("array", TokenType.KEYWORD_ARRAY),
            entry("of", TokenType.KEYWORD_OF),
            entry("if", TokenType.KEYWORD_IF),
            entry("then", TokenType.KEYWORD_THEN),
            entry("else", TokenType.KEYWORD_ELSE),
            entry("switch", TokenType.KEYWORD_SWITCH),
            entry("case", TokenType.KEYWORD_CASE),
            entry("default", TokenType.KEYWORD_DEFAULT),
            entry("while", TokenType.KEYWORD_WHILE),
            entry("until", TokenType.KEYWORD_UNTIL),
            entry("do", TokenType.KEYWORD_DO),
            entry("for", TokenType.KEYWORD_FOR),
            entry("each", TokenType.KEYWORD_EACH),
            entry("in", TokenType.KEYWORD_IN),
            entry("repeat", TokenType.KEYWORD_REPEAT),
            entry("with", TokenType.KEYWORD_WITH),
            entry("break", TokenType.KEYWORD_BREAK),
            entry("continue", TokenType.KEYWORD_CONTINUE),
            entry("function", TokenType.KEYWORD_FUNCTION),
            entry("accepts", TokenType.KEYWORD_ACCEPTS),
            entry("returns", TokenType.KEYWORD_RETURNS),
            entry("return", TokenType.KEYWORD_RETURN),
            entry("print", TokenType.KEYWORD_PRINT),
            entry("true", TokenType.KEYWORD_TRUE),
            entry("false", TokenType.KEYWORD_FALSE),

            // Data Types
            entry("int", TokenType.TYPE_INT),
            entry("float", TokenType.TYPE_FLOAT),
            entry("char", TokenType.TYPE_CHAR),
            entry("string", TokenType.TYPE_STRING),
            entry("boolean", TokenType.TYPE_BOOLEAN),

            // Operators
            entry("+", TokenType.OPERATOR_PLUS),
            entry("-", TokenType.OPERATOR_MINUS),
            entry("*", TokenType.OPERATOR_TIMES),
            entry("/", TokenType.OPERATOR_DIVIDE),
            entry("%", TokenType.OPERATOR_MODULO),
            entry("++", TokenType.OPERATOR_INCREMENT),
            entry("--", TokenType.OPERATOR_DECREMENT),
            entry("=", TokenType.OPERATOR_ASSIGN),
            entry("+=", TokenType.OPERATOR_ASSIGNPLUS),
            entry("-=", TokenType.OPERATOR_ASSIGNMINUS),
            entry("*=", TokenType.OPERATOR_ASSIGNTIMES),
            entry("/=", TokenType.OPERATOR_ASSIGNDIVIDE),
            entry("%=", TokenType.OPERATOR_ASSIGNMODULO),
            entry("==", TokenType.OPERATOR_EQUALS),
            entry("!=", TokenType.OPERATOR_NOTEQUALS),
            entry(">", TokenType.OPERATOR_GREATER),
            entry("<", TokenType.OPERATOR_LESSER),
            entry(">=", TokenType.OPERATOR_GREATEREQUALS),
            entry("<=", TokenType.OPERATOR_LESSERQUALS),
            entry("not", TokenType.OPERATOR_NOT),
            entry("and", TokenType.OPERATOR_AND),
            entry("nand", TokenType.OPERATOR_NAND),
            entry("or", TokenType.OPERATOR_OR),
            entry("nor", TokenType.OPERATOR_NOR),
            entry("xor", TokenType.OPERATOR_XOR),
            entry("xnor", TokenType.OPERATOR_XNOR)
    );

    private static final Map<String, TokenType> DELIMITERS = Map.<String, TokenType>ofEntries(
        entry("{", TokenType.DELIMITER_LBRACE),
        entry("}", TokenType.DELIMITER_RBRACE),
        entry("[", TokenType.DELIMITER_LBRACKET),
        entry("]", TokenType.DELIMITER_RBRACKET),
        entry("(", TokenType.DELIMITER_LPARENTH),
        entry(")", TokenType.DELIMITER_RPARENTH),
        entry(",", TokenType.DELIMITER_COMMA),
        entry(";", TokenType.DELIMITER_SEMICOLON)
    );

    public Stream<Token> tokenize() throws IOException {

        StringBuilder symbol = new StringBuilder();
        ArrayList<Token> tokenArrayList = new ArrayList<>();

        while (codeReader.ready()) {
            char c = (char) codeReader.read();
            boolean whitespace = (c == ' ' || c == '\t' || c == '\r');
            boolean delimiter = DELIMITERS.containsKey(c+"");
            boolean checkToken = whitespace || delimiter;

            if (symbol.toString().equals("//")) {
                while (c != '\n') {
                    c = (char) codeReader.read();
                }

                symbol.setLength(0);
                continue;
            }

            if (c == '\n') {
                String lexeme = "newline";
                TokenType type = TokenType.NEWLINE;
                Token token = new Token(lexeme, type);
                tokenArrayList.add(token);

                continue;
            }

            boolean checkPreUnaryOperator
            if (checkToken || symbol.toString().endsWith("/*")) {
                String symbolStr = symbol.toString();
                if (symbol.toString().endsWith("/*")) {
                    symbolStr = symbolStr.substring(0, symbolStr.length()-2);

                    while (!symbol.toString().endsWith("*/")) {
                        c = (char) codeReader.read();
                        symbol.append(c);
                    }

                    symbol.setLength(0);
                }

                if (!symbolStr.isEmpty()) {
                    TokenType type = KEYWORDS.getOrDefault(symbolStr, TokenType.IDENTIFIER);
                    Token token = new Token(symbolStr, type);
                    tokenArrayList.add(token);
                    symbol.setLength(0);
                }

                if (delimiter) {
                    String lexeme = c + "";
                    TokenType type = DELIMITERS.get(c + "");
                    Token token = new Token(lexeme, type);
                    tokenArrayList.add(token);
                    symbol.setLength(0);
                }

                continue;
            }

            symbol.append(c);
        }

        Token eof = Token
                .builder()
                .lexeme("eof")
                .type(TokenType.EOF)
                .build();
        tokenArrayList.add(eof);

        return tokenArrayList.stream();
    }
}


