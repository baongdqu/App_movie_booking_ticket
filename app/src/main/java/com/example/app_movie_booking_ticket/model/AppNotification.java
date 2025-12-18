package com.example.app_movie_booking_ticket.model;

public class AppNotification {

    private String id;

    private String title;
    private String message;

    //  hiển thị thời gian
    private long timestamp;

    private boolean read;

    /**
     * type:
     * - LOGIN
     * - PROFILE
     * - REFUND
     */
    private String type;

    // 🔥 ID của ticket liên quan (chỉ dùng cho REFUND)
    private String ticketId;

    public AppNotification() {}

    // ===== GET / SET =====
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public String getMessage() { return message; }

    public long getTimestamp() { return timestamp; }

    public boolean isRead() { return read; }

    public String getType() { return type; }

    public String getTicketId() { return ticketId; }
}


