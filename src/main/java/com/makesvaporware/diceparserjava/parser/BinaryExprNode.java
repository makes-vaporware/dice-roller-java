package com.makesvaporware.diceparserjava.parser;

import com.makesvaporware.diceparserjava.evaluator.EvaluationResult;
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
    public EvaluationResult evaluate() throws Exception {
        EvaluationResult leftResult = left.evaluate();
        EvaluationResult rightResult = right.evaluate();

        switch (operator) {
            case PLUS:
                return new EvaluationResult(leftResult.value + rightResult.value,
                        String.format("%s + %s",
                                leftResult.displayString, rightResult.displayString));
            case MINUS:
                return new EvaluationResult(leftResult.value - rightResult.value,
                        String.format("%s - %s",
                                leftResult.displayString, rightResult.displayString));
            case MULTIPLY:
                return new EvaluationResult(leftResult.value
                        * rightResult.value,
                        String.format("%s * %s",
                                leftResult.displayString, rightResult.displayString));
            case DIVIDE:
                if (rightResult.value == 0)
                    throw new Exception("Division by zero");
                return new EvaluationResult(leftResult.value / rightResult.value,
                        String.format("%s / %s",
                                leftResult.displayString, rightResult.displayString));
            default:
                throw new Exception("Unknown binary operator: " + Token.typeToString(operator));
        }
    }
}
