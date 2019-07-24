package com.alvaroagea.elk.practica5;

import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
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
import java.util.List;

abstract class RecordController {


    static final String RECORD_INDEX = "records";

    static final String YEAR_FIELD="year";
    static final String CITY_FIELD="city";
    static final String SPORT_FIELD="sport";
    static final String DISCIPLINE_FIELD="discipline";
    static final String ATHLETE_FIELD="athlete";
    static final String COUNTRY_FIELD="country";
    static final String GENDER_FIELD="gender";
    static final String EVENT_FIELD="event";
    static final String MEDAL_FIELD="medal";


    final RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(new HttpHost("localhost", 9200, "http")));


    abstract void index(Record record) throws IOException;

    abstract List<OlympicWinner> getOlympicWinnerByYear() throws IOException;

    abstract List<OlympicAthletes> getTop10Athletes() throws IOException;

    abstract List<OlympicCountry> getTop10Countries() throws IOException;

    abstract List<OlympicAthletes> getAthleteWithMoreMedalsByCountry() throws IOException;


    final void init() throws IOException {

        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("properties");
            {
                builder.startObject(YEAR_FIELD);
                {
                    builder.field("type", "keyword");
                    builder.field("store", true);
                }
                builder.endObject();

                builder.startObject(CITY_FIELD);
                {
                    builder.field("type", "keyword");
                    builder.field("store", true);
                }
                builder.endObject();
                builder.startObject(SPORT_FIELD);
                {
                    builder.field("type", "keyword");
                    builder.field("store", true);
                }
                builder.endObject();

                builder.startObject(DISCIPLINE_FIELD);
                {
                    builder.field("type", "keyword");
                    builder.field("store", true);
                }
                builder.endObject();
                builder.startObject(ATHLETE_FIELD);
                {
                    builder.field("type", "keyword");
                    builder.field("store", true);
                }
                builder.endObject();

                builder.startObject(COUNTRY_FIELD);
                {
                    builder.field("type", "keyword");
                    builder.field("store", true);
                }
                builder.endObject();
                builder.startObject(GENDER_FIELD);
                {
                    builder.field("type", "keyword");
                    builder.field("store", true);
                }
                builder.endObject();

                builder.startObject(EVENT_FIELD);
                {
                    builder.field("type", "keyword");
                    builder.field("store", true);
                }
                builder.endObject();
                builder.startObject(MEDAL_FIELD);
                {
                    builder.field("type", "keyword");
                    builder.field("store", true);
                }
                builder.endObject();
            }
            builder.endObject();
        }
        builder.endObject();
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(RECORD_INDEX)
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
        DeleteIndexRequest request = new DeleteIndexRequest(RECORD_INDEX);
        AcknowledgedResponse acknowledgedResponse = client.indices().delete(request,
                RequestOptions.DEFAULT);
        if (!acknowledgedResponse.isAcknowledged()) {
            throw new UnexpectedException("The command has failed.");
        }
    }

    final void flush() throws IOException {
        FlushRequest flushRequest = new FlushRequest(RECORD_INDEX);
        client.indices().flush(flushRequest,
                RequestOptions.DEFAULT);
    }

    final long count() throws IOException {

        CountRequest countRequest = new CountRequest(RECORD_INDEX);

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
