package com.makesvaporware.diceparserjava.parser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.makesvaporware.diceparserjava.evaluator.EvaluationResult;
import com.makesvaporware.diceparserjava.lexer.Token;
import com.makesvaporware.diceparserjava.lexer.Token.TokenType;

public class DiceExprNode extends ASTNode {
    private static final int MAX_DICE_ROLLS = 99999;
    private static final int UNSET_VALUE = -1;

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
    record ValidatedModifier(TokenType type, int value) {
    }

    class DieRoll {
        int value = 0;
        boolean kept = true;
        StringBuilder transformations = new StringBuilder();

        public DieRoll originalValue(int value) {
            this.value = value;
            this.transformations.append(value);
            return this;
        }

        public DieRoll transformValue(String transformation, int value) {
            this.value = value;
            transformations.append(transformation + value);
            return this;
        }

        public DieRoll transform(String transformation) {
            transformations.append(transformation);
            return this;
        }

        public void bold() {
            transformations.insert(0, "**");
            transformations.append("**");
        }

        public void strikethrough() {
            transformations.insert(0, "~~");
            transformations.append("~~");
        }

        public void discard() {
            kept = false;
        }

        public void keep() {
            kept = true;
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
        int keepHighestValue = UNSET_VALUE;
        int keepLowestValue = UNSET_VALUE;

        for (Modifier modifier : modifiers) {
            if (!(modifier.factor instanceof IntegerLiteralNode))
                throw new Exception("Invalid modifier: Modifier values must be a literal integer.");

            EvaluationResult modResult = modifier.factor.evaluate();

            int intModValue = (int) modResult.value;

            if (intModValue < 0)
                throw new Exception("Modifiers must be a non-negative integer.");

            // kh/kl checking
            switch (modifier.type) {
                case KEEP_HIGHEST:
                    if (keepHighestValue > UNSET_VALUE)
                        throw new Exception("Cannot have multiple kh modifiers.");
                    keepHighestValue = intModValue;
                    break;
                case KEEP_LOWEST:
                    if (keepLowestValue > UNSET_VALUE)
                        throw new Exception("Cannot have multiple kl modifiers.");
                    keepLowestValue = intModValue;
                    break;
                default:
                    break;
            }

            validatedModifiers.add(new ValidatedModifier(modifier.type, intModValue));
        }

        // Roll dice
        int total = 0;
        int diceToRoll = (int) numDice;
        int diceRolled = 0;
        List<DieRoll> rolls2 = new ArrayList<>();

        while (diceToRoll > 0) {
            diceToRoll--;
            diceRolled++;

            // Catch infinite explosion loops
            if (diceRolled > MAX_DICE_ROLLS)
                throw new Exception("Too many dice rolled.");

            DieRoll roll2 = new DieRoll();

            // First roll the base die
            int rollValue = (int) Math.floor(Math.random() * numSides) + 1;
            roll2.originalValue(rollValue);

            // Apply per-die modifiers in sequence
            for (ValidatedModifier modifier : validatedModifiers) {
                switch (modifier.type) {
                    case MINIMUM:
                        if (rollValue < modifier.value)
                            // roll2.append(" -> ").append(modifier.value);
                            roll2.transformValue(" -> ", modifier.value);
                        rollValue = Math.max(rollValue, modifier.value);
                        break;
                    case MAXIMUM:
                        if (rollValue > modifier.value)
                            roll2.transformValue(" -> ", modifier.value);
                        rollValue = Math.min(rollValue, modifier.value);
                        break;
                    case EXPLODE:
                        if (rollValue == modifier.value) {
                            diceToRoll++;
                            roll2.transform("!");
                        }
                        break;
                    case KEEP_HIGHEST:
                    case KEEP_LOWEST:
                        // Pass here. Section modifiers are handled at the end of rolling
                        break;
                    default:
                        throw new Exception("Unknown modifier operator: " + Token.typeToString(operator));
                }
            }

            roll2.value = rollValue;

            if (roll2.value == 1 || roll2.value == numSides)
                roll2.bold();

            rolls2.add(roll2);
        }

        // Apply selection modifiers at this level
        if (keepHighestValue > UNSET_VALUE || keepLowestValue > UNSET_VALUE) {
            List<DieRoll> sortedRolls = new ArrayList<>(rolls2);
            sortedRolls.sort(Comparator.comparingInt(r -> r.value));

            for (int i = 0; i < sortedRolls.size(); i++) {
                boolean keepAsLowest = keepLowestValue != UNSET_VALUE && i < keepLowestValue;
                boolean keepAsHighest = keepHighestValue != UNSET_VALUE && i >= sortedRolls.size() - keepHighestValue;

                if (!keepAsLowest && !keepAsHighest)
                    sortedRolls.get(i).discard();
            }
        }

        // Calulate total
        for (DieRoll roll : rolls2) {
            if (roll.kept)
                total += roll.value;
            else
                roll.strikethrough();
        }

        // Build display string
        StringBuilder display = new StringBuilder();
        display.append(numDice).append("d").append(numSides);

        for (ValidatedModifier modifier : validatedModifiers) {
            display.append(Token.typeToString(modifier.type)).append(modifier.value);
        }

        return new EvaluationResult((float) total,
                String.format("%s (%s)", display.toString(), String.join(", ",
                        rolls2.stream().map(roll -> roll.transformations).collect(Collectors.toList()))));
    }
}
