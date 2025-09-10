package com.makesvaporware.diceparserjava.parser;

import com.makesvaporware.diceparserjava.lexer.Token.TokenType;

public class Modifier {
    private TokenType type;
    private ASTNode factor;

    public Modifier(TokenType type, ASTNode factor) {
        this.type = type;
        this.factor = factor;
    }

    public TokenType getType() {
        return type;
    }

    public ASTNode getFactor() {
        return factor;
    }
}
