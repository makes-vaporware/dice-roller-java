package com.makesvaporware.diceparserjava.parser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
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
    class DieRoll {
        int value = 0;
        boolean kept = true;
        StringBuilder transformations = new StringBuilder();

        public DieRoll(int numSides) {
            this.originalValue(ThreadLocalRandom.current().nextInt(1, numSides + 1));
        }

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

    record ValidatedModifier(TokenType type, int value) {
    }

    class ModifierGroup {
        enum Type {
            PER_DIE,
            KEEP,
            DROP,
        }

        Type type;
        List<ValidatedModifier> modifiers = new ArrayList<>();

        public ModifierGroup(Type type) {
            this.type = type;
        }

        public ModifierGroup addToGroup(ValidatedModifier modifier) {
            modifiers.add(modifier);
            return this;
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

        if (numSides == 0)
            throw new Exception(
                    "Invalid dice expression: Cannot roll a 0-sided die.");

        // Start building DiceExpr display string
        StringBuilder display = new StringBuilder();
        display.append(numDice).append("d").append(numSides);

        // Validate node modifiers before evaluating
        // Group together consecutive `k` and `p` to be evaluated as a union set
        List<ModifierGroup> modifierGroups = new ArrayList<>();

        for (Modifier modifier : modifiers) {
            if (!(modifier.factor instanceof IntegerLiteralNode))
                throw new Exception("Invalid modifier: Modifier values must be a literal integer.");

            EvaluationResult modResult = modifier.factor.evaluate();
            int intModValue = (int) modResult.value;

            ValidatedModifier validatedModifier = new ValidatedModifier(modifier.type, intModValue);

            switch (modifier.type) {
                case MINIMUM:
                case MAXIMUM:
                case EXPLODE:
                    modifierGroups.add(new ModifierGroup(ModifierGroup.Type.PER_DIE).addToGroup(validatedModifier));
                    break;
                case KEEP_HIGHEST:
                case KEEP_LOWEST:
                case KEEP_GREATER_THAN:
                case KEEP_LESS_THAN:
                case KEEP_LITERAL:
                    if (!modifierGroups.isEmpty()
                            && modifierGroups.get(modifierGroups.size() - 1).type == ModifierGroup.Type.KEEP)
                        modifierGroups.get(modifierGroups.size() - 1).addToGroup(validatedModifier);
                    else
                        modifierGroups.add(new ModifierGroup(ModifierGroup.Type.KEEP).addToGroup(validatedModifier));
                    break;
                case DROP_HIGHEST:
                case DROP_LOWEST:
                case DROP_GREATER_THAN:
                case DROP_LESS_THAN:
                case DROP_LITERAL:
                    if (!modifierGroups.isEmpty()
                            && modifierGroups.get(modifierGroups.size() - 1).type == ModifierGroup.Type.DROP)
                        modifierGroups.get(modifierGroups.size() - 1).addToGroup(validatedModifier);
                    else
                        modifierGroups.add(new ModifierGroup(ModifierGroup.Type.DROP).addToGroup(validatedModifier));
                    break;
                default:
                    throw new Exception("Unknown modifier operator: " + Token.typeToString(operator));
            }

            // Continue building DiceExpr display string here
            display.append(Token.typeToString(modifier.type)).append(intModValue);
        }

        // Roll all base dice first
        List<DieRoll> rolls = new ArrayList<>();
        for (int i = 0; i < numDice; i++) {
            DieRoll roll = new DieRoll(numSides);
            rolls.add(roll);
        }

        // Evaluate modifier transformations left-to-right
        for (ModifierGroup group : modifierGroups) {
            switch (group.type) {
                case PER_DIE:
                    ValidatedModifier modifier = group.modifiers.get(0);
                    switch (modifier.type) {
                        case MINIMUM:
                            for (DieRoll roll : rolls) {
                                if (roll.kept && roll.value < modifier.value) {
                                    roll.transformValue(" -> ", modifier.value);
                                    roll.value = modifier.value;
                                }
                            }
                            break;
                        case MAXIMUM:
                            for (DieRoll roll : rolls) {
                                if (roll.kept && roll.value > modifier.value) {
                                    roll.transformValue(" -> ", modifier.value);
                                    roll.value = modifier.value;
                                }
                            }
                            break;
                        case EXPLODE:
                            List<DieRoll> newRolls = new ArrayList<>();
                            for (DieRoll roll : rolls) {
                                newRolls.add(roll);
                                if (roll.kept) {
                                    while (roll.value == modifier.value) {
                                        roll.transform("!");
                                        if (newRolls.size() > MAX_DICE_ROLLS)
                                            throw new Exception("Too many dice rolled.");
                                        roll = new DieRoll(numSides);
                                        newRolls.add(roll);
                                    }
                                }
                            }
                            rolls = newRolls;
                            break;
                        default:
                            break;
                    }
                    break;
                case KEEP:
                    applyKeepUnion(rolls, group.modifiers);
                    break;
                case DROP:
                    applyDropUnion(rolls, group.modifiers);
                    break;
                default:
                    break;
            }
        }

        // Calulate total
        int total = 0;
        for (DieRoll roll : rolls) {
            if (roll.value == 1 || roll.value == numSides)
                roll.bold();

            if (roll.kept)
                total += roll.value;
            else
                roll.strikethrough();
        }

        return new EvaluationResult((float) total,
                String.format("%s (%s)", display.toString(), String.join(", ",
                        rolls.stream().map(roll -> roll.transformations).collect(Collectors.toList()))));
    }

    private void applyKeepUnion(List<DieRoll> rolls, List<ValidatedModifier> modifiers) {
        int keepHighestValue = UNSET_VALUE;
        int keepLowestValue = UNSET_VALUE;
        int keepGreaterThanValue = UNSET_VALUE;
        int keepLessThanValue = UNSET_VALUE;
        Set<Integer> keepLiteralValues = new HashSet<>();

        for (ValidatedModifier modifier : modifiers) {
            switch (modifier.type) {
                case KEEP_HIGHEST:
                    if (keepHighestValue == UNSET_VALUE || modifier.value > keepHighestValue)
                        keepHighestValue = modifier.value;
                    break;
                case KEEP_LOWEST:
                    if (keepLowestValue == UNSET_VALUE || modifier.value > keepLowestValue)
                        keepLowestValue = modifier.value;
                    break;
                case KEEP_GREATER_THAN:
                    if (keepGreaterThanValue == UNSET_VALUE || modifier.value < keepGreaterThanValue)
                        keepGreaterThanValue = modifier.value;
                    break;
                case KEEP_LESS_THAN:
                    if (keepLessThanValue == UNSET_VALUE || modifier.value > keepLessThanValue)
                        keepLessThanValue = modifier.value;
                    break;
                case KEEP_LITERAL:
                    keepLiteralValues.add(modifier.value);
                    break;
                default:
                    break;
            }
        }

        List<DieRoll> sortedRolls = rolls.stream().filter(r -> r.kept).collect(Collectors.toList());
        sortedRolls.sort(Comparator.comparingInt(r -> r.value));

        for (int i = 0; i < sortedRolls.size(); i++) {
            DieRoll roll = sortedRolls.get(i);

            boolean keepAsHighest = keepHighestValue != UNSET_VALUE && i >= sortedRolls.size() - keepHighestValue;
            boolean keepAsLowest = keepLowestValue != UNSET_VALUE && i < keepLowestValue;
            boolean keepAsGreaterThan = keepGreaterThanValue != UNSET_VALUE && roll.value > keepGreaterThanValue;
            boolean keepAsLessThan = keepLessThanValue != UNSET_VALUE && roll.value < keepLessThanValue;
            boolean keepAsLiteral = keepLiteralValues.contains(roll.value);

            if (!keepAsHighest && !keepAsLowest && !keepAsGreaterThan && !keepAsLessThan && !keepAsLiteral)
                roll.discard();
        }
    }

    private void applyDropUnion(List<DieRoll> rolls, List<ValidatedModifier> modifiers) {
        int dropHighestValue = UNSET_VALUE;
        int dropLowestValue = UNSET_VALUE;
        int dropGreaterThanValue = UNSET_VALUE;
        int dropLessThanValue = UNSET_VALUE;
        Set<Integer> dropLiteralValues = new HashSet<>();

        for (ValidatedModifier modifier : modifiers) {
            switch (modifier.type) {
                case DROP_HIGHEST:
                    if (dropHighestValue == UNSET_VALUE || modifier.value > dropHighestValue)
                        dropHighestValue = modifier.value;
                    break;
                case DROP_LOWEST:
                    if (dropLowestValue == UNSET_VALUE || modifier.value > dropLowestValue)
                        dropLowestValue = modifier.value;
                    break;
                case DROP_GREATER_THAN:
                    if (dropGreaterThanValue == UNSET_VALUE || modifier.value < dropGreaterThanValue)
                        dropGreaterThanValue = modifier.value;
                    break;
                case DROP_LESS_THAN:
                    if (dropLessThanValue == UNSET_VALUE || modifier.value > dropLessThanValue)
                        dropLessThanValue = modifier.value;
                    break;
                case DROP_LITERAL:
                    dropLiteralValues.add(modifier.value);
                    break;
                default:
                    break;
            }
        }

        List<DieRoll> sortedRolls = rolls.stream().filter(r -> r.kept).collect(Collectors.toList());
        sortedRolls.sort(Comparator.comparingInt(r -> r.value));

        for (int i = 0; i < sortedRolls.size(); i++) {
            DieRoll roll = sortedRolls.get(i);

            boolean dropAsHighest = dropHighestValue != UNSET_VALUE && i >= sortedRolls.size() - dropHighestValue;
            boolean dropAsLowest = dropLowestValue != UNSET_VALUE && i < dropLowestValue;
            boolean dropAsGreaterThan = dropGreaterThanValue != UNSET_VALUE && roll.value > dropGreaterThanValue;
            boolean dropAsLessThan = dropLessThanValue != UNSET_VALUE && roll.value < dropLessThanValue;
            boolean dropAsLiteral = dropLiteralValues.contains(roll.value);

            if (dropAsHighest || dropAsLowest || dropAsGreaterThan || dropAsLessThan || dropAsLiteral)
                roll.discard();
        }
    }
}
