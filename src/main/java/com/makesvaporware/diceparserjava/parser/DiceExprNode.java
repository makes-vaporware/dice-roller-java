package com.makesvaporware.diceparserjava.parser;

import java.util.ArrayList;
import java.util.List;

import com.makesvaporware.diceparserjava.lexer.Token;
import com.makesvaporware.diceparserjava.lexer.Token.TokenType;

public class DiceExprNode extends ASTNode {
    private ASTNode left;
    private ASTNode right;
    private TokenType operator;
    private List<Modifier> modifiers = new ArrayList<>();

    public DiceExprNode(ASTNode left, ASTNode right, TokenType operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public void addModifier(Modifier modifier) {
        this.modifiers.add(modifier);
    }

    // Helper Class
    class ValidatedModifier {
        TokenType type;
        int value;

        public ValidatedModifier(TokenType type, int value) {
            this.type = type;
            this.value = value;
        }
    }

    @Override
    public float evaluate() throws Exception {
        float leftValue = left.evaluate();
        float rightValue = right.evaluate();

        // Validate dice parameters
        int numDice = (int) leftValue;
        int numSides = (int) rightValue;

        if (leftValue != numDice || rightValue != numSides || numDice < 0 || numSides <= 0) {
            throw new Error(
                    "Invalid dice expression: Number of dice (" + leftValue + ") and sides (" + rightValue
                            + ") must be positive integers.");
        }

        // Validate node modifiers before evaluating
        List<ValidatedModifier> validatedModifiers = new ArrayList<>();

        for (Modifier modifier : modifiers) {
            float modValue = modifier.getFactor().evaluate();
            int intModValue = (int) modValue;

            if (modValue != intModValue || intModValue < 0)
                throw new Error("Invalid modifier value" + modValue + ". Modifiers must be a non-negative integer.");
            validatedModifiers.add(new ValidatedModifier(modifier.getType(), intModValue));
        }

        // Roll dice
        int total = 0;
        int diceToRoll = (int) numDice;
        int diceRolled = 0;
        int MAX_DICE_ROLLS = 99999;

        while (diceToRoll > 0) {
            diceToRoll--;
            diceRolled++;

            // Catch infinite explosion loops
            if (diceRolled > MAX_DICE_ROLLS) {
                throw new Error("Too many dice rolled.");
            }

            // First roll the base die
            double rollValue = Math.floor(Math.random() * numSides) + 1;

            // Then add all modifier transformations in sequence
            for (ValidatedModifier modifier : validatedModifiers) {
                switch (modifier.type) {
                    case MINIMUM:
                        rollValue = Math.max(rollValue, modifier.value);
                        break;
                    case MAXIMUM:
                        rollValue = Math.min(rollValue, modifier.value);
                        break;
                    case EXPLODE:
                        if (rollValue == modifier.value) {
                            diceToRoll++;
                            continue;
                        }
                        break;
                    case KEEP_HIGHEST:
                    case KEEP_LOWEST:
                        // todo
                        throw new Exception("kh/kl not implemented yet");
                    default:
                        throw new Exception("Unknown modifier operator: " + Token.typeToString(operator));
                }
            }

            total += rollValue;

        }
        return (float) total;
    }
}
