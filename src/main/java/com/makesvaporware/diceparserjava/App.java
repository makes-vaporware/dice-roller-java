package com.makesvaporware.diceparserjava;

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

                    - VALUE MODIFIERS:
                        miX         - Minimum. Set rolls below X to X. (e.g. 2d6mi3)
                        maX         - Maximum. Set rolls above X to X. (e.g. 2d6ma5)
                        eX          - Explode. Roll an additional die when you roll X (e.g. 2d6e6)

                    - KEEP MODIFIERS:
                        khX         - Keep highest X dice (e.g. 4d6kh3)
                        klX         - Keep lowest X dice (e.g. 1d20kl1)
                        k>X         - Keep all dice greater than X (e.g. 6d6k>2)
                        k<X         - Keep all dice less than X (e.g. 3d20k<15)
                        kX          - Keep all dice literally matching X (e.g. 10d6k3)

                    - DROP MODIFIERS:
                        phX         - Drop highest X dice (e.g. 2d8ph3)
                        plX         - Drop lowest X dice (e.g. 4d10pl2)
                        p>X         - Drop all dice greater than X (e.g. 4d6p>5)
                        p<X         - Drop all dice less than X (e.g. 5d10p<8)
                        pX          - Drop all dice literally matching X (e.g. 10d6p1)

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
            String input = scanner.nextLine().trim();

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
