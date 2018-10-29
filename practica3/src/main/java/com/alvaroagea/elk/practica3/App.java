package com.alvaroagea.elk.practica3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;

public class App {

    private static Controller controller = new Controller();

    public static void main(String[] args) throws IOException {
        readCommand();
    }


    private static void readCommand() throws IOException {
        String command;
        boolean exit = false;
        while (!exit) {
            System.out.println("(A)dd information - (S)earch message - Search Au(t)hor - (E)xit");
            command = readLine();
            switch (command.toLowerCase()) {
                case "a":
                    addDocument();
                    break;
                case "s":
                    searchMessage();
                    break;
                case "t":
                    searchAuthor();
                    break;
                case "e":
                    endApplication();
                    exit = true;
                    break;
            }
        }
    }

    private static void endApplication() throws IOException {
        controller.close();
        System.out.println("Goodbye!!");
    }

    private static void addDocument() throws IOException {
        System.out.println("Author:");
        String name = readLine();
        System.out.println("Text:");
        String data = readLine();

        controller.index(Instant.now(), name, data);
    }

    private static void searchMessage() throws IOException {
        System.out.println("Message:");
        String message = readLine();
        controller.searchMessage(message);
    }

    private static void searchAuthor() throws IOException {
        System.out.println("Author:");
        String author = readLine();
        controller.searchAuthor(author);
    }

    private static String readLine() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            return reader.readLine();
        }
    }

}
