package com.example.app_movie_booking_ticket;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(extra_language_helper.onAttach(newBase));
    }
}
