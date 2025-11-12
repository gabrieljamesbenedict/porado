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

    @Override
    public String toString() {
        return "Token(lexeme=\'"+lexeme+"\', type="+type.toString()+")";
    }

}
