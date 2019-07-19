package com.alvaroagea.elk.practica3;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
