package com.example.app_movie_booking_ticket.model;

import java.io.Serializable;
import java.util.List;

public class Movie implements Serializable {
    private String Title;
    private String Poster;
    private double Imdb;
    private List<String> Pcitures;
    private List<String> Genre;
    private String Description;
    private String Time;
    private int Year;
    private int price;
    private String Trailer;
    private List<Cast> Casts;

    // Firebase requires a no-argument constructor
    public Movie() {}

    public Movie(String title, String poster, double imdb, List<String> pictures, List<String> genre,
                 String description, String time, int year, int price,
                 String trailer, List<Cast> casts) {
        this.Title = title;
        this.Poster = poster;
        this.Imdb = imdb;
        this.Pcitures = pictures;
        this.Genre = genre;
        this.Description = description;
        this.Time = time;
        this.Year = year;
        this.price = price;
        this.Trailer = trailer;
        this.Casts = casts;
    }

    // Getters
    public String getTitle() {
        return Title;
    }

    public String getPoster() {
        return Poster;
    }

    public double getImdb() {
        return Imdb;
    }
    public List<String> getPicture(){
        return Pcitures;
    }
    public List<String> getGenre() {
        return Genre;
    }

    public String getDescription() {
        return Description;
    }

    public String getTime() {
        return Time;
    }

    public int getYear() {
        return Year;
    }

    public int getPrice() {
        return price;
    }

    public String getTrailer() {
        return Trailer;
    }

    public List<Cast> getCasts() {
        return Casts;
    }

    // Inner class for Cast objects
    public static class Cast implements Serializable{
        private String Actor;
        private String PicUrl;

        public Cast() {}

        public Cast(String actor, String picUrl) {
            this.Actor = actor;
            this.PicUrl = picUrl;
        }

        public String getActor() {
            return Actor;
        }

        public String getPicUrl() {
            return PicUrl;
        }
    }
}
