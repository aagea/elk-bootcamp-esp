package com.alvaroagea.elk.practica3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.List;

public class App {

    private static Controller controller = Practica3Controller.get();

    public static void main(String[] args) throws IOException {
        readCommand();
    }


    private static void readCommand() throws IOException {
        String command;
        boolean exit = false;
        while (!exit) {
            System.out.println("(A)dd information - (S)earch message - Search Au(T)hor - (R)eset -(E)xit");
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
                case "r":
                    reset();
                    break;
                case "e":
                    endApplication();
                    exit = true;
                    break;
            }
        }
    }

    private static void reset() throws IOException {
        controller.reset();
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

        controller.index(new Message(Instant.now(), name, data));
    }

    private static void searchMessage() throws IOException {
        System.out.println("Message:");
        String message = readLine();
        final List<Message> messages = controller.searchMessage(message);
        printMessages(messages);
    }

    private static void searchAuthor() throws IOException {
        System.out.println("Author:");
        String author = readLine();
        final List<Message> messages = controller.searchAuthor(author);
        printMessages(messages);
    }

    private static void printMessages(List<Message> messages){
        System.out.printf("Found messages: %d", messages.size());
        System.out.println();
        messages.forEach(msg -> {
            System.out.printf("Time: %s\tAuthor: %s\tMessage: %s", msg.getTime().toString(),
                    msg.getAuthor(), msg.getMessage());
            System.out.println();
        });
    }

    private static String readLine() throws IOException {
        if (System.console() != null) {
            return System.console().readLine();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                System.in));
        return reader.readLine();
    }

}
