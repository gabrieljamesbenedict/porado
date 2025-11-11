package com.gabrieljamesbenedict;

import java.io.File;
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
            case "compile": compile(args);

            default: {
                System.out.println("Error: Unknown command \"" + command + "\"");
            }
        }

    }

    private static void compile(String[] args) {
        ArrayList<File> files = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            if (i < 1) continue;

            files.add(new File(args[i]));
        }

        System.out.println(args);
    }
}

