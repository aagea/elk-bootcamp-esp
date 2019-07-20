package com.alvaroagea.elk.practica3;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

final class Practica3Controller extends Controller {

    private final Logger logger = LogManager.getLogger();


    @Override
    void index(Message message) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    List<Message> searchMessage(String message) throws IOException {
        throw new UnsupportedOperationException();

    }

    @Override
    List<Message> searchAuthor(String author) throws IOException {
        throw new UnsupportedOperationException();
    }
}
