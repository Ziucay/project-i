package compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static compiler.TokenType.*;
public class Lexer {
    private static final Map <String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("not", NOT);
        keywords.put("or", OR);
        keywords.put("xor", XOR);
        keywords.put("boolean", BOOLEAN);
        keywords.put("integer", INTEGER);
        keywords.put("real", REAL);
        keywords.put("string", STRING);
        keywords.put("var", VAR);
        keywords.put("true", TRUE);
        keywords.put("false", FALSE);
        keywords.put("array", ARRAY);
        keywords.put("record", RECORD);
        keywords.put("if", IF);
        keywords.put("then", THEN);
        keywords.put("else", ELSE);
        keywords.put("for", FOR);
        keywords.put("while", WHILE);
        keywords.put("loop", LOOP);
        keywords.put("return", RETURN);
        keywords.put("end", END);
        keywords.put("in", IN);
        keywords.put("is", IS);
        keywords.put("print", PRINT);
        keywords.put("reverse", REVERSE);
        keywords.put("routine", ROUTINE);
        keywords.put("type", TYPE);
    }

    private final String source;
    private final List <Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char symbol = next();
        switch (symbol) {
            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case '{':
                addToken(LEFT_BRACE);
                break;
            case '}':
                addToken(RIGHT_BRACE);
                break;
            case '[':
                addToken(LEFT_SQUARE_BRACE);
                break;
            case ']':
                addToken(RIGHT_SQUARE_BRACE);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '+':
                addToken(PLUS);
                break;
            case '*':
                addToken(STAR);
                break;
            case '%':
                addToken(PERCENT);
                break;
            case ';':
                addToken(SEMICOLON);
                break;
            case ':':
                addToken(match('=') ? WALRUS : COLON);
                break;
            case '.':
                addToken(match('.') ? DOT_DOT : DOT);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                addToken(match('=') ? SLASH_EQUAL : SLASH);
                break;

            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;

            default:
                if (isDigit(symbol)) {
                    number();
                } else if (isAlpha(symbol)) {
                    identifier();
                } else if (isString(symbol)) {
                    string();
                }
                break;

        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek()))
            next();

        String text = source.substring(start, current);
        TokenType type = keywords.getOrDefault(text, IDENTIFIER);
        addToken(type);
    }

    private void number() {
        while (isDigit(peek()))
            next();

        if (peek() == '.' && isDigit(peekNext())) {
            next();

            while (isDigit(peek()))
                next();
            addToken(TYPE_REAL, Double.parseDouble(source.substring(start, current)));
            return;
        }

        addToken(TYPE_INTEGER, Integer.parseInt(source.substring(start, current)));
    }

    private void string() {
        while (notQuote(peek())) {
            next();
        }

        String text = source.substring(start, current);
        addToken(TYPE_STRING, text);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char next() {
        current++;
        return source.charAt(current - 1);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private boolean match(char expected) {
        if (isAtEnd())
            return false;
        if (source.charAt(current) != expected)
            return false;

        current++;
        return true;
    }

    private char peek() {
        if (isAtEnd())
            return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length())
            return '\0';
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char symbol) {
        return (symbol >= 'a' && symbol <= 'z') ||
                (symbol >= 'A' && symbol <= 'Z') ||
                (symbol == '_');
    }

    private boolean isDigit(char symbol) {
        return symbol >= '0' && symbol <= '9';
    }

    private boolean isString(char symbol) {
        return symbol == '\"';
    }

    private boolean notQuote(char symbol) {
        return symbol != '\"';
    }

    private boolean isAlphaNumeric(char symbol) {
        return isAlpha(symbol) || isDigit(symbol);
    }

    public String toString() {
        String string = "";
        for (Token token : this.tokens) {
            string = token.toString() + '\n';
        }
        return string;
    }
}