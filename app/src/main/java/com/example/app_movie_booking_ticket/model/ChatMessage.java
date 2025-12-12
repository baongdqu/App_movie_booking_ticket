package com.example.app_movie_booking_ticket.model;

/**
 * Model class đại diện cho một tin nhắn trong chatbot
 */
public class ChatMessage {

    // Loại tin nhắn
    public static final int TYPE_USER = 0;
    public static final int TYPE_BOT = 1;

    private String message;
    private int type;
    private long timestamp;

    /**
     * Constructor cho tin nhắn mới
     * 
     * @param message Nội dung tin nhắn
     * @param type    TYPE_USER hoặc TYPE_BOT
     */
    public ChatMessage(String message, int type) {
        this.message = message;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Constructor đầy đủ với timestamp
     */
    public ChatMessage(String message, int type, long timestamp) {
        this.message = message;
        this.type = type;
        this.timestamp = timestamp;
    }

    // Getters
    public String getMessage() {
        return message;
    }

    public int getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // Helper methods
    public boolean isUser() {
        return type == TYPE_USER;
    }

    public boolean isBot() {
        return type == TYPE_BOT;
    }
}
