package com.makesvaporware.diceparserjava.parser;

import com.makesvaporware.diceparserjava.lexer.Token;
import com.makesvaporware.diceparserjava.lexer.Token.TokenType;

public class BinaryExprNode extends ASTNode {
    private ASTNode left;
    private ASTNode right;
    private TokenType operator;

    public BinaryExprNode(ASTNode left, ASTNode right, TokenType operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public float evaluate() throws Exception {
        float leftValue = left.evaluate();
        float rightValue = right.evaluate();

        switch (operator) {
            case PLUS:
                return leftValue + rightValue;
            case MINUS:
                return leftValue - rightValue;
            case MULTIPLY:
                return leftValue * rightValue;
            case DIVIDE:
                if (rightValue == 0)
                    throw new Error("Division by zero");
                return leftValue / rightValue;
            default:
                throw new Error("Unknown binary operator: " + Token.typeToString(operator));
        }

    }
}
