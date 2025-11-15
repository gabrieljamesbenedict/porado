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
    private boolean doLogging = false;
    Token lastConsumed = null;

    TokenIterator(Stream<Token> tokenStream) {
        this.tokens = tokenStream.collect(Collectors.toCollection(ArrayDeque::new));
    }

    Token peek() {
        Token token = tokens.peekFirst();
        if (doLogging && token != null) {
            System.out.println("Peek: " + token.getLexeme());
        }
        return token;
    }

    public Token lookahead(int n) {
        Token token = tokens.stream().skip(n).findFirst().orElse(null);
        if (doLogging && token != null) {
            System.out.println("Lookahead: " + token.getLexeme());
        }
        return token;
    }

    Token next() {
        Token token = tokens.pollFirst();
        lastConsumed = token;
        if (doLogging && token != null) {
            System.out.println("Next: " + token.getLexeme());
        }
        return token;
    }

    boolean match(TokenType... types) throws CompileException {
        String typeMatch = Arrays.stream(types).map(TokenType::toString).reduce((s1,s2)->s1 + ", " + s2).orElseThrow(() -> new CompileException("Syntax Error: Expected match value but none found"));
        Token next = peek();
        if (next == null) return false;
        if (doLogging) {
            System.out.println("Match: " + next.getLexeme() + " to " + typeMatch);
        }
        boolean isMatch = false;
        for (TokenType type : types) {
            if (next.getType() == type) {
                if (doLogging) {
                    System.out.println("Match True");
                }
                lastConsumed = tokens.pollFirst();
                return true;
            }
        }
        return isMatch;
    }

    public Token expect(TokenType... expecteds) throws CompileException {
        String typeMatch = Arrays.stream(expecteds).map(TokenType::toString).reduce((s1,s2)->s1 + ", " + s2).orElseThrow(() -> new CompileException("Syntax Error: Expected match value but none found"));
        Token token = next();
        lastConsumed = token;
        if (doLogging) {
            System.out.println("Match: " + token.getLexeme() + " to " + typeMatch);
        }
        boolean isError = true;
        for (TokenType expected : expecteds) {
            if (token.getType() == expected) {
                isError = false;
            }
        }
        if (isError) {
            throw new CompileException(
                    "Syntax Error: Expected " + typeMatch + " but found " + token.getType()
            );
        }
        lastConsumed = token;
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
