package com.makesvaporware.diceparserjava.lexer;

import java.util.ArrayList;
import java.util.List;

import com.makesvaporware.diceparserjava.lexer.Token.TokenType;

public class Lexer {
    private String str;
    private int strlen;
    private int pos;

    public Lexer(String str) {
        this.str = str;
        this.strlen = str.length();
        this.pos = 0;
    }

    private void advance() {
        pos += 1;
    }

    private void advance(int n) {
        pos += n;
    }

    private char currentChar() {
        if (pos > strlen)
            return '\0';
        return str.charAt(pos);
    }

    private char peekChar(int n) {
        if (pos + n > strlen)
            return '\0';
        return str.charAt(pos + n);
    }

    public List<Token> lex() throws Exception {
        List<Token> tokens = new ArrayList<>();

        while (pos < strlen) {
            char ch = currentChar();

            if (Character.isWhitespace(ch)) {
                advance();
            } else if (ch == '+') {
                tokens.add(new Token(TokenType.PLUS));
                advance();
            } else if (ch == '-') {
                tokens.add(new Token(TokenType.MINUS));
                advance();
            } else if (ch == '*') {
                tokens.add(new Token(TokenType.MULTIPLY));
                advance();
            } else if (ch == '/') {
                tokens.add(new Token(TokenType.DIVIDE));
                advance();
            } else if (ch == '(') {
                tokens.add(new Token(TokenType.LPAREN));
                advance();
            } else if (ch == ')') {
                tokens.add(new Token(TokenType.RPAREN));
                advance();
            } else if (ch == 'd') {
                tokens.add(new Token(TokenType.DICE));
                advance();
            } else if (ch == 'm' && peekChar(1) == 'i') {
                tokens.add(new Token(TokenType.MINIMUM));
                advance(2);
            } else if (ch == 'm' && peekChar(1) == 'a') {
                tokens.add(new Token(TokenType.MAXIMUM));
                advance(2);
            } else if (ch == 'e') {
                tokens.add(new Token(TokenType.EXPLODE));
                advance();
            } else if (ch == 'k' && peekChar(1) == 'h') {
                tokens.add(new Token(TokenType.KEEP_HIGHEST));
                advance(2);
            } else if (ch == 'k' && peekChar(1) == 'l') {
                tokens.add(new Token(TokenType.KEEP_LOWEST));
                advance(2);
            } else if (Character.isDigit(ch) || (ch == '.' && Character.isDigit(peekChar(1)))) {
                // maybe re-examine this?
                int start = pos;
                boolean hasDot = false;

                if (ch == '.') {
                    hasDot = true;
                    advance();
                }

                while (pos < strlen) {
                    char ch2 = currentChar();
                    if (ch2 == '.') {
                        if (hasDot)
                            break;
                        hasDot = true;
                    } else if (!Character.isDigit(ch2)) {
                        break;
                    }
                    advance();
                }

                float value = Float.parseFloat(str.substring(start, pos));
                tokens.add(new Token(TokenType.NUMBER, value));
            } else {
                // just crash? revisit
                throw new Exception("Unexpected character '" + ch + "' at position " + pos);
            }
        }

        // Add END token to signify end of expression
        tokens.add(new Token(TokenType.END));

        return tokens;
    }

}
