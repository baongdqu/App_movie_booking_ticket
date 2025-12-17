package com.example.app_movie_booking_ticket.model;

public class TicketSimple {

    public String title;
    public String info;
    public String seats;
    public String posterUrl;
    public Movie movie;

    public TicketSimple(Movie movie,String title, String info, String seats, String posterUrl) {
        this.title = title;
        this.info = info;
        this.seats = seats;
        this.posterUrl = posterUrl;
        this.movie = movie;
    }
}
