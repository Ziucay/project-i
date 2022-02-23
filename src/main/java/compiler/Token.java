package compiler;

public class Token {
    final TokenType type;
    final String lexeme;
    final Object literal;
    final int line;

    Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public String toString() {
        return type + " : " + lexeme + " : " + literal + " : " + this.TokenTypeToInt();
    }


    public int TokenTypeToInt()
    {
        switch (this.type)
        {
            case LEFT_PAREN -> {
                return 257;
            }
            case RIGHT_PAREN -> {
                return 258;
            }
            case LEFT_BRACE -> {
                return 259;
            }
            case LEFT_SQUARE_BRACE -> {
                return 260;
            }
            case RIGHT_SQUARE_BRACE -> {
                return 261;
            }
            case RIGHT_BRACE -> {
                return 262;
            }
            case COMMA -> {
                return 263;
            }
            case DOT -> {
                return 264;
            }
            case MINUS -> {
                return 265;
            }
            case PLUS -> {
                return 266;
            }
            case SLASH -> {
                return 267;
            }
            case STAR -> {
                return 268;
            }
            case PERCENT -> {
                return 269;
            }
            case EQUAL -> {
                return 270;
            }
            case EQUAL_EQUAL -> {
                return 271;
            }
            case SLASH_EQUAL -> {
                return 272;
            }
            case GREATER -> {
                return 273;
            }
            case GREATER_EQUAL -> {
                return 274;
            }
            case LESS -> {
                return 275;
            }
            case LESS_EQUAL -> {
                return 276;
            }
            case DOT_DOT -> {
                return 277;
            }
            case IDENTIFIER -> {
                return 278;
            }
            case SEMICOLON -> {
                return 279;
            }
            case COLON -> {
                return 280;
            }
            case TYPE_BOOLEAN -> {
                return 281;
            }
            case TYPE_INTEGER -> {
                return 282;
            }
            case TYPE_REAL -> {
                return 283;
            }
            case TYPE_RECORD -> {
                return 284;
            }
            case TYPE_ARRAY -> {
                return 285;
            }
            case TYPE_STRING -> {
                return 286;
            }
            case AND -> {
                return 287;
            }
            case ARRAY -> {
                return 288;
            }
            case BOOLEAN -> {
                return 289;
            }
            case ELSE -> {
                return 290;
            }
            case END -> {
                return 291;
            }
            case FALSE -> {
                return 292;
            }
            case FOR -> {
                return 293;
            }
            case IF -> {
                return 294;
            }
            case IN -> {
                return 295;
            }
            case INTEGER -> {
                return 296;
            }
            case IS -> {
                return 297;
            }
            case LOOP -> {
                return 298;
            }
            case NOT -> {
                return 299;
            }
            case OR -> {
                return 300;
            }
            case PRINT -> {
                return 301;
            }
            case REAL -> {
                return 302;
            }
            case RECORD -> {
                return 303;
            }
            case RETURN -> {
                return 304;
            }
            case REVERSE -> {
                return 305;
            }
            case ROUTINE -> {
                return 306;
            }
            case THEN -> {
                return 307;
            }
            case TRUE -> {
                return 308;
            }
            case TYPE -> {
                return 309;
            }
            case VAR -> {
                return 310;
            }
            case WHILE -> {
                return 311;
            }
            case XOR -> {
                return 312;
            }
            case STRING -> {
                return 313;
            }
            case WALRUS -> {
                return 314;
            }
            case EOF -> {
                return 315;
            }
            default -> {
                return 256;
            }

        }
    }

}