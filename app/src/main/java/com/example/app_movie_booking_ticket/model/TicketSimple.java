package com.example.app_movie_booking_ticket.model;

import java.util.List;

public class TicketSimple {
    private String ticketId;
    public String movieTitle;
    public String posterUrl;
    public String cinemaName;
    public String date;
    public String time;
    public List<String> seats;
    public long totalPrice;
    public String status;
    public long createdAt;

    // Node Payment bên trong JSON
    public PaymentInfo payment;

    public TicketSimple() {}

    // Lớp lồng để khớp với cấu trúc "payment": { ... } trong JSON
    public static class PaymentInfo {
        public String method;
        public String status;
        public long paidAt;

        public PaymentInfo() {}
    }

    // Getters
    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }
}