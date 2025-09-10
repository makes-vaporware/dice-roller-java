package com.makesvaporware.diceparserjava.parser;

public class NumberLiteralNode extends ASTNode {
    private float value;

    public NumberLiteralNode(float value) {
        this.value = value;
    }

    @Override
    public float evaluate() {
        return value;
    }
}
