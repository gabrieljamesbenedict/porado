package com.gabrieljamesbenedict.LexicalAnalysis;

import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Token {

    private String lexeme;
    private TokenType type;
    private TokenCategory category;

    @Override
    public String toString() {
        return "Token(lexeme=\'"+lexeme+"\', type="+type.toString()+", category="+category.toString()+")";
    }

}
