package com.gabrieljamesbenedict;

import com.gabrieljamesbenedict.Exceptions.CompileException;
import com.gabrieljamesbenedict.Interpreter.CodeRunner;
import com.gabrieljamesbenedict.LexicalAnalysis.LexicalAnalyzer;
import com.gabrieljamesbenedict.LexicalAnalysis.Token;
import com.gabrieljamesbenedict.SyntaxAnalysis.AbstractSyntaxTree;
import com.gabrieljamesbenedict.SyntaxAnalysis.SyntaxAnalyzer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Porado
{
    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Hello Porado");
            return;
        }

        String command = args[0];
        switch (command) {
            case "run" -> compile(args);
            default -> System.out.println("Error: Unknown command \"" + command + "\"");
        }
    }

    private static void compile(String[] args) {
        ArrayList<File> files = new ArrayList<>();
        PushbackReader reader;

        for (int i = 0; i < args.length; i++) {
            if (i < 1) continue;

            files.add(new File(args[i]));
        }

        for (File file : files) {
            try {
                reader = new PushbackReader (new FileReader(file));

                // FIVE STEPS OF COMPILATION
                Stream<Token> tokenStream = LexicalAnalyzer.tokenize(reader);
                List<Token> copy = tokenStream.toList();
                AbstractSyntaxTree ast = SyntaxAnalyzer.parse(copy.stream());
                ast.print();
                CodeRunner runner = new CodeRunner(ast);
                runner.run();

            } catch (FileNotFoundException e) {
                System.out.println("Error: Cannot find file \"" + file.getName() + "\"");
                return;
            } catch (IOException e) {
                System.out.println("Error: Something went wrong while reading the file \"" + file.getName() + "\"");
                return;
            } catch (CompileException e) {
                System.out.println(e.getMessage());
                return;
            }
        }
    }
}

