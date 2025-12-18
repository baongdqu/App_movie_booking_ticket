package com.example.app_movie_booking_ticket.model;

public class TicketSimple {

    // 🔥 ID của ticket trong Firebase
    private String ticketId;

    public String title;
    public String info;
    public String seats;
    public String posterUrl;
    public Movie movie;

    public TicketSimple() {}

    // ✅ Constructor cũ + thêm ticketId
    public TicketSimple(String ticketId,
                        Movie movie,
                        String title,
                        String info,
                        String seats,
                        String posterUrl) {

        this.ticketId = ticketId;
        this.movie = movie;
        this.title = title;
        this.info = info;
        this.seats = seats;
        this.posterUrl = posterUrl;
    }

    // 🔥 Getter bắt buộc để refund
    public String getTicketId() {
        return ticketId;
    }
}

