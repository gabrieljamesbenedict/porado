    package com.gabrieljamesbenedict.porado;

import lombok.RequiredArgsConstructor;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class PoradoInterpreter {

    private final String[] args;

    public void translate () {

//        System.out.println("Args: ");
//        for (String arg : args) {
//            System.out.println(arg);
//        }

        Iterator<String> fileIterator = Arrays.stream(args).iterator();

        while (fileIterator.hasNext()) {
            File file = new File(fileIterator.next());

            try (BufferedReader reader = new BufferedReader(new FileReader(file));) {

                StringWriter sw = new StringWriter();
                reader.transferTo(sw);
                List<Character> charList = sw
                        .toString()
                        .chars()
                        .mapToObj(c -> (char) c)
                        .collect(Collectors.toList());

                LexicalAnalyzer lexer = new LexicalAnalyzer(charList);

                lexer.tokenize().forEach(System.out::println);

            } catch (FileNotFoundException e) {
                System.out.println("File Error: Cannot find specified file \"" + file.getName() + "\"");
                return;
            } catch (IOException e) {
                System.out.println("File Error: Error in reading file  \"" + file.getName() + "\"");
                return;
            }

        }



    }
}
