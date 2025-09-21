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
                tokens.add(new Token(TokenType.MODIFIER_MINIMUM));
                advance(2);
                lexSelector(tokens);
            } else if (ch == 'm' && peekChar(1) == 'a') {
                tokens.add(new Token(TokenType.MODIFIER_MAXIMUM));
                advance(2);
                lexSelector(tokens);
            } else if (ch == 'e') {
                tokens.add(new Token(TokenType.MODIFIER_EXPLODE));
                advance();
                lexSelector(tokens);
            } else if (ch == 'r' && peekChar(1) == 'r') {
                tokens.add(new Token(TokenType.MODIFIER_REROLL));
                advance(2);
                lexSelector(tokens);
            } else if (ch == 'r' && peekChar(1) == 'o') {
                tokens.add(new Token(TokenType.MODIFIER_REROLL_ONCE));
                advance(2);
                lexSelector(tokens);
            } else if (ch == 'k') {
                tokens.add(new Token(TokenType.MODIFIER_KEEP));
                advance();
                lexSelector(tokens);
            } else if (ch == 'p') {
                tokens.add(new Token(TokenType.MODIFIER_DROP));
                advance();
                lexSelector(tokens);
            } else if (Character.isDigit(ch) || ch == '.') {
                lexNumber(tokens);
            } else {
                throw new Exception("Unexpected character '" + ch + "' at position " + pos);
            }
        }

        // Add END token to signify end of expression
        tokens.add(new Token(TokenType.END));

        return tokens;
    }

    private void lexSelector(List<Token> tokens) {
        char ch = currentChar();

        if (ch == 'h') {
            tokens.add(new Token(TokenType.SELECTOR_HIGHEST));
            advance();
        } else if (ch == 'l') {
            tokens.add(new Token(TokenType.SELECTOR_LOWEST));
            advance();
        } else if (ch == '>') {
            tokens.add(new Token(TokenType.SELECTOR_GREATER_THAN));
            advance();
        } else if (ch == '<') {
            tokens.add(new Token(TokenType.SELECTOR_LESS_THAN));
            advance();
        } else {
            tokens.add(new Token(TokenType.SELECTOR_LITERAL));
        }
    }

    private void lexNumber(List<Token> tokens) throws Exception {
        char ch = currentChar();
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
