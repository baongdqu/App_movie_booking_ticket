package com.example.app_movie_booking_ticket.model;

import java.util.List;

public class Movie {

    private String Title;
    private String Poster;
    private String Description;
    private String Time;
    private Double Imdb;
    private int Year;
    private int price;
    private List<String> Genre;

    // Bắt buộc có constructor rỗng để Firebase ánh xạ dữ liệu
    public Movie() {}

    public String getTitle() { return Title; }
    public String getPoster() { return Poster; }
    public String getDescription() { return Description; }
    public String getTime() { return Time; }
    public Double getImdb() { return Imdb; }
    public int getYear() { return Year; }
    public int getPrice() { return price; }
    public List<String> getGenre() { return Genre; }
}
