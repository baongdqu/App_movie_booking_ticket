package com.example.app_movie_booking_ticket.model;

public class extra_Movie {
    private String Title;
    private String Poster;
    private double Imdb;

    public extra_Movie() {} // Firebase bắt buộc cần constructor rỗng

    public extra_Movie(String title, String poster, double imdb) {
        Title = title;
        Poster = poster;
        Imdb = imdb;
    }

    public String getTitle() {
        return Title;
    }

    public String getPoster() {
        return Poster;
    }

    public double getImdb() {
        return Imdb;
    }
}
