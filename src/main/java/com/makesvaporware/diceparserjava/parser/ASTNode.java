package com.makesvaporware.diceparserjava.parser;

import com.makesvaporware.diceparserjava.evaluator.EvaluationResult;

public abstract class ASTNode {
    abstract public EvaluationResult evaluate() throws Exception;
}
