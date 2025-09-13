package com.makesvaporware.diceparserjava.parser;

import com.makesvaporware.diceparserjava.evaluator.EvaluationResult;

public class ParenthesizedExprNode extends ASTNode {
    private ASTNode inner;

    public ParenthesizedExprNode(ASTNode inner) {
        this.inner = inner;
    }

    @Override
    public EvaluationResult evaluate() throws Exception {
        EvaluationResult innerResult = inner.evaluate();

        return new EvaluationResult(innerResult.value, String.format("(%s)", innerResult.displayString));
    }

}
