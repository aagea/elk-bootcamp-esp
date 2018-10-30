package com.alvaroagea.elk.practica3;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;

import org.elasticsearch.client.RequestOptions;
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
    void index(Instant date, String author, String message) throws IOException {

    }

    /***
     * Search by message text.
     * @param message Message to search.
     */
    void searchMessage(String message) throws IOException {

    }

    /**
     * Search by author using wildcards.
     *
     * @param author Wildcard author.
     */
    void searchAuthor(String author) throws IOException {

    }

    void reset() throws IOException{
        DeleteIndexRequest request = new DeleteIndexRequest("message");
        DeleteIndexResponse deleteIndexResponse = client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(deleteIndexResponse.toString());
    }

    /**
     * Close is the method that closes the client.
     */
    void close() throws IOException {
        client.close();
    }

}
