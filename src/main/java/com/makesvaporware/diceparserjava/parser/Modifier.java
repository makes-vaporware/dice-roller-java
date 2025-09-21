package com.makesvaporware.diceparserjava.parser;

import com.makesvaporware.diceparserjava.lexer.Token.TokenType;

public class Modifier {
    public TokenType type;
    public TokenType selector;
    public ASTNode factor;

    public Modifier(TokenType type, TokenType selector, ASTNode factor) {
        this.type = type;
        this.selector = selector;
        this.factor = factor;
    }
}
