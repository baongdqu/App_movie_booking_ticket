package com.example.app_movie_booking_ticket.model;

public class Ticket {
    private String movieTitle;
    private String date;
    private String time;
    private String seats;
    private int totalPrice;
    private long timestamp;

    public Ticket() {
    }

    public Ticket(String movieTitle, String date, String time, String seats, int totalPrice, long timestamp) {
        this.movieTitle = movieTitle;
        this.date = date;
        this.time = time;
        this.seats = seats;
        this.totalPrice = totalPrice;
        this.timestamp = timestamp;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getSeats() {
        return seats;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
