package com.example.app_movie_booking_ticket;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

/**
 * Activity to display detailed information about a cinema
 */
public class CinemaDetailActivity extends AppCompatActivity {

    public static final String EXTRA_CINEMA = "extra_cinema";

    private ImageView imgCinema;
    private TextView tvCinemaName;
    private TextView tvDistance;
    private TextView tvRating;
    private TextView tvRatingCount;
    private TextView tvStatus;
    private TextView tvAddress;
    private TextView tvPhone;
    private TextView tvWorkingHours;
    private TextView tvScreens;
    private ChipGroup chipGroupAmenities;
    private LinearLayout layoutPhone;
    private LinearLayout layoutHours;
    private LinearLayout layoutScreens;
    private MaterialButton btnCall;
    private MaterialButton btnDirections;
    private MaterialToolbar toolbar;

    private Cinema cinema;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layouts_cinema_detail);

        // Get cinema from intent
        cinema = (Cinema) getIntent().getSerializableExtra(EXTRA_CINEMA);
        if (cinema == null) {
            finish();
            return;
        }

        initViews();
        setupToolbar();
        bindData();
        setupClickListeners();
    }

    private void initViews() {
        imgCinema = findViewById(R.id.imgCinema);
        tvCinemaName = findViewById(R.id.tvCinemaName);
        tvDistance = findViewById(R.id.tvDistance);
        tvRating = findViewById(R.id.tvRating);
        tvRatingCount = findViewById(R.id.tvRatingCount);
        tvStatus = findViewById(R.id.tvStatus);
        tvAddress = findViewById(R.id.tvAddress);
        tvPhone = findViewById(R.id.tvPhone);
        tvWorkingHours = findViewById(R.id.tvWorkingHours);
        tvScreens = findViewById(R.id.tvScreens);
        chipGroupAmenities = findViewById(R.id.chipGroupAmenities);
        layoutPhone = findViewById(R.id.layoutPhone);
        layoutHours = findViewById(R.id.layoutHours);
        layoutScreens = findViewById(R.id.layoutScreens);
        btnCall = findViewById(R.id.btnCall);
        btnDirections = findViewById(R.id.btnDirections);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void bindData() {
        // Cinema image
        String photoUrl = cinema.getPhotoReference();
        if (photoUrl != null && !photoUrl.isEmpty()) {
            Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.ic_cinema)
                    .error(R.drawable.ic_cinema)
                    .centerCrop()
                    .into(imgCinema);
        }

        // Basic info
        tvCinemaName.setText(cinema.getName());
        tvDistance.setText(cinema.getFormattedDistance());
        tvAddress.setText(cinema.getAddress());

        // Rating
        if (cinema.getRating() > 0) {
            tvRating.setText(String.format("%.1f", cinema.getRating()));
            tvRatingCount.setText("(" + cinema.getUserRatingsTotal() + " đánh giá)");
        } else {
            tvRating.setText("N/A");
            tvRatingCount.setVisibility(View.GONE);
        }

        // Status
        if (cinema.hasOpeningHours()) {
            tvStatus.setVisibility(View.VISIBLE);
            if (cinema.isOpenNow()) {
                tvStatus.setText(R.string.cinema_open_now);
                tvStatus.setBackgroundResource(R.drawable.bg_status_open);
            } else {
                tvStatus.setText(R.string.cinema_closed);
                tvStatus.setBackgroundResource(R.drawable.bg_status_closed);
            }
        } else {
            tvStatus.setVisibility(View.GONE);
        }

        // Phone
        String phone = cinema.getPhone();
        if (phone != null && !phone.isEmpty()) {
            layoutPhone.setVisibility(View.VISIBLE);
            tvPhone.setText(phone);
        } else {
            layoutPhone.setVisibility(View.GONE);
            btnCall.setVisibility(View.GONE);
        }

        // Working hours
        String workingHours = cinema.getWorkingHours();
        if (workingHours != null && !workingHours.isEmpty()) {
            layoutHours.setVisibility(View.VISIBLE);
            tvWorkingHours.setText(workingHours);
        } else {
            layoutHours.setVisibility(View.GONE);
        }

        // Screens
        int screens = cinema.getScreens();
        if (screens > 0) {
            layoutScreens.setVisibility(View.VISIBLE);
            tvScreens.setText(screens + " phòng chiếu");
        } else {
            layoutScreens.setVisibility(View.GONE);
        }

        // Amenities
        if (cinema.getAmenities() != null && !cinema.getAmenities().isEmpty()) {
            chipGroupAmenities.removeAllViews();
            for (String amenity : cinema.getAmenities()) {
                Chip chip = new Chip(this);
                chip.setText(amenity);
                chip.setClickable(false);
                chip.setChipBackgroundColorResource(R.color.profile_accent);
                chip.setTextColor(getResources().getColor(android.R.color.white, null));
                chipGroupAmenities.addView(chip);
            }
        } else {
            chipGroupAmenities.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        // Call button
        btnCall.setOnClickListener(v -> {
            String phone = cinema.getPhone();
            if (phone != null && !phone.isEmpty()) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phone.replaceAll("\\s+", "")));
                startActivity(callIntent);
            }
        });

        // Phone row click
        layoutPhone.setOnClickListener(v -> btnCall.performClick());

        // Directions button
        btnDirections.setOnClickListener(v -> {
            String uri = "google.navigation:q=" + cinema.getLatitude() + "," + cinema.getLongitude();
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                // Fallback to browser
                String browserUri = "https://www.google.com/maps/dir/?api=1&destination="
                        + cinema.getLatitude() + "," + cinema.getLongitude();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(browserUri)));
            }
        });
    }
}
