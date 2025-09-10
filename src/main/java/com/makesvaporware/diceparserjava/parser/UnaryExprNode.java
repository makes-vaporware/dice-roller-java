package com.makesvaporware.diceparserjava.parser;

import com.makesvaporware.diceparserjava.lexer.Token;
import com.makesvaporware.diceparserjava.lexer.Token.TokenType;

public class UnaryExprNode extends ASTNode {
    private ASTNode child;
    private TokenType operator;

    public UnaryExprNode(ASTNode child, TokenType operator) {
        this.child = child;
        this.operator = operator;
    }

    @Override
    public float evaluate() throws Exception {
        float childValue = child.evaluate();

        switch (operator) {
            case PLUS:
                return childValue;
            case MINUS:
                return -1 * childValue;
            default:
                throw new Error("Unknown unary operator: " + Token.typeToString(operator));
        }
    }
}
