package com.alvaroagea.elk.practica3;

import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.rmi.UnexpectedException;
import java.util.List;

public abstract class Controller {


    static final String MESSAGE_INDEX = "messages";

    static final String MESSAGE_FIELD = "message";
    static final String TIME_FIELD = "time";
    static final String AUTHOR_FIELD = "author";


    final RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(new HttpHost("localhost", 9200, "http")));


    /**
     * Index the doument in the system.
     *
     * @param message Message stored.
     */
    abstract void index(Message message) throws IOException;

    /**
     * Search messages using part message field.
     *
     * @param message Message filter.
     * @return List of messages.
     * @throws IOException In the case of ElasticSearch fails.
     */
    abstract List<Message> searchMessage(String message) throws IOException;


    /**
     * Search by Author name with wildcard filters.
     *
     * @param author Author filter with wildcards.
     * @return List of messages.
     * @throws IOException In the case of ElasticSearch fails.
     */
    abstract List<Message> searchAuthor(String author) throws IOException;

    final void init() throws IOException {

        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("properties");
            {
                builder.startObject(MESSAGE_FIELD);
                {
                    builder.field("type", "text");
                }
                builder.endObject();

                builder.startObject(AUTHOR_FIELD);
                {
                    builder.field("type", "keyword");
                    builder.field("store", true);
                }
                builder.endObject();

                builder.startObject(TIME_FIELD);
                {
                    builder.field("type", "date");
                }
                builder.endObject();
            }
            builder.endObject();
        }
        builder.endObject();
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(MESSAGE_INDEX)
                .mapping(builder)
                .settings(Settings.builder()
                        .put("index.number_of_shards", 1)
                        .put("index.number_of_replicas", 1)
                );
        try {
            AcknowledgedResponse acknowledgedResponse = client.indices().create(createIndexRequest,
                    RequestOptions.DEFAULT);
            if (!acknowledgedResponse.isAcknowledged()) {
                throw new UnexpectedException("The command has failed.");
            }
        } catch (ElasticsearchStatusException esStatusEx) {
            if (!esStatusEx.getDetailedMessage().contains("resource_already_exists_exception")) {
                throw esStatusEx;
            }
        }
    }

    final void reset() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(MESSAGE_INDEX);
        AcknowledgedResponse acknowledgedResponse = client.indices().delete(request,
                RequestOptions.DEFAULT);
        if (!acknowledgedResponse.isAcknowledged()) {
            throw new UnexpectedException("The command has failed.");
        }
    }

    final void flush() throws IOException {
        FlushRequest flushRequest = new FlushRequest(MESSAGE_INDEX);
        client.indices().flush(flushRequest,
                RequestOptions.DEFAULT);
    }

    /**
     * Close is the method that closes the client.
     */
    void close() throws IOException {
        client.close();
    }

}
