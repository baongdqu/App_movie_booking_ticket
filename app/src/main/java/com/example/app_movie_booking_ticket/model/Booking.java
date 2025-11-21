package com.example.app_movie_booking_ticket.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Booking implements Serializable {
    private String movieTitle;
    private String date;
    private String time;
    private ArrayList<String> seats;
    private int pricePerSeat;

    public Booking() {
        seats = new ArrayList<>();
    }

    public Booking(String movieTitle, String date, String time, ArrayList<String> seats, int pricePerSeat) {
        this.movieTitle = movieTitle;
        this.date = date;
        this.time = time;
        this.seats = seats;
        this.pricePerSeat = pricePerSeat;
    }

    // Getters & Setters
    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public ArrayList<String> getSeats() { return seats; }
    public void setSeats(ArrayList<String> seats) { this.seats = seats; }

    public int getPricePerSeat() { return pricePerSeat; }
    public void setPricePerSeat(int pricePerSeat) { this.pricePerSeat = pricePerSeat; }

    public int getTotalPrice() {
        return pricePerSeat * seats.size();
    }
}
