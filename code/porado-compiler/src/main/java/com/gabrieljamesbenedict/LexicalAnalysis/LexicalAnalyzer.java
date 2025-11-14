package com.gabrieljamesbenedict.LexicalAnalysis;

import jdk.jfr.Category;
import lombok.RequiredArgsConstructor;

import javax.xml.catalog.Catalog;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.*;

import static java.util.Map.entry;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class LexicalAnalyzer {

    private static Deque<Character> history = new ArrayDeque<>();

    private static final Map<String, TokenType> KEYWORDS = Map.<String, TokenType>ofEntries(
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
            entry("print", TokenType.KEYWORD_PRINT)
    );

    private static final Map<String, TokenType> TYPES = Map.<String, TokenType>ofEntries(
            entry("int", TokenType.TYPE_INT),
            entry("float", TokenType.TYPE_FLOAT),
            entry("char", TokenType.TYPE_CHAR),
            entry("string", TokenType.TYPE_STRING),
            entry("boolean", TokenType.TYPE_BOOLEAN)
    );

    private static final Map<String, TokenType> OPERATORS = Map.<String, TokenType>ofEntries(
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
        entry(":", TokenType.DELIMITER_COLON),
        entry(";", TokenType.DELIMITER_SEMICOLON)
    );

    private static final Set<Character> operators = Set.of(
        '+','-','*','/','%'
    );

    public static Stream<Token> tokenize(PushbackReader codeReader) throws IOException {

        StringBuilder symbol = new StringBuilder();
        ArrayList<Token> tokenArrayList = new ArrayList<>();

        while (codeReader.ready()) {
            char c = (char) codeReader.read();
            history.push(c);

            boolean isWhitespace = (c == ' ' || c == '\t' || c == '\r');
            boolean isDelimiter  = DELIMITERS.containsKey(String.valueOf(c));
            boolean isOperator   = operators.contains(c);
            boolean isTokenBreak = isWhitespace || isDelimiter || isOperator;

            String current = symbol.toString();

            if (c == '\n') {
                tokenArrayList.add(new Token("newline", TokenType.NEWLINE, TokenCategory.NEWLINE));
                symbol.setLength(0);
                continue;
            }

            if (isWhitespace) {
                tokenArrayList.add(new Token("whitespace", TokenType.WHITESPACE, TokenCategory.WHITESPACE));
            }

            if (isTokenBreak) {

                if (!current.isEmpty()) {
                    addToken(current, tokenArrayList);
                    symbol.setLength(0);
                }

                if (isDelimiter) {
                    addToken(String.valueOf(c), tokenArrayList);
                } else if (isOperator) {
                    char check = (char) codeReader.read();

                    boolean isSingleComment = c == '/' && check == '/';
                    boolean isMultiComment = c == '/' && check == '*';

                    if (isSingleComment) {
                        while (codeReader.ready() && c != '\n') {
                            c = (char) codeReader.read();
                            symbol.append(c);
                        }
                        symbol.setLength(0);
                        continue;

                    } else if (isMultiComment) {
                        while (codeReader.ready() && !symbol.toString().endsWith("*/")) {
                            c = (char) codeReader.read();
                            symbol.append(c);
                        }
                        symbol.setLength(0);
                        continue;
                    }

                    boolean isIncrement = c == '+' && check == '+';
                    boolean isDecrement = c == '-' && check == '-';

                    if (isIncrement) {
                        addToken("++", tokenArrayList);
                        continue;
                    } else if (isDecrement) {
                        addToken("--", tokenArrayList);
                        continue;
                    }
                    else codeReader.unread(check);
                    addToken(String.valueOf(c), tokenArrayList);
                }

                continue;
            }

            symbol.append(c);
        }

        Token eof = Token
                .builder()
                .lexeme("eof")
                .type(TokenType.EOF)
                .category(TokenCategory.EOF)
                .build();
        tokenArrayList.add(eof);
        TokenPostProcesser postProcesser = new TokenPostProcesser();

        return postProcesser.clean(tokenArrayList.stream());
    }


    private static void addToken(String lexeme, List<Token> list) {
        TokenType type;
        TokenCategory category;
        if (KEYWORDS.containsKey(lexeme)) {
            type = KEYWORDS.get(lexeme);
            category = TokenCategory.KEYWORD;
        } else if (TYPES.containsKey(lexeme)) {
            type = TYPES.get(lexeme);
            category = TokenCategory.TYPE;
        } else if (OPERATORS.containsKey(lexeme)) {
            type = OPERATORS.get(lexeme);
            category = TokenCategory.OPERATOR;
        } else if (DELIMITERS.containsKey(lexeme)) {
            type = DELIMITERS.get(lexeme);
            category = TokenCategory.DELIMITER;
        } else {
            type = TokenType.IDENTIFIER;
            category = TokenCategory.IDENTIFIER;
        }

        if (lexeme.equals("negative")) {
            type = TokenType.OPERATOR_NEGATIVE;
            category = TokenCategory.OPERATOR;
        }

        list.add(new Token(lexeme, type, category));
    }
}


