package com.makesvaporware.diceparserjava.parser;

import com.makesvaporware.diceparserjava.lexer.Token.TokenType;

public class Modifier {
    public TokenType type;
    public ASTNode factor;

    public Modifier(TokenType type, ASTNode factor) {
        this.type = type;
        this.factor = factor;
    }
}
