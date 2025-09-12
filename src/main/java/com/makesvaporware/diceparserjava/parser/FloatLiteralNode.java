package com.makesvaporware.diceparserjava.parser;

import com.makesvaporware.diceparserjava.evaluator.EvaluationResult;

public class FloatLiteralNode extends ASTNode {
    private float value;

    public FloatLiteralNode(float value) {
        this.value = value;
    }

    @Override
    public EvaluationResult evaluate() throws Exception {
        return new EvaluationResult(value, Float.toString(value));
    }
}
