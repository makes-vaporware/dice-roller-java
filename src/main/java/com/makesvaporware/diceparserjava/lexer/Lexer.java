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
        if (pos >= strlen)
            return '\0';
        return str.charAt(pos);
    }

    private char peekChar(int n) {
        if (pos + n >= strlen)
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
            } else if (ch == 'k' && peekChar(1) == '>') {
                tokens.add(new Token(TokenType.KEEP_GREATER_THAN));
                advance(2);
            } else if (ch == 'k' && peekChar(1) == '<') {
                tokens.add(new Token(TokenType.KEEP_LESS_THAN));
                advance(2);
            } else if (ch == 'k') {
                tokens.add(new Token(TokenType.KEEP_LITERAL));
                advance();
            } else if (ch == 'p' && peekChar(1) == 'h') {
                tokens.add(new Token(TokenType.DROP_HIGHEST));
                advance(2);
            } else if (ch == 'p' && peekChar(1) == 'l') {
                tokens.add(new Token(TokenType.DROP_LOWEST));
                advance(2);
            } else if (ch == 'p' && peekChar(1) == '>') {
                tokens.add(new Token(TokenType.DROP_GREATER_THAN));
                advance(2);
            } else if (ch == 'p' && peekChar(1) == '<') {
                tokens.add(new Token(TokenType.DROP_LESS_THAN));
                advance(2);
            } else if (ch == 'p') {
                tokens.add(new Token(TokenType.DROP_LITERAL));
                advance();
            } else if (Character.isDigit(ch) || ch == '.') {
                int start = pos;
                boolean hasIntegerPart = false;
                boolean hasDecimalPoint = false;
                boolean hasFractionalPart = false;

                if (Character.isDigit(currentChar())) {
                    hasIntegerPart = true;
                    while (pos < strlen && Character.isDigit(currentChar()))
                        advance();
                }

                if (currentChar() == '.') {
                    hasDecimalPoint = true;
                    advance();
                }

                if (Character.isDigit(currentChar())) {
                    hasFractionalPart = true;
                    while (pos < strlen && Character.isDigit(currentChar()))
                        advance();
                }

                if (currentChar() == '.')
                    throw new Exception("Unexpected character '" + currentChar() + "' at position " + pos);

                if (!hasIntegerPart && !hasFractionalPart)
                    throw new Exception("Unexpected character '" + ch + "' at position " + start);

                String numString = str.substring(start, pos);

                if (hasDecimalPoint)
                    tokens.add(new Token(TokenType.FLOAT_LITERAL, Float.parseFloat(numString)));
                else
                    tokens.add(new Token(TokenType.INTEGER_LITERAL, Float.parseFloat(numString)));
            }
        }

        // Add END token to signify end of expression
        tokens.add(new Token(TokenType.END));

        return tokens;
    }
}
