package com.example.app_movie_booking_ticket;

public class extra_user {
    public String uid;
    public String fullName;
    public String email;
    public String phone;

    // ⚠️ Constructor trống - bắt buộc để Firebase đọc/ghi dữ liệu
    public extra_user() {
    }

    // ✅ Constructor đầy đủ tham số, gán dữ liệu đúng cách
    public extra_user(String uid, String fullName, String email, String phone) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }
}
