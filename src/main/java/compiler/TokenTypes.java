package compiler;

enum TokenType {
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE,
    LEFT_SQUARE_BRACE, RIGHT_SQUARE_BRACE,
    RIGHT_BRACE, COMMA, DOT,

    MINUS, PLUS, SLASH, STAR, PERCENT,
    EQUAL, EQUAL_EQUAL, SLASH_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL, DOT_DOT,

    IDENTIFIER, SEMICOLON, COLON,
    TYPE_BOOLEAN, TYPE_INTEGER, TYPE_REAL,
    TYPE_RECORD, TYPE_ARRAY, TYPE_STRING,

    AND, ARRAY, BOOLEAN, ELSE, END, FALSE, FOR,
    IF, IN, INTEGER, IS, LOOP, NOT, OR, PRINT,
    REAL, RECORD, RETURN, REVERSE, ROUTINE, THEN, TRUE,
    TYPE, VAR, WHILE, XOR, STRING, WALRUS,

    EOF
}










