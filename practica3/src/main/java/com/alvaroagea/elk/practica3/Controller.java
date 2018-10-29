package com.alvaroagea.elk.practica3;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.time.Instant;

class Controller {

    private RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(new HttpHost("localhost", 9200, "http")));


    /***
     * Index the doument in the system.
     * @param date Generation date.
     * @param author Author name.
     * @param message Message stored.
     */
    void index(Instant date, String author, String message) {
        throw new UnsupportedOperationException();
    }

    /***
     * Search by message text.
     * @param message Message to search.
     */
    void searchMessage(String message) {
        throw new UnsupportedOperationException();
    }

    /**
     * Search by author using wildcards.
     *
     * @param author Wildcard author.
     */
    void searchAuthor(String author) {
        throw new UnsupportedOperationException();
    }

    /**
     * Close is the method that closes the client.
     */
    void close() throws IOException {
        client.close();
    }

}
