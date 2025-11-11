package com.gabrieljamesbenedict;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
        BufferedReader reader;

        for (int i = 0; i < args.length; i++) {
            if (i < 1) continue;

            files.add(new File(args[i]));
        }

        for (File file : files) {
            try {
                reader = new BufferedReader(new FileReader(file));
                String code = reader.lines().reduce(
                        (a, b) ->
                            a + " " + b
                ).orElse("Error: Code reading error");



            } catch (FileNotFoundException e) {
                System.out.println("Error: Cannot find file \"" + file.getName() + "\"");
                return;
            }
        }
    }
}

