package com.alvaroagea.elk.practica4a;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class App {

    private static ShakespeareController controller = new Practica4AController();

    public static void main(String[] args) throws IOException {
        readCommand();
    }


    private static void readCommand() throws IOException {

        String command;
        boolean exit = false;
        while (!exit) {
            System.out.println("(G)et Line - (S)earch - (Q)uery string -(E)xit");
            command = readLine();
            switch (command.toLowerCase()) {
                case "s":
                    search();
                    break;
                case "g":
                    get();
                    break;
                case "q":
                    query();
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


    private static void search() throws IOException {
        System.out.println("Text:");
        String text = readLine();
        final List<ShakespeareEntry> entries = controller.search(text);
        printEntries(entries);
    }

    private static void get() throws IOException {
        System.out.println("ID:");
        String id = readLine();
        final List<ShakespeareEntry> entries = controller.get(id);
        printEntries(entries);
    }

    private static void query() throws IOException {
        System.out.println("Query:");
        String query = readLine();
        final List<ShakespeareEntry> messages = controller.query(query);
        printEntries(messages);
    }

    private static void printEntries(List<ShakespeareEntry> entries) {
        System.out.printf("Found entries: %d", entries.size());
        System.out.println();
        entries.forEach(txt -> {
            System.out.printf("ID: %s\tPlay: %s\tSpeaker: %s\tText: %s", txt.getLineID(),
                    txt.getPlayName(), txt.getSpeaker(),txt.getTextEntry());
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
