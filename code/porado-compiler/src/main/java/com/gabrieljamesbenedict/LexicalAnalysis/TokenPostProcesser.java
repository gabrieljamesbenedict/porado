package com.gabrieljamesbenedict.LexicalAnalysis;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class TokenPostProcesser {

    public Stream<Token> clean(Stream<Token> tokenStream) {

        List<Token> tokenList = tokenStream.toList();
        for (int i = 1; i < tokenList.size()-1; i++) {
            if (
                    (tokenList.get(i-1).getCategory() != TokenCategory.LITERAL && tokenList.get(i-1).getCategory() != TokenCategory.IDENTIFIER)
                    && (tokenList.get(i-1).getType() != TokenType.OPERATOR_INCREMENT || tokenList.get(i-1).getType() != TokenType.OPERATOR_DECREMENT)
                    && (tokenList.get(i).getType() == TokenType.OPERATOR_MINUS)
            ) {
                tokenList.get(i).setType(TokenType.OPERATOR_NEGATIVE);
            }
        }

        return tokenList.stream()
                .peek(
                        token -> {
                            if (checkIfLiteral(token.getLexeme()) != null) {
                                token.setType(checkIfLiteral(token.getLexeme()));
                                token.setCategory(TokenCategory.LITERAL);
                            }
                        }
                )
                .peek(
                        token -> {
                            String newLexeme = switch (token.getType()) {
                                case TokenType.LITERAL_CHAR -> token.getLexeme().replace("'", "");
                                case TokenType.LITERAL_STRING -> token.getLexeme().replace("\"", "");
                                default -> token.getLexeme();
                            };
                            token.setLexeme(newLexeme);
                        }
                ).filter(
                        token -> token.getCategory() != TokenCategory.NEWLINE
                ).filter(
                        token -> token.getCategory() != TokenCategory.WHITESPACE
                );
    }

    private TokenType checkIfLiteral (String compare) {
        final String literalInt = "[0-9]+";
        final String literalFloat = "[0-9]+\\.[0-9]+";
        final String literalChar = "^\'(.)*\'$";
        final String literalString = "^\"(.)*\"$";
        final String literalTrue = "true";
        final String literalFalse = "false";
        final String[] literalRegexArray = {
                literalInt, literalFloat, literalChar, literalString, literalTrue, literalFalse
        };

        int type = -1;
        for (int i = 0; i < 6; i++) {
            Pattern pattern = Pattern.compile(literalRegexArray[i], Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(compare);
            if (matcher.matches()) {
                type = i;
                break;
            }
        }

        return switch (type) {
            case 0 -> TokenType.LITERAL_INT;
            case 1 -> TokenType.LITERAL_FLOAT;
            case 2 -> TokenType.LITERAL_CHAR;
            case 3 -> TokenType.LITERAL_STRING;
            case 4 -> TokenType.LITERAL_TRUE;
            case 5 -> TokenType.LITERAL_FALSE;
            default -> null;
        };
    }

}
