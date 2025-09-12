package com.makesvaporware.diceparserjava.parser;

import com.makesvaporware.diceparserjava.evaluator.EvaluationResult;
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
    public EvaluationResult evaluate() throws Exception {
        EvaluationResult childResult = child.evaluate();

        switch (operator) {
            case PLUS:
                return new EvaluationResult(childResult.value,
                        String.format("+%s", childResult.displayString));

            case MINUS:
                return new EvaluationResult(-1 * childResult.value,
                        String.format("-%s", childResult.displayString));
            default:
                throw new Error("Unknown unary operator: " + Token.typeToString(operator));
        }
    }
}
