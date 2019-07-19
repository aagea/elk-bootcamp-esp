package com.alvaroagea.elk.practica3;

import java.time.Instant;

/**
 * Represent a message store in the ElasticSearch server.
 */
public class Message {
    /**
     * Time is the creation time of the message.
     */
    private final Instant time;

    /**
     * Author is the creator of the message.
     */
    private final String author;

    /**
     * Message is the sent message.
     */
    private final String message;

    /**
     * Basic constructor for Message class.
     *
     * @param time    Creation time.
     * @param author  Message author.
     * @param message Data of the message.
     */
    public Message(Instant time, String author, String message) {
        this.time = time;
        this.author = author;
        this.message = message;
    }

    /**
     * Get the creation time.
     *
     * @return The creation time.
     */
    public Instant getTime() {
        return time;
    }

    /**
     * Get the name of the author.
     *
     * @return The name of the author.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Get the data of the message.
     *
     * @return The data of the message.
     */
    public String getMessage() {
        return message;
    }
}
