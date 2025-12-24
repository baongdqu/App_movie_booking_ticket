package com.example.app_movie_booking_ticket;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.app_movie_booking_ticket.adapter.CinemaAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Fragment for displaying nearby movie theaters/cinemas
 * Shows current location and calculates real-time distances
 * Reads cinema data from Firebase Realtime Database
 */
public class fragments_cinema extends Fragment {

    private static final String TAG = "fragments_cinema";

    // Default UIT coordinates
    private static final double DEFAULT_LAT = 10.8700;
    private static final double DEFAULT_LNG = 106.8031;

    // Views
    private RecyclerView recyclerCinemas;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout loadingLayout;
    private LinearLayout emptyLayout;
    private TextView tvEmptyMessage;
    private TextView tvLastUpdated;
    private MaterialButton btnGrantPermission;
    private ImageView btnRefresh;

    // Location card views
    private MaterialCardView locationCard;
    private TextView tvCurrentLocation;
    private TextView tvCoordinates;
    private ProgressBar progressLocation;
    private ImageView imgLocationStatus;
    private TextView tvCinemaCount;

    // Data
    private CinemaAdapter cinemaAdapter;
    private List<Cinema> cinemaList = new ArrayList<>();
    private DatabaseReference cinemasRef;
    private FusedLocationProviderClient fusedLocationClient;

    // Current location
    private double userLatitude = DEFAULT_LAT;
    private double userLongitude = DEFAULT_LNG;
    private boolean hasRealLocation = false;

