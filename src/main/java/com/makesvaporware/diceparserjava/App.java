package com.makesvaporware.diceparserjava;

import java.util.Scanner;

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

            // Run the roller
            try {
                Lexer lexer = new Lexer(input);
                Parser parser = new Parser(lexer.lex());
                Evaluator evaluator = new Evaluator(parser.parse());
                float result = evaluator.evaluate();

                String resultString = result == (int) result ? String.valueOf((int) result)
                        : String.valueOf(result);

                System.out.println("Rolled " + input + ": " + resultString);

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
    }
}
