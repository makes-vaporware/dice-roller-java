package com.makesvaporware.diceparserjava.evaluator;

import com.makesvaporware.diceparserjava.parser.ASTNode;

public class Evaluator {
    private ASTNode root;

    public Evaluator(ASTNode root) {
        this.root = root;
    }

    public float evaluate() throws Exception {
        return root.evaluate();
    }
}
