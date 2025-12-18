package com.example.app_movie_booking_ticket;

import java.util.Map;

public class extra_user {
    public String uid;
    public String fullName;
    public String email;
    public String phone;
    public String dateOfBirth;
    public String gender;

    // Preferences
    public Map<String, Object> moviePreferences;

    private String avatarUrl;

    // ✅ SỐ DƯ VÍ (BẮT BUỘC CHO REFUND)
    private long balance;

    // ⚠️ Constructor trống - bắt buộc để Firebase đọc/ghi dữ liệu
    public extra_user() {
    }

    // ✅ Constructor đầy đủ
    public extra_user(String uid, String fullName, String email,
                      String phone, String dateOfBirth, String gender) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.balance = 0; // ✅ Mặc định user mới = 0đ
    }

    // ----------------- Avatar -----------------
    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    // ----------------- Balance -----------------
    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }
}
