package com.gabrieljamesbenedict.LexicalAnalysis;

import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static java.util.Map.entry;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class LexicalAnalyzer {

    private final PushbackReader codeReader;

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

    private static final Set<Character> OPERATORS = Set.of(
        '+','-','*','/','%'
    );

    public Stream<Token> tokenize() throws IOException {

        StringBuilder symbol = new StringBuilder();
        ArrayList<Token> tokenArrayList = new ArrayList<>();

        final String literalRegex = "true|false|[0-9]+|[0-9]+\\.[0-9]+|^\"(.)*\"$|^\'(.)*\'$";
        Pattern pattern = Pattern.compile(literalRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher;

        while (codeReader.ready()) {
            char c = (char) codeReader.read();

            boolean isWhitespace = (c == ' ' || c == '\t' || c == '\r');
            boolean isDelimiter  = DELIMITERS.containsKey(String.valueOf(c));
            boolean isOperator   = OPERATORS.contains(c);
            boolean isTokenBreak = isWhitespace || isDelimiter || isOperator;

            String current = symbol.toString();

//            // Comments
//            if (current.equals("//")) {
//                while (codeReader.ready() && c != '\n') {
//                    c = (char) codeReader.read();
//                }
//                symbol.setLength(0);
//                continue;
//            }

            // Newlines
            if (c == '\n') {
                tokenArrayList.add(new Token("newline", TokenType.NEWLINE));
                symbol.setLength(0);
                continue;
            }

//            // Increment and Decrement
//            if (current.equals("++") || current.equals("--")) {
//                addToken(current, KEYWORDS, tokenArrayList);
//                symbol.setLength(0);
//                continue;
//            }
//            if (current.endsWith("++") || current.endsWith("--")) {
//                String base = current.substring(0, current.length() - 2);
//                String op   = current.substring(current.length() - 2);
//
//                addToken(base, KEYWORDS, tokenArrayList);
//                addToken(op, KEYWORDS, tokenArrayList);
//
//                symbol.setLength(0);
//                continue;
//            }

//            // Multiline Comments
//            if (current.endsWith("/*")) {
//                String beforeComment = current.substring(0, current.length() - 2);
//                if (!beforeComment.isEmpty()) addToken(beforeComment, KEYWORDS, tokenArrayList);
//
//                while (codeReader.ready() && !symbol.toString().endsWith("*/")) {
//                    c = (char) codeReader.read();
//                    symbol.append(c);
//                }
//
//                symbol.setLength(0);
//                continue;
//            }

            // Token
            if (isTokenBreak) {
                if (!current.isEmpty()) {
                    if (current.startsWith("-") && current.length() > 1) {
                        tokenArrayList.add(new Token("neg", TokenType.OPERATOR_NEGATIVE));
                        current = current.substring(1);
                    }
                    matcher = pattern.matcher(current);
                    if (matcher.matches()) {
                        addToken(current, TokenType.LITERAL, tokenArrayList);
                    } else {
                        addToken(current, KEYWORDS, tokenArrayList);
                    }
                    symbol.setLength(0);
                }

                if (isDelimiter) {
                    addToken(String.valueOf(c), DELIMITERS, tokenArrayList);
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

                    boolean isIncrement =
                            c == '+'
                                    && check == '+';
                    boolean isDecrement =
                            c == '-'
                                    && codeReader.ready()
                                    && check == '-';

                    if (Character.isLetterOrDigit(check)) {
                        addToken("neg_op", TokenType.OPERATOR_NEGATIVE, tokenArrayList);
                        codeReader.unread(check);
                        continue;
                    }

                    if (isIncrement) {
                        addToken("++", TokenType.OPERATOR_INCREMENT, tokenArrayList);
                        continue;
                    } else if (isDecrement) {
                        addToken("--", TokenType.OPERATOR_DECREMENT, tokenArrayList);
                        continue;
                    }
                    else codeReader.unread(check);
                    addToken(String.valueOf(c), KEYWORDS, tokenArrayList);
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

    private static void addToken(String lexeme, Map<String, TokenType> lookup, List<Token> list) {
        TokenType type = lookup.getOrDefault(lexeme, TokenType.IDENTIFIER);
        list.add(new Token(lexeme, type));
    }
    private static void addToken(String lexeme, TokenType type, List<Token> list) {
        list.add(new Token(lexeme, type));
    }
}


