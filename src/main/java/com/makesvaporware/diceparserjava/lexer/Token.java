package com.makesvaporware.diceparserjava.lexer;

public class Token {
    // @formatter:off
    public enum TokenType {
        // Operators
        PLUS,                   // +
        MINUS,                  // -
        MULTIPLY,               // *
        DIVIDE,                 // /

        // Parentheses
        LPAREN,                 // (
        RPAREN,                 // )

        // Dice Notation
        DICE,                   // d

        // Modifiers
        MODIFIER_MINIMUM,       // mi
        MODIFIER_MAXIMUM,       // ma
        MODIFIER_EXPLODE,       // e
        MODIFIER_KEEP,          // k
        MODIFIER_DROP,          // p

        // Selectors
        SELECTOR_HIGHEST,       // h
        SELECTOR_LOWEST,        // l
        SELECTOR_GREATER_THAN,  // >
        SELECTOR_LESS_THAN,     // <
        SELECTOR_LITERAL,       // ("", literal)

        // Literals
        INTEGER_LITERAL,        // e.g. 1
        FLOAT_LITERAL,          // e.g. 2.3

        // End of Input
        END,                    // end of token stream
    }
    // @formatter:on

    public TokenType type;
    public float numericValue;

    public Token(TokenType type) {
        this.type = type;
        numericValue = -1;
    }

    public Token(TokenType type, float numericValue) {
        this.type = type;
        this.numericValue = numericValue;
    }

    public static String typeToString(TokenType type) {
        switch (type) {
            // Arithmetic perators
            case PLUS:
                return "+";
            case MINUS:
                return "-";
            case MULTIPLY:
                return "*";
            case DIVIDE:
                return "/";

            // Parentheses
            case LPAREN:
                return "(";
            case RPAREN:
                return ")";

            // Dice Notation
            case DICE:
                return "d";

            // Modifiers
            case MODIFIER_MINIMUM:
                return "mi";
            case MODIFIER_MAXIMUM:
                return "ma";
            case MODIFIER_EXPLODE:
                return "e";
            case MODIFIER_KEEP:
                return "k";
            case MODIFIER_DROP:
                return "p";

            // Selectors
            case SELECTOR_HIGHEST:
                return "h";
            case SELECTOR_LOWEST:
                return "l";
            case SELECTOR_GREATER_THAN:
                return "<";
            case SELECTOR_LESS_THAN:
                return ">";
            case SELECTOR_LITERAL:
                return "";

            // Literals
            case INTEGER_LITERAL:
                return "INTEGER";
            case FLOAT_LITERAL:
                return "FLOAT";

            // End of Input
            case END:
            default:
                return "\0";
        }
    }

    public String toString() {
        switch (type) {
            case INTEGER_LITERAL:
                return Integer.toString((int) numericValue);
            case FLOAT_LITERAL:
                return Float.toString(numericValue);
            default:
                return typeToString(type);
        }
    }
}
