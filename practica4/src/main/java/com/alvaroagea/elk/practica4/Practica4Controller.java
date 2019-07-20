package com.alvaroagea.elk.practica4;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

final class Practica4Controller extends EventController {

    private final Logger logger = LogManager.getLogger();

    @Override
    public Optional<List<Event>> last(String id, int limit) {
        return Optional.empty();
    }

    @Override
    public Optional<List<Event>> last(String id, List<String> tags, int limit, Instant before) {
        return Optional.empty();
    }

    @Override
    public Optional<List<Event>> lastDistinct(String id, List<String> tags, int limit, Optional<Instant> before) {
        return Optional.empty();
    }

    @Override
    public Optional<List<Event>> search(String id, List<String> tags, Instant before, Instant after, int limit) {
        return Optional.empty();
    }

    @Override
    public Optional<List<Event>> searchDistinct(String id, List<String> tags, Instant before, Instant after, int limit) {
        return Optional.empty();
    }

    @Override
    public Optional<List<Event>> first(String id, List<String> tags, int limit, Instant after) {
        return Optional.empty();
    }

    @Override
    public Optional<List<Event>> firstDistinct(String id, List<String> tags, int limit, Instant after) {
        return Optional.empty();
    }
}
