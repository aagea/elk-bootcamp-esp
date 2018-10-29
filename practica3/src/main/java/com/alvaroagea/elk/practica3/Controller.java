package com.alvaroagea.elk.practica3;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.Instant;

class Controller {

    /***
     * Index the doument in the system.
     * @param date Generation date.
     * @param author Author name.
     * @param message Message stored.
     */
    void index(Instant date, String author, String message) {
        throw new NotImplementedException();
    }

    /***
     * Search by message text.
     * @param message Message to search.
     */
    void searchMessage(String message) {
        throw new NotImplementedException();
    }

    /**
     * Search by author using wildcards.
     * @param author Wildcard author.
     */
    void searchAuthor(String author) {
        throw new NotImplementedException();
    }

}