    // Permission launcher
    private final ActivityResultLauncher<String[]> locationPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fineLocation = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                Boolean coarseLocation = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);

                if (Boolean.TRUE.equals(fineLocation) || Boolean.TRUE.equals(coarseLocation)) {
                    // Permission granted, get location
                    getCurrentLocation();
                } else {
                    // Permission denied, use default location
                    useDefaultLocation();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layouts_fragments_cinema, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        initViews(view);

        // Initialize Firebase reference
        cinemasRef = FirebaseDatabase.getInstance().getReference("Cinemas");

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Setup RecyclerView
        setupRecyclerView();

        // Setup SwipeRefreshLayout
        setupSwipeRefresh();

        // Check permissions and get location
        checkPermissionAndGetLocation();

        // Refresh button click
        btnRefresh.setOnClickListener(v -> {
            checkPermissionAndGetLocation();
        });

        // Grant permission button
        btnGrantPermission.setOnClickListener(v -> {
            requestLocationPermission();
        });

        // Location card click - retry getting location
        locationCard.setOnClickListener(v -> {
            checkPermissionAndGetLocation();
        });
    }

    private void initViews(View view) {
        recyclerCinemas = view.findViewById(R.id.recyclerCinemas);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        loadingLayout = view.findViewById(R.id.loadingLayout);
        emptyLayout = view.findViewById(R.id.emptyLayout);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage);
        tvLastUpdated = view.findViewById(R.id.tvLastUpdated);
        btnGrantPermission = view.findViewById(R.id.btnGrantPermission);
        btnRefresh = view.findViewById(R.id.btnRefresh);

        // Location card views
        locationCard = view.findViewById(R.id.locationCard);
        tvCurrentLocation = view.findViewById(R.id.tvCurrentLocation);
        tvCoordinates = view.findViewById(R.id.tvCoordinates);
        progressLocation = view.findViewById(R.id.progressLocation);
        imgLocationStatus = view.findViewById(R.id.imgLocationStatus);
        tvCinemaCount = view.findViewById(R.id.tvCinemaCount);
    }

    private void setupRecyclerView() {
        cinemaAdapter = new CinemaAdapter(requireContext(), "");
        recyclerCinemas.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerCinemas.setAdapter(cinemaAdapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(
                R.color.profile_accent,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        swipeRefreshLayout.setOnRefreshListener(this::checkPermissionAndGetLocation);
    }

    private void checkPermissionAndGetLocation() {
        showLocationLoading();

        if (hasLocationPermission()) {
            getCurrentLocation();
        } else {
            requestLocationPermission();
        }
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        locationPermissionLauncher.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        showLocationLoading();

        CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                cancellationTokenSource.getToken())
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        userLatitude = location.getLatitude();
                        userLongitude = location.getLongitude();
                        hasRealLocation = true;
                        Log.d(TAG, "Location obtained: " + userLatitude + ", " + userLongitude);
                        updateLocationDisplay(location);
                        loadCinemasFromFirebase();
                    } else {
                        // Try to get last known location
                        getLastKnownLocation();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get current location", e);
                    getLastKnownLocation();
                });
    }

    @SuppressLint("MissingPermission")
    private void getLastKnownLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        userLatitude = location.getLatitude();
                        userLongitude = location.getLongitude();
                        hasRealLocation = true;
                        updateLocationDisplay(location);
                        loadCinemasFromFirebase();
                    } else {
                        // Use default location
                        useDefaultLocation();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get last location", e);
                    useDefaultLocation();
                });
    }

    private void useDefaultLocation() {
        userLatitude = DEFAULT_LAT;
        userLongitude = DEFAULT_LNG;
        hasRealLocation = false;

        // Update UI
        progressLocation.setVisibility(View.GONE);
        imgLocationStatus.setVisibility(View.VISIBLE);
        tvCurrentLocation.setText(R.string.cinema_uit_location);
        tvCoordinates.setText(String.format(Locale.US, "%.4f, %.4f", userLatitude, userLongitude));
        tvCoordinates.setVisibility(View.VISIBLE);

        btnGrantPermission.setVisibility(View.VISIBLE);

        // Load cinemas with default location
        loadCinemasFromFirebase();
    }

    private void updateLocationDisplay(Location location) {
        progressLocation.setVisibility(View.GONE);
        imgLocationStatus.setVisibility(View.VISIBLE);
        btnGrantPermission.setVisibility(View.GONE);

        // Show coordinates
        tvCoordinates.setText(String.format(Locale.US, "%.4f, %.4f", location.getLatitude(), location.getLongitude()));
        tvCoordinates.setVisibility(View.VISIBLE);

        // Try to get address from coordinates
        getAddressFromLocation(location.getLatitude(), location.getLongitude());
    }

    private void getAddressFromLocation(double latitude, double longitude) {
        new Thread(() -> {
            try {
                Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    String addressText;

                    // Build a short address string
                    if (address.getThoroughfare() != null) {
                        addressText = address.getThoroughfare();
                        if (address.getSubLocality() != null) {
                            addressText += ", " + address.getSubLocality();
                        }
                    } else if (address.getSubLocality() != null) {
                        addressText = address.getSubLocality();
                        if (address.getLocality() != null) {
                            addressText += ", " + address.getLocality();
                        }
                    } else if (address.getLocality() != null) {
                        addressText = address.getLocality();
                    } else {
                        addressText = getString(R.string.cinema_location_found);
                    }

                    final String finalAddress = addressText;
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            tvCurrentLocation.setText(finalAddress);
                        });
                    }
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            tvCurrentLocation.setText(R.string.cinema_location_found);
                        });
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Geocoder error", e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        tvCurrentLocation.setText(R.string.cinema_location_found);
                    });
                }
            }
        }).start();
    }

    private void showLocationLoading() {
        progressLocation.setVisibility(View.VISIBLE);
        imgLocationStatus.setVisibility(View.GONE);
        tvCurrentLocation.setText(R.string.cinema_detecting_location);
        tvCoordinates.setVisibility(View.GONE);
    }

    private void loadCinemasFromFirebase() {
        showLoading();

        cinemasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cinemaList.clear();

                for (DataSnapshot cinemaSnapshot : snapshot.getChildren()) {
                    try {
                        Cinema cinema = new Cinema();

                        cinema.setPlaceId(cinemaSnapshot.child("id").getValue(String.class));
                        cinema.setName(cinemaSnapshot.child("name").getValue(String.class));
                        cinema.setAddress(cinemaSnapshot.child("address").getValue(String.class));

                        // Get coordinates
                        Double lat = cinemaSnapshot.child("latitude").getValue(Double.class);
                        Double lng = cinemaSnapshot.child("longitude").getValue(Double.class);
                        if (lat != null)
                            cinema.setLatitude(lat);
                        if (lng != null)
                            cinema.setLongitude(lng);

                        // Get rating
                        Double rating = cinemaSnapshot.child("rating").getValue(Double.class);
                        if (rating != null)
                            cinema.setRating(rating);

                        Integer userRatingsTotal = cinemaSnapshot.child("userRatingsTotal").getValue(Integer.class);
                        if (userRatingsTotal != null)
                            cinema.setUserRatingsTotal(userRatingsTotal);

                        // Calculate distance from user's current location
                        cinema.calculateDistance(userLatitude, userLongitude);

                        // Get image URL
                        String imageUrl = cinemaSnapshot.child("image").getValue(String.class);
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            cinema.setPhotoReference(imageUrl);
                        }

                        // Get phone
                        String phone = cinemaSnapshot.child("phone").getValue(String.class);
                        if (phone != null) {
                            cinema.setPhone(phone);
                        }

                        // Get screens count
                        Integer screens = cinemaSnapshot.child("screens").getValue(Integer.class);
                        if (screens != null) {
                            cinema.setScreens(screens);
                        }

                        // Get working hours
                        String workingHours = cinemaSnapshot.child("workingHours").getValue(String.class);
                        if (workingHours != null) {
                            cinema.setWorkingHours(workingHours);
                        }

                        // Get amenities
                        DataSnapshot amenitiesSnapshot = cinemaSnapshot.child("amenities");
                        if (amenitiesSnapshot.exists()) {
                            List<String> amenitiesList = new ArrayList<>();
                            for (DataSnapshot amenity : amenitiesSnapshot.getChildren()) {
                                String value = amenity.getValue(String.class);
                                if (value != null) {
                                    amenitiesList.add(value);
                                }
                            }
                            cinema.setAmenities(amenitiesList);
                        }

                        // Check if cinema is currently open based on working hours
                        cinema.setHasOpeningHours(true);
                        cinema.setOpenNow(isCinemaOpen(cinema.getWorkingHours()));

                        cinemaList.add(cinema);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing cinema: " + e.getMessage());
                    }
                }

                // Sort by calculated distance (nearest first)
                Collections.sort(cinemaList, (c1, c2) -> Double.compare(c1.getDistance(), c2.getDistance()));

                // Limit to 7 nearest cinemas only
                if (cinemaList.size() > 7) {
                    cinemaList = new ArrayList<>(cinemaList.subList(0, 7));
                }

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (cinemaList.isEmpty()) {
                            showEmpty(getString(R.string.cinema_no_results));
                        } else {
                            showCinemas(cinemaList);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Firebase error: " + error.getMessage());
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showError(getString(R.string.cinema_error));
                    });
                }
            }
        });
    }

    private void showLoading() {
        // Keep SwipeRefreshLayout indicator if it's refreshing (pull-to-refresh)
        if (!swipeRefreshLayout.isRefreshing()) {
            loadingLayout.setVisibility(View.VISIBLE);
        }
        emptyLayout.setVisibility(View.GONE);
        recyclerCinemas.setVisibility(View.GONE);
    }

    private void showCinemas(List<Cinema> cinemas) {
        loadingLayout.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.GONE);
        recyclerCinemas.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);

        cinemaAdapter.setCinemaList(cinemas);

        // Show cinema count
        tvCinemaCount.setText("hiện đang có " + cinemas.size() + " rạp gần bạn");
        tvCinemaCount.setVisibility(View.VISIBLE);

        // Update last updated time
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        tvLastUpdated.setText(getString(R.string.cinema_last_updated, currentTime));
        tvLastUpdated.setVisibility(View.VISIBLE);
    }

    private void showEmpty(String message) {
        loadingLayout.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.VISIBLE);
        recyclerCinemas.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);

        tvEmptyMessage.setText(message);
    }

    private void showError(String message) {
        loadingLayout.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.VISIBLE);
        recyclerCinemas.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);

        tvEmptyMessage.setText(message);

        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Check if cinema is currently open based on working hours string
     * Format expected: "HH:mm - HH:mm" (e.g., "09:00 - 23:30")
     */
    private boolean isCinemaOpen(String workingHours) {
        if (workingHours == null || workingHours.isEmpty()) {
            return true; // Assume open if no data
        }

        try {
            // Parse working hours (format: "09:00 - 23:30")
            String[] parts = workingHours.split(" - ");
            if (parts.length != 2) {
                return true;
            }

            String openTime = parts[0].trim();
            String closeTime = parts[1].trim();

            // Get current time
            java.util.Calendar now = java.util.Calendar.getInstance();
            int currentHour = now.get(java.util.Calendar.HOUR_OF_DAY);
            int currentMinute = now.get(java.util.Calendar.MINUTE);
            int currentTimeMinutes = currentHour * 60 + currentMinute;

            // Parse open time
            String[] openParts = openTime.split(":");
            int openHour = Integer.parseInt(openParts[0]);
            int openMinute = Integer.parseInt(openParts[1]);
            int openTimeMinutes = openHour * 60 + openMinute;

            // Parse close time
            String[] closeParts = closeTime.split(":");
            int closeHour = Integer.parseInt(closeParts[0]);
            int closeMinute = Integer.parseInt(closeParts[1]);
            int closeTimeMinutes = closeHour * 60 + closeMinute;

            // Handle overnight hours (e.g., 08:00 - 24:00 or 08:00 - 02:00)
            if (closeTimeMinutes <= openTimeMinutes) {
                // Overnight: open if current is after open OR before close
                return currentTimeMinutes >= openTimeMinutes || currentTimeMinutes < closeTimeMinutes;
            } else {
                // Normal: open if current is between open and close
                return currentTimeMinutes >= openTimeMinutes && currentTimeMinutes < closeTimeMinutes;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing working hours: " + e.getMessage());
            return true; // Assume open on error
        }
    }
}
