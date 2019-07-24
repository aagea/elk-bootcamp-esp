package com.alvaroagea.elk.practica5;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

public class App {

    private static final String COMMA_DELIMITER = ",";
    private static final int NUM_ITERATION = 10;
    private static RecordController controller = new Practica5Controller();

    public static void main(String[] args) throws IOException, InterruptedException {
        readCommand();
    }

    private static void readCommand() throws IOException, InterruptedException {
        controller.init();
        String command;
        boolean exit = false;
        while (!exit) {
            System.out.println("(I)ndex - (A)ggregate - (R)eset -(E)xit");
            command = readLine();
            switch (command.toLowerCase()) {
                case "i":
                    index();
                    break;
                case "a":
                    aggregate();
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
        controller.init();
    }

    private static void endApplication() throws IOException {
        controller.close();
        System.out.println("Goodbye!!");
    }


    private static void aggregate() throws IOException {
        System.out.println("getOlympicWinnerByYear");
        printList(controller.getOlympicWinnerByYear());
        System.out.println("getTop10Athletes");
        printList(controller.getTop10Athletes());
        System.out.println("getTop10Countries");
        printList(controller.getTop10Countries());
        System.out.println("getAthleteWithMoreMedalsByCountry");
        printList(controller.getAthleteWithMoreMedalsByCountry());
    }


    private static void index() throws IOException, InterruptedException {
        try (Scanner scanner = new Scanner(new File("summer.csv"));) {
            int i = 0;
            while (scanner.hasNextLine()) {
                i++;
                indexLine(scanner.nextLine(), i);
                if (i % 1000 == 0) {
                    System.out.println("Processing..." + i);
                }
            }
        }
        controller.flush();

        for (int i = 0; i < NUM_ITERATION && controller.count() != 31165; i++) {
            Thread.sleep(2000);
        }
    }

    //Year,City,Sport,Discipline,Athlete,Country,Gender,Event,Medal
    private static void indexLine(String line, int i) throws IOException {


        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(COMMA_DELIMITER);
            while (rowScanner.hasNext()) {

                String year = rowScanner.next();
                String city = rowScanner.next();
                String sport = rowScanner.next();
                String discipline = rowScanner.next();
                String athlete = rowScanner.next();
                String country = rowScanner.next();
                String gender = rowScanner.next();
                String event = rowScanner.next();
                String medal = rowScanner.next();

                Record record = new Record(year, city, sport, discipline, athlete, country, gender, event, medal);

                controller.index(record);

            }
        } catch (Exception ex) {
            System.out.println("Error in Document: " + i);
        }

    }

    private static void printList(List<?> list) {
        System.out.printf("Number of items: %d", list.size());
        System.out.println();
        list.forEach(i -> System.out.println(i.toString()));
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
