package com.example.app_movie_booking_ticket;

public class extra_user {
    public String uid;
    public String fullName;
    public String email;
    public String phone;
    public String dateOfBirth;
    public String gender;

    private String avatarUrl;

    // ⚠️ Constructor trống - bắt buộc để Firebase đọc/ghi dữ liệu
    public extra_user() {
    }

    // ✅ Constructor đầy đủ tham số, gán dữ liệu đúng cách
    public extra_user(String uid, String fullName, String email, String phone, String dateOfBirth, String gender) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
