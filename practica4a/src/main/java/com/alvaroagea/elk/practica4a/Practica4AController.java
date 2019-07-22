package com.alvaroagea.elk.practica4a;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

final class Practica4AController extends ShakespeareController {

    private final Logger logger = LogManager.getLogger();

    @Override
    List<ShakespeareEntry> search(String text) throws IOException {
        return null;
    }

    @Override
    List<ShakespeareEntry> get(String id) throws IOException {
        return null;
    }

    @Override
    List<ShakespeareEntry> query(String query) throws IOException {
        return null;
    }
}
