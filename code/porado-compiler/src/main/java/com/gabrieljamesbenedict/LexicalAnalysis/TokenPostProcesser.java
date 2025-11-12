package com.gabrieljamesbenedict.LexicalAnalysis;

import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public class TokenPostProcesser {

    public Stream<Token> clean(Stream<Token> tokenStream) {
        return tokenStream
                .filter(token -> token.getType() != TokenType.NEWLINE);
    }

}
