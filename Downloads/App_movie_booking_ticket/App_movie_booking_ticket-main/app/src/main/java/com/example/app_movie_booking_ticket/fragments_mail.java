package com.example.app_movie_booking_ticket;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.app_movie_booking_ticket.R;

public class fragments_mail extends Fragment {

    public fragments_mail() {
        // Bắt buộc phải có constructor rỗng
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout fragment_mail.xml
        return inflater.inflate(R.layout.layouts_fragments_mail, container, false);
    }
}
