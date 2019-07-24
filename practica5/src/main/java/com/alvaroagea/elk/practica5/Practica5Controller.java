package com.alvaroagea.elk.practica5;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

final class Practica5Controller extends RecordController {

    private final Logger logger = LogManager.getLogger();


    @Override
    void index(Record record) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    List<OlympicWinner> getOlympicWinnerByYear() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    List<OlympicAthletes> getTop10Athletes() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    List<OlympicCountry> getTop10Countries() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    List<OlympicAthletes> getAthleteWithMoreMedalsByCountry() throws IOException {
        throw new UnsupportedOperationException();
    }
}
