package com.alvaroagea.elk.practica4;

import java.time.Instant;

public final class Event {
    private final String id;

    private final String tag;

    private final Instant t1;

    private final Instant t2;

    public Event(String id, String tag, Instant t1, Instant t2) {
        this.id = id;
        this.tag = tag;
        this.t1 = t1;
        this.t2 = t2;
    }

    public String getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }

    public Instant getT1() {
        return t1;
    }

    public Instant getT2() {
        return t2;
    }
}
