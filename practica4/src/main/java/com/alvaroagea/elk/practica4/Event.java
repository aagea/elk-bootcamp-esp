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


    // Overriding equals() to compare two Complex objects
    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Event)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Event c = (Event) o;

        // Compare the data members and return accordingly
        return this.id.compareTo(c.id) == 0 &&
                this.tag.compareTo(c.tag) == 0 &&
                this.t1.compareTo(c.t1) == 0 &&
                this.t2.compareTo(c.t2) == 0;

    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + this.id.hashCode();
        hash = 53 * hash + this.tag.hashCode();
        hash = 53 * hash + this.t1.hashCode();
        hash = 53 * hash + this.t2.hashCode();
        return hash;
    }
}
