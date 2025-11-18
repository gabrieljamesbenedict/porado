package com.gabrieljamesbenedict.porado.token;

import com.gabrieljamesbenedict.porado.util.PeekableIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class TokenPostProcessor {

    Stream<Token> tokenStream;
    private Token mergeTarget;
    private TokenType[] mergeParameters;

    public TokenPostProcessor startProcess(Stream<Token> tokenStream) {
        this.tokenStream = tokenStream;

        return this;
    }

    public TokenPostProcessor removeTokenType(TokenType type) {

        tokenStream = tokenStream.filter(
                token -> token.getType() != type
        );

        return this;
    }

    public TokenPostProcessor mergeTokens(String lexeme, TokenType type, TokenType... types) {
        if (types.length < 2) throw new IllegalArgumentException("");

        PeekableIterator<Token> it = new PeekableIterator<>(new ArrayList<>(tokenStream.toList()));
        List<Token> accumulator = new ArrayList<>();

        int typeSize = types.length;
        Token[] next = new Token[typeSize];

        while (it.hasNext()) {
            boolean nextMatch = true;
            for (int i = 0; i < typeSize; i++) {
                next[i] = it.ahead(i);
                if (next[i] == null) continue;
                nextMatch = nextMatch && next[i].getType() == types[i];
            }

            if (nextMatch) {
                assert next[0] != null;
                accumulator.add(new Token(lexeme, type, next[0].col, next[0].row));
                for (int i = 0; i < typeSize; i++) {
                    it.next();
                }
                continue;
            }

            accumulator.add(it.next());
       }

        tokenStream = accumulator.stream();
        return this;
    }

    public TokenPostProcessor convertToLiteral() {


        String intRegex = "\\d+";
        String floatRegex = "\\d*\\.\\d+";
        String charRegex = "'.?'";
        String stringRegex = "\"(\\\\.|[^\"\\\\])*\"";

        tokenStream = tokenStream.peek(token -> {
            if (token.getType() == TokenType.IDENTIFIER) {
                if (Pattern.matches(intRegex, token.getLexeme())) token.setType(TokenType.LITERAL_INT);
                if (Pattern.matches(floatRegex, token.getLexeme())) token.setType(TokenType.LITERAL_FLOAT);
                if (Pattern.matches(charRegex, token.getLexeme())) token.setType(TokenType.LITERAL_CHAR);
                if (Pattern.matches(stringRegex, token.getLexeme())) token.setType(TokenType.LITERAL_STRING);
            }
        });

        return this;
    }

    public Stream<Token> collect() {
        return tokenStream;
    }
}
