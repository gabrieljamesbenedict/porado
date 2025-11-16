package com.gabrieljamesbenedict.porado;

import com.gabrieljamesbenedict.porado.token.Token;
import com.gabrieljamesbenedict.porado.token.TokenType;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class LexicalAnalyzer {

    private final Stream<Character> charStream;
    ArrayList<Token> tokenArrayList = new ArrayList<>();

    public Stream<Token> tokenize() {
        tokenArrayList.clear();

        Iterator<Character> it = charStream.iterator();
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            char c = it.next();

            boolean isWhitespace = Set.of(' ', '\t', '\n', '\r').contains(c);
            boolean isDelimiter = Set.of('(',')','[',']','{','}',',',';').contains(c);

            String current = sb.toString();

            if (isWhitespace || isDelimiter) {
                createToken(current);
                sb.setLength(0);

                createToken(c);
                continue;
            }

            sb.append(c);
        }

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
