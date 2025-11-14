package com.gabrieljamesbenedict.SyntaxAnalysis;

import com.gabrieljamesbenedict.Exceptions.CompileException;
import com.gabrieljamesbenedict.LexicalAnalysis.Token;
import com.gabrieljamesbenedict.LexicalAnalysis.TokenType;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TokenIterator {

    private final Deque<Token> tokens;

    Token lastConsumed = null;

    TokenIterator(Stream<Token> tokenStream) {
        this.tokens = tokenStream.collect(Collectors.toCollection(ArrayDeque::new));
    }

    Token peek() {
        Token token = tokens.peekFirst();
//        if (token != null)
//            System.out.println("Peeked Token: " + token.getLexeme());
//        else
//            System.out.println("Peeked Token: EOF");
        return token;
    }

    public Token lookahead(int n) {
        Token token = tokens.stream().skip(n).findFirst().orElse(null);
        //if (token != null) System.out.println("Lookahead Token: " + token.getLexeme());
        return token;
    }

    Token next() {
        Token token = tokens.pollFirst();
        lastConsumed = token;
        //if (token != null) System.out.println("Peeked Token: " + token.getLexeme());
        return token;
    }

    boolean match(TokenType type) {
        Token next = peek();
        if (next == null) return false;
        //System.out.println("Match Token: " + next.getLexeme() + " To: " + type.toString() + "=" + (next.getType()==type));
        if (next.getType() == type) {
            tokens.pollFirst();
            return true;
        }
        return false;
    }

    public Token expect(TokenType... expecteds) throws CompileException {
        String allExpected = Arrays.stream(expecteds).map(Enum::toString).reduce((s1, s2) -> s1 + " " + s2).orElse("");
        Token token = next();
        lastConsumed = token;
        boolean isError = true;
        for (TokenType expected : expecteds) {
            if (token.getType() == expected) {
                isError = false;
            }
        }
        if (isError) {
            throw new CompileException(
                    "Expected: [" + allExpected + "] but found " + token.getType()
            );
        }
        return token;
    }

    boolean hasNext() {
        return peek() != null;
    }

    boolean eof() {
        return tokens.isEmpty();
    }

    public Token previous() {
        if (lastConsumed == null) {
            throw new IllegalStateException("No previous token: next() has not been called yet.");
        }
        return lastConsumed;
    }
}
