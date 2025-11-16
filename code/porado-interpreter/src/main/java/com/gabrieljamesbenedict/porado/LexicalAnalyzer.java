package com.gabrieljamesbenedict.porado;

import com.gabrieljamesbenedict.porado.token.Token;
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
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            char c = it.next();

            if (c == '/' && it.peek() == '/') {
                while (!sb.toString().endsWith("\n")) {
                    sb.append(it.next());
                }
                System.out.println("Comment: " + sb);
                sb.setLength(0);

                continue;
            }

            boolean isWhitespace = Set.of(' ', '\t', '\n', '\r').contains(c);
            boolean isDelimiter = Set.of('(',')','[',']','{','}',',',';').contains(c);
            boolean isOperator = Set.of('+','-','*','/','%').contains(c);

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
        return tokenArrayList.stream();
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
