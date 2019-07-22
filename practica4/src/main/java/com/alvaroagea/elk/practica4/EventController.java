package com.alvaroagea.elk.practica4;

import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.rmi.UnexpectedException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

abstract class EventController {

    private final Logger logger = LogManager.getLogger();

    final RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(
                    new HttpHost("localhost", 9200, "http")
            ));

    static final String EVENT_INDEX = "events";

    static final String ID_FIELD = "idEvent";

    static final String TAG_FIELD = "tag";

    static final String T1_FIELD = "t1";

    static final String T2_FIELD = "t2";


    /**
     * The last n occurrences.
     *
     * @param id    Id of the stream.
     * @param limit Query limit.
     * @return The list of events.
     */
    abstract Optional<List<Event>> last(String id, int limit);

    /**
     * Take the last occurrences of a list of tags.
     *
     * @param id     Id of the stream.
     * @param tags   List of tags.
     * @param limit  Query limit.
     * @param before Before of this date.
     * @return The list of events.
     */
    abstract Optional<List<Event>> last(String id, List<String> tags, int limit, Instant before);

    /**
     * Take the last distinct occurrences  of a list of tags.
     *
     * @param id     Id of the stream.
     * @param tags   List of tags.
     * @param limit  Query limit.
     * @param before Before of this date.
     * @return The list of events.
     */
    abstract Optional<List<Event>> lastDistinct(String id, List<String> tags, int limit, Instant before);

    /**
     * Take the list of the occurrences between two dates.
     *
     * @param id     Id of the stream.
     * @param tags   List of tags.
     * @param before Init time.
     * @param after  After time.
     * @param limit  Query limit.
     * @return The list of events.
     */
    abstract Optional<List<Event>> search(String id, List<String> tags, Instant before, Instant after, int limit);

    /**
     * Take the list of the distinct occurrences between two dates.
     *
     * @param id     Id of the stream.
     * @param tags   List of tags.
     * @param before Init time.
     * @param after  After time.
     * @param limit  Query limit.
     * @return The list of events.
     */
    abstract Optional<List<Event>> searchDistinct(String id, List<String> tags,
                                                         Instant before, Instant after, int limit);

    /**
     * Take the first occurrences of a list of tags.
     *
     * @param id    Id of the stream.
     * @param tags  List of tags.
     * @param limit Query limit.
     * @param after After of this date.
     * @return The list of events.
     */
    abstract Optional<List<Event>> first(String id, List<String> tags, int limit, Instant after);


    /**
     * Take the last distinct occurrences  of a list of tags.
     *
     * @param id    Id of the stream.
     * @param tags  List of tags.
     * @param limit Query limit.
     * @param after Before of this date.
     * @return The list of events.
     */
    abstract Optional<List<Event>> firstDistinct(String id, List<String> tags, int limit, Instant after);

    /**
     * Persist a event in the storage.
     *
     * @param event The selected event.
     * @return The event.
     */
    Optional<Event> index(Event event) {
        IndexRequest indexRequest = new IndexRequest(EventController.EVENT_INDEX)
                .source(EventController.ID_FIELD, event.getId(),
                        EventController.TAG_FIELD, event.getTag(),
                        EventController.T1_FIELD, event.getT1(),
                        EventController.T2_FIELD, event.getT2());

        try {
            IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            return Optional.empty();
        }
        return Optional.ofNullable(event);
    }


    final void init() throws IOException {

        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("properties");
            {
                builder.startObject(ID_FIELD);
                {
                    builder.field("type", "keyword");
                    builder.field("store", true);
                }
                builder.endObject();

                builder.startObject(TAG_FIELD);
                {
                    builder.field("type", "keyword");
                    builder.field("store", true);
                }
                builder.endObject();

                builder.startObject(T1_FIELD);
                {
                    builder.field("type", "date");
                }
                builder.endObject();

                builder.startObject(T2_FIELD);
                {
                    builder.field("type", "date");
                }
                builder.endObject();
            }
            builder.endObject();
        }
        builder.endObject();
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(EVENT_INDEX)
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
        DeleteIndexRequest request = new DeleteIndexRequest(EVENT_INDEX);
        AcknowledgedResponse acknowledgedResponse = client.indices().delete(request,
                RequestOptions.DEFAULT);
        if (!acknowledgedResponse.isAcknowledged()) {
            throw new UnexpectedException("The command has failed.");
        }
    }

    final void flush() throws IOException {
        FlushRequest flushRequest = new FlushRequest(EVENT_INDEX);
        client.indices().flush(flushRequest,
                RequestOptions.DEFAULT);
    }

    final long count() throws IOException {

        CountRequest countRequest = new CountRequest(EVENT_INDEX);

        final CountResponse countResponse = client.count(countRequest, RequestOptions.DEFAULT);
        return countResponse.getCount();
    }

    /**
     * Close is the method that closes the client.
     */
    void close() throws IOException {
        client.close();
    }
}
