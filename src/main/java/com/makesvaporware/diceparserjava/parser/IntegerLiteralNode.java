package com.makesvaporware.diceparserjava.parser;

import com.makesvaporware.diceparserjava.evaluator.EvaluationResult;

public class IntegerLiteralNode extends ASTNode {
    private int value;

    public IntegerLiteralNode(int value) {
        this.value = value;
    }

    @Override
    public EvaluationResult evaluate() {
        return new EvaluationResult(value, Integer.toString(value));
    }
}
