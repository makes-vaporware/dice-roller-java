package com.makesvaporware.diceparserjava.parser;

import java.util.ArrayList;
import java.util.List;

import com.makesvaporware.diceparserjava.lexer.Token;
import com.makesvaporware.diceparserjava.lexer.Token.TokenType;

public class Parser {
    List<Token> tokens = new ArrayList<>();
    int pos = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
    }

    // === HELPERS ===

    private boolean match(TokenType... tokenTypes) {
        for (TokenType type : tokenTypes) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd())
            return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd())
            pos++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.END;
    }

    private Token peek() {
        return tokens.get(pos);
    }

    private Token previous() {
        return tokens.get(pos - 1);
    }

    // === TOP LEVEL ===

    public ASTNode parse() throws Exception {
        ASTNode ast = expression();

        if (!isAtEnd()) {
            throw new Exception("Unexpected token: " + peek().toString());
        }

        return ast;
    }

    // === HELPERS ===

    // @formatter:off
    /* 
        --- GRAMMAR ---
        expression  := term
                    | expression "+" term
                    | expression "-" term
        term        := dice
                    | term "*" dice
                    | term "/" dice
        dice        := factor
                    | factor "d" factor modifiers*
        modifiers   := "mi" factor
                    | "ma" factor
                    | "e" factor 
                    | "kh" factor
                    | "kl" factor
                    | "k" factor
        factor      := number
                    | "+" factor
                    | "-" factor
                    | "(" expression ")"
        number      := IntegerLiteral
                    | FloatLiteral
    */
    // @formatter:on

    private ASTNode expression() throws Exception {
        ASTNode node = term();

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            ASTNode right = term();
            node = new BinaryExprNode(node, right, operator.type);
        }
        return node;
    }

    private ASTNode term() throws Exception {
        ASTNode node = dice();

        while (match(TokenType.MULTIPLY, TokenType.DIVIDE)) {
            Token operator = previous();
            ASTNode right = dice();
            node = new BinaryExprNode(node, right, operator.type);
        }

        return node;
    }

    private ASTNode dice() throws Exception {
        ASTNode node = factor();

        if (match(TokenType.DICE)) {
            Token operator = previous();
            ASTNode right = factor();
            node = new DiceExprNode(node, right, operator.type);

            while (match(TokenType.MINIMUM, TokenType.MAXIMUM, TokenType.EXPLODE, TokenType.KEEP_HIGHEST,
                    TokenType.KEEP_LOWEST, TokenType.KEEP_LITERAL)) {
                Token mod = previous();
                ASTNode modFactor = factor();
                ((DiceExprNode) node).addModifier(new Modifier(mod.type, modFactor));
            }
        }

        return node;
    }

    private ASTNode factor() throws Exception {
        if (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            ASTNode right = factor();
            return new UnaryExprNode(right, operator.type);
        }

        if (match(TokenType.LPAREN)) {
            ASTNode expr = expression();
            if (!match(TokenType.RPAREN))
                throw new Exception("Expected ')");
            return expr;
        }

        return number();
    }

    private ASTNode number() throws Exception {
        if (!match(TokenType.NUMBER))
            throw new Exception("Expected a number");

        float value = previous().numericValue;
        int intValue = (int) value;

        if (value == intValue)
            return new IntegerLiteralNode(intValue);

        return new FloatLiteralNode(value);
    }
}
