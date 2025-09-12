package com.makesvaporware.diceparserjava.parser;

import java.util.ArrayList;
import java.util.List;

import com.makesvaporware.diceparserjava.evaluator.EvaluationResult;
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

    // Helpers
    class ValidatedModifier {
        TokenType type;
        int value;

        public ValidatedModifier(TokenType type, int value) {
            this.type = type;
            this.value = value;
        }
    }

    @Override
    public EvaluationResult evaluate() throws Exception {
        // Validate dice parameters
        if (!(left instanceof IntegerLiteralNode))
            throw new Exception("Invalid dice expression: Number of dice must be a literal integer.");

        if (!(right instanceof IntegerLiteralNode))
            throw new Exception("Invalid dice expression: Number of sides must be a literal integer.");

        EvaluationResult leftResult = left.evaluate();
        EvaluationResult rightResult = right.evaluate();

        int numDice = (int) leftResult.value;
        int numSides = (int) rightResult.value;

        if (numDice < 0)
            throw new Exception(
                    "Invalid dice expression: Number of dice must be a positive integer.");

        if (numSides <= 0)
            throw new Exception(
                    "Invalid dice expression: Number of sides must be a positive integer.");

        // Validate node modifiers before evaluating
        List<ValidatedModifier> validatedModifiers = new ArrayList<>();

        for (Modifier modifier : modifiers) {
            if (!(modifier.factor instanceof IntegerLiteralNode))
                throw new Exception("Invalid modifier: Modifier values must be a literal integer.");

            EvaluationResult modResult = modifier.factor.evaluate();

            int intModValue = (int) modResult.value;

            if (intModValue < 0)
                throw new Exception("Modifiers must be a non-negative integer.");

            validatedModifiers.add(new ValidatedModifier(modifier.type, intModValue));
        }

        // Roll dice
        int total = 0;
        int diceToRoll = (int) numDice;
        int diceRolled = 0;
        int MAX_DICE_ROLLS = 99999;
        List<String> rolls = new ArrayList<>();

        while (diceToRoll > 0) {
            diceToRoll--;
            diceRolled++;

            // Catch infinite explosion loops
            if (diceRolled > MAX_DICE_ROLLS)
                throw new Exception("Too many dice rolled.");

            StringBuilder roll = new StringBuilder();

            // First roll the base die
            int rollValue = (int) Math.floor(Math.random() * numSides) + 1;
            roll.append(rollValue);

            // Then add all modifier transformations in sequence
            for (ValidatedModifier modifier : validatedModifiers) {
                switch (modifier.type) {
                    case MINIMUM:
                        if (rollValue < modifier.value)
                            roll.append(" -> ").append(modifier.value);
                        rollValue = Math.max(rollValue, modifier.value);
                        break;
                    case MAXIMUM:
                        if (rollValue > modifier.value)
                            roll.append(" -> ").append(modifier.value);
                        rollValue = Math.min(rollValue, modifier.value);
                        break;
                    case EXPLODE:
                        if (rollValue == modifier.value) {
                            diceToRoll++;
                            roll.append("!");
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

            String rollStr = roll.toString();
            if (rollValue == 1 || rollValue == numSides) {
                rollStr = "**" + rollStr + "**";
            }
            rolls.add(rollStr);
        }

        // Build display string
        StringBuilder display = new StringBuilder();
        display.append(numDice).append("d").append(numSides);

        for (ValidatedModifier modifier : validatedModifiers) {
            display.append(Token.typeToString(modifier.type)).append(modifier.value);
        }

        return new EvaluationResult((float) total,
                String.format("%s (%s)", display.toString(), String.join(", ", rolls)));
    }
}
