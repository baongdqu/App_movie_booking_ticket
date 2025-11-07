package com.example.app_movie_booking_ticket.model;

public class extra_Movie {
    private String Title;
    private String Description;
    private String Poster;
    private double Imdb;
    private int Year;
    private double price;
    private String Time;
    private String Trailer;

    // ⚠️ Quan trọng: Cần constructor trống cho Firebase
    public extra_Movie() {}

    // Getter & Setter
    public String getTitle() { return Title; }
    public void setTitle(String title) { this.Title = title; }

    public String getDescription() { return Description; }
    public void setDescription(String description) { this.Description = description; }

    public String getPoster() { return Poster; }
    public void setPoster(String poster) { this.Poster = poster; }

    public double getImdb() { return Imdb; }
    public void setImdb(double imdb) { this.Imdb = imdb; }

    public int getYear() { return Year; }
    public void setYear(int year) { this.Year = year; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getTime() { return Time; }
    public void setTime(String time) { this.Time = time; }

    public String getTrailer() { return Trailer; }
    public void setTrailer(String trailer) { this.Trailer = trailer; }
}
