package com.makesvaporware.diceparserjava;

import java.util.NoSuchElementException;
import java.util.Scanner;

import com.makesvaporware.diceparserjava.evaluator.EvaluationResult;
import com.makesvaporware.diceparserjava.evaluator.Evaluator;
import com.makesvaporware.diceparserjava.lexer.Lexer;
import com.makesvaporware.diceparserjava.parser.Parser;

/**
 * Hello world!
 */
public class App {
    private static final String HELP_TEXT = """
                    ================================================================
                                            DICE ROLLER HELP
                    ================================================================

                    BASIC SYNTAX:
                      XdY           - Roll X dice with Y sides (e.g. 2d6, 3d20)
                      +, -, *, /    - Standard arithmetic operations
                      ( )           - Parentheses for grouping expressions

                    EXAMPLES:
                      1d20          - Roll a single 20-sided die
                      2d6 + 3       - Roll 2d6 and add 3
                      1d4 + 8d6     - Roll 1d4 and 8d6, then sum together
                      (1d8 + 2) * 2 - Roll 1d8 and add 2, then multiply sum by 2

                    ----------------------------------------------------------------

                    MODIFIERS:
                      mi[X]         - Minimum. Set rolls below X to X. (e.g. 2d6mi3)
                      ma[X]         - Maximum. Set rolls above X to X. (e.g. 2d6ma5)
                      e[X]          - Explode. Roll an additional die when you
                                      match the selector. (e.g. 2d6e6)
                      rr[X]         - Reroll recursively. Rerolls all dice that match the
                                      selector until the selector is no longer fulfilled. (e.g. 1d2rr2)
                      ro[X]         - Reroll once. Rerolls all dice that match the selector
                                      a single time. (e.g.2d2ro1)
                      ra[X]         - Reroll and add. Rerolls up to one die that matches
                                      the selector, then adds it to the total. (e.g. 3d2ra2)
                      k[X]          - Keep. Keeps all dice that match the selector. (e.g. 10d6k3)
                      p[X]          - Drop. Drops all dice that match the selector. (e.g. 10d6p1)

                    Most modifiers can be paired with any one of the selectors below.
                    Minimum and maximum modifiers only work with literal selectors.

                    ----------------------------------------------------------------

                    SELECTORS:
                      hX            - Act on highest X rolls. (e.g. 4d6kh3)
                      lX            - Act on lowest X rolls. (e.g. 1d20kl1)
                      >X            - Act on rolls more than X. (e.g. 6d6k>2)
                      <X            - Act on rolls less than X. (e.g. 3d20k<15)
                      X             - Act on rolls literally matching X. (e.g. 10d6k3)

                    ----------------------------------------------------------------

                    Type 'help' to bring up this guide.
                    Type 'quit' to quit the program.

                    ================================================================
            """;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Type a dice expression to evaluate (or 'help' for help, or 'quit' to exit)");

        while (true) {
            System.out.print("> ");
            String input;

            try {
                input = scanner.nextLine().trim();
            } catch (NoSuchElementException e) {
                // Ctrl+C suppression
                System.out.println("\nQuitting app...");
                break;
            }

            if ("quit".equalsIgnoreCase(input)) {
                System.out.println("Quitting app...");
                break;
            }

            if ("help".equalsIgnoreCase(input)) {
                System.out.println(HELP_TEXT);
                continue;
            }

            try {
                Lexer lexer = new Lexer(input);
                Parser parser = new Parser(lexer.lex());
                Evaluator evaluator = new Evaluator(parser.parse());
                EvaluationResult result = evaluator.evaluate();

                String displayString = result.displayString;
                String totalString = result.value == (int) result.value ? String.valueOf((int) result.value)
                        : String.valueOf(result.value);

                System.out.println(String.format("Rolled: %s", displayString));
                System.out.println(String.format("Total: %s", totalString));

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
    }
}
