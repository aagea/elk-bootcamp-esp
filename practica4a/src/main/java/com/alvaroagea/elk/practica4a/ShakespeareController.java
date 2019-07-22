package com.alvaroagea.elk.practica4a;

import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;

import java.io.IOException;
import java.util.List;

abstract class ShakespeareController {
    private final Logger logger = LogManager.getLogger();

    final RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(
                    new HttpHost("localhost", 9200, "http")
            ));

    static final String SHAKESPEARE_INDEX = "shakespeare";

    static final String LINE_ID_FIELD = "line_id";

    static final String PLAY_NAME_FIELD = "play_name";

    static final String SPEECH_NUMBER_FIELD = "speech_number";

    static final String LINE_NUMBER_FIELD = "line_number";

    static final String SPEAKER_FIELD = "speaker";

    static final String TEXT_ENTRY_FIELD = "text_entry";


    final long count() throws IOException {

        CountRequest countRequest = new CountRequest(SHAKESPEARE_INDEX);

        final CountResponse countResponse = client.count(countRequest, RequestOptions.DEFAULT);
        return countResponse.getCount();
    }

    /**
     * Close is the method that closes the client.
     */
    void close() throws IOException {
        client.close();
    }

    /**
     * Recover a set text entries that matches with a bag of words.
     *
     * @param text Bag of words.
     * @return List of text entries.
     */
    abstract List<ShakespeareEntry> search(String text) throws IOException;

    /**
     * Recover the line id and 5 lines before and 5 lines after.
     *
     * @param id Line id.
     * @return List of text entries.
     */
    abstract List<ShakespeareEntry> get(String id) throws IOException;

    /**
     * Recover a list of text entries using a query string command.
     *
     * @param query Query string.
     * @return List of text entries.
     */
    abstract List<ShakespeareEntry> query(String query) throws IOException;
}
