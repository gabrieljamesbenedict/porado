package com.gabrieljamesbenedict;

import com.gabrieljamesbenedict.LexicalAnalysis.LexicalAnalyzer;
import com.gabrieljamesbenedict.LexicalAnalysis.Token;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class PoradoCompiler
{
    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Hello Porado");
            return;
        }

        String command = args[0];
        switch (command) {
            case "compile": compile(args); break;

            default: {
                System.out.println("Error: Unknown command \"" + command + "\"");
            }
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
                LexicalAnalyzer lexer = new LexicalAnalyzer(reader);

                for (Token token : lexer.tokenize().toList()) {
                    System.out.println(token.toString());
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error: Cannot find file \"" + file.getName() + "\"");
                return;
            } catch (IOException e) {
                System.out.println("Error: Something went wrong while reading the file \"" + file.getName() + "\"");
                return;
            }
        }
    }
}

