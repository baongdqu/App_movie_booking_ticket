package com.example.app_movie_booking_ticket.model;

public class AppNotification {

    private String id;
    private String title;
    private String message;
    private long timestamp;
    private boolean read;
    private String type; // LOGIN / PROFILE

    public AppNotification() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
    public boolean isRead() { return read; }
    public String getType() { return type; }
}

