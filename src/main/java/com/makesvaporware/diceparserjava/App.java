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
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Type a dice expression (or 'quit' to exit)");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if ("quit".equalsIgnoreCase(input)) {
                System.out.println("Quitting app...");
                break;
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
