package com.example.app_movie_booking_ticket.model;

public class ReviewModel {
    private String userId;
    private String userName;
    private String userAvatar; // Nếu bạn muốn hiển thị ảnh đại diện
    private int rating;
    private String comment;
    private long timestamp;

    // Constructor mặc định cho Firebase
    public ReviewModel() {
    }

    public ReviewModel(String userId, String userName, String userAvatar, int rating, String comment, long timestamp) {
        this.userId = userId;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    // Getters và Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}