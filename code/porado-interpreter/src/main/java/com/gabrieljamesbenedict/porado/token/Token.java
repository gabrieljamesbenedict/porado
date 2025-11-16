package com.gabrieljamesbenedict.porado.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
public class Token {

    String lexeme;
    TokenType type;

}
