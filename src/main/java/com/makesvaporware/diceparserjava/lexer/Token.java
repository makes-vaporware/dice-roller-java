package com.makesvaporware.diceparserjava.lexer;

public class Token {
    // @formatter:off
    public enum TokenType {
        // Operators
        PLUS,               // +
        MINUS,              // -
        MULTIPLY,           // *
        DIVIDE,             // /

        // Parentheses
        LPAREN,             // (
        RPAREN,             // )

        // Dice Notation
        DICE,               // d

        // Modifiers
        MINIMUM,            // mi
        MAXIMUM,            // ma
        EXPLODE,            // e
        KEEP_HIGHEST,       // kh
        KEEP_LOWEST,        // kl
        KEEP_GREATER_THAN,  // k>
        KEEP_LESS_THAN,     // k<
        KEEP_LITERAL,       // k
        DROP_HIGHEST,       // ph
        DROP_LOWEST,        // pl
        DROP_GREATER_THAN,  // p>
        DROP_LESS_THAN,     // p<
        DROP_LITERAL,       // p

        // Literals
        INTEGER_LITERAL,    // e.g. 1
        FLOAT_LITERAL,      // e.g. 2.3

        // End of Input
        END,                // end of token stream
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
            case MINIMUM:
                return "mi";
            case MAXIMUM:
                return "ma";
            case EXPLODE:
                return "e";
            case KEEP_HIGHEST:
                return "kh";
            case KEEP_LOWEST:
                return "kl";
            case KEEP_GREATER_THAN:
                return "k>";
            case KEEP_LESS_THAN:
                return "k<";
            case KEEP_LITERAL:
                return "k";
            case DROP_HIGHEST:
                return "ph";
            case DROP_LOWEST:
                return "pl";
            case DROP_GREATER_THAN:
                return "p>";
            case DROP_LESS_THAN:
                return "p<";
            case DROP_LITERAL:
                return "p";

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
