package com.gabrieljamesbenedict.porado.token;

public enum TokenType {

    PROGRAM,

    WHITESPACE,
    LINEFEED,
    CARRIAGE_RETURN,

    IDENTIFER,

    KEYWORD_AS,
    KEYWORD_ARRAY,
    KEYWORD_OF,
    KEYWORD_FUNCTION,
    KEYWORD_ACCEPTS,
    KEYWORD_RETURNS,

    KEYWORD_IF,
    KEYWORD_ELSE,
    KEYWORD_ELSE_IF,
    KEYWORD_SWITCH,
    KEYWORD_CASE,
    KEYWORD_DEFAULT,

    KEYWORD_WHILE,
    KEYWORD_UNTIL,
    KEYWORD_DO,
    KEYWORD_FOR,
    KEYWORD_EACH,
    KEYWORD_IN,
    KEYWORD_REPEAT,

    OPERATOR_ASSIGN,
    OPERATOR_ASSIGN_ADD,
    OPERATOR_ASSIGN_SUBTRACT,
    OPERATOR_ASSIGN_MULTIPLY,
    OPERATOR_ASSIGN_DIVIDE,
    OPERATOR_ASSIGN_MODULO,

    OPERATOR_OR,
    OPERATOR_NOR,
    OPERATOR_XOR,
    OPERATOR_XNOR,
    OPERATOR_AND,
    OPERATOR_NAND,
    OPERATOR_NOT,

    OPERATOR_LESS_THAN,
    OPERATOR_LESS_THAN_EQUAL,
    OPERATOR_GREATER_THAN,
    OPERATOR_GREATER_THAN_EQUAL,
    OPERATOR_EQUAL,
    OPERATOR_NOT_EQUAL,

    OPERATOR_ADD,
    OPERATOR_SUBTRACT,
    OPERATOR_MULTIPLY,
    OPERATOR_DIVIDE,
    OPERATOR_MODULO,

    DELIMITER_LPARENTH,
    DELIMITER_RPARENTH,
    DELIMITER_LBRACKET,
    DELIMITER_RBRACKET,
    DELIMITER_LBRACE,
    DELIMITER_RBRACE,
    DELIMITER_COMMA,
    DELIMITER_COLON,
    DELIMITER_SEMICOLON,

    TYPE_INT,
    TYPE_FLOAT,
    TYPE_CHAR,
    TYPE_STRING,
    TYPE_BOOLEAN,

    LITERAL_INT,
    LITERAL_FLOAT,
    LITERAL_CHAR,
    LITERAL_STRING,
    LITERAL_TRUE,
    LITERAL_FALSE,

    ERROR,
    EOF;

    public static boolean isWhitespace(TokenType type) {
        return
                type == WHITESPACE
                || type == LINEFEED
                || type == CARRIAGE_RETURN;
    }

    public static TokenType mapToType (String s) {
        return switch (s) {

            case " ", "\\t" -> WHITESPACE;
            case "\\r" -> CARRIAGE_RETURN;
            case "\\n" -> LINEFEED;

            case "int" -> TYPE_INT;
            case "float" -> TYPE_FLOAT;
            case "char" -> TYPE_CHAR;
            case "string" -> TYPE_STRING;
            case "boolean" -> TYPE_BOOLEAN;

            case "false" -> LITERAL_FALSE;
            case "true" -> LITERAL_TRUE;

            case "as" -> KEYWORD_AS;
            case "array" -> KEYWORD_ARRAY;
            case "of" -> KEYWORD_OF;
            case "accepts" -> KEYWORD_ACCEPTS;
            case "function" -> KEYWORD_FUNCTION;
            case "returns" -> KEYWORD_RETURNS;

            case "if" -> KEYWORD_IF;
            case "else" -> KEYWORD_ELSE;
            case "else-if" -> KEYWORD_ELSE_IF;
            case "switch" -> KEYWORD_SWITCH;
            case "case" -> KEYWORD_CASE;
            case "default" -> KEYWORD_DEFAULT;

            case "white" -> KEYWORD_WHILE;
            case "until" -> KEYWORD_UNTIL;
            case "do" -> KEYWORD_DO;
            case "for" -> KEYWORD_FOR;
            case "each" -> KEYWORD_EACH;
            case "in" -> KEYWORD_IN;
            case "repeat" -> KEYWORD_REPEAT;

            case "=" -> OPERATOR_ASSIGN;
            case "+=" -> OPERATOR_ASSIGN_ADD;
            case "-=" -> OPERATOR_ASSIGN_SUBTRACT;
            case "*=" -> OPERATOR_ASSIGN_MULTIPLY;
            case "/=" -> OPERATOR_ASSIGN_DIVIDE;
            case "%=" -> OPERATOR_ASSIGN_MODULO;

            case "or" -> OPERATOR_OR;
            case "xor" -> OPERATOR_XOR;
            case "and" -> OPERATOR_AND;
            case "nor" -> OPERATOR_NOR;
            case "xnor" -> OPERATOR_XNOR;
            case "nand" -> OPERATOR_NAND;
            case "not" -> OPERATOR_NOT;

            case "<" -> OPERATOR_GREATER_THAN;
            case "<=" -> OPERATOR_GREATER_THAN_EQUAL;
            case ">" -> OPERATOR_LESS_THAN;
            case ">=" -> OPERATOR_LESS_THAN_EQUAL;
            case "==" -> OPERATOR_EQUAL;
            case "!=" -> OPERATOR_NOT_EQUAL;

            case "+" -> OPERATOR_ADD;
            case "-" -> OPERATOR_SUBTRACT;
            case "*" -> OPERATOR_MULTIPLY;
            case "/" -> OPERATOR_DIVIDE;
            case "%" -> OPERATOR_MODULO;
            case "(" -> DELIMITER_LPARENTH;
            case ")" -> DELIMITER_RPARENTH;
            case "[" -> DELIMITER_LBRACKET;
            case "]" -> DELIMITER_RBRACKET;
            case "{" -> DELIMITER_LBRACE;
            case "}" -> DELIMITER_RBRACE;
            case "," -> DELIMITER_COMMA;
            case ":" -> DELIMITER_COLON;
            case ";" -> DELIMITER_SEMICOLON;

            default -> IDENTIFER;
        };
    }

}
