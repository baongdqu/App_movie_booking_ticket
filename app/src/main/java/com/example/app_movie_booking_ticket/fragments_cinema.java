package com.example.app_movie_booking_ticket;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Fragment for displaying nearby movie theaters/cinemas
 * Uses Google Places API Nearby Search to find cinemas near user's location
 * Results are sorted by distance from nearest to farthest
 */
public class fragments_cinema extends Fragment {

    private static final String TAG = "fragments_cinema";
    private static final int SEARCH_RADIUS = 10000; // 10km radius

    // Views
    private RecyclerView recyclerCinemas;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout loadingLayout;
    private LinearLayout emptyLayout;
    private TextView tvEmptyMessage;
    private TextView tvLastUpdated;
    private MaterialButton btnGrantPermission;
    private ImageView btnRefresh;

    // Data
    private CinemaAdapter cinemaAdapter;
    private FusedLocationProviderClient fusedLocationClient;
    private String apiKey;
    private double userLatitude = 0;
    private double userLongitude = 0;

    // Permission launcher
    private final ActivityResultLauncher<String[]> locationPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fineLocation = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                Boolean coarseLocation = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);

                if (Boolean.TRUE.equals(fineLocation) || Boolean.TRUE.equals(coarseLocation)) {
                    // Permission granted, get location
                    getCurrentLocationAndLoadCinemas();
                } else {
                    // Permission denied
                    showPermissionDenied();
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

        // Get API key from strings.xml
        apiKey = getString(R.string.google_places_api_key);

        // Setup RecyclerView
        setupRecyclerView();

        // Setup SwipeRefreshLayout
        setupSwipeRefresh();

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Check permissions and load cinemas
        checkPermissionAndLoadCinemas();

        // Refresh button click
        btnRefresh.setOnClickListener(v -> {
            checkPermissionAndLoadCinemas();
        });

        // Grant permission button
        btnGrantPermission.setOnClickListener(v -> {
            requestLocationPermission();
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
    }

    private void setupRecyclerView() {
        cinemaAdapter = new CinemaAdapter(requireContext(), apiKey);
        recyclerCinemas.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerCinemas.setAdapter(cinemaAdapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(
                R.color.profile_accent,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        swipeRefreshLayout.setOnRefreshListener(this::checkPermissionAndLoadCinemas);
    }

    private void checkPermissionAndLoadCinemas() {
        if (hasLocationPermission()) {
            getCurrentLocationAndLoadCinemas();
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
    private void getCurrentLocationAndLoadCinemas() {
        showLoading();

        CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                cancellationTokenSource.getToken())
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        userLatitude = location.getLatitude();
                        userLongitude = location.getLongitude();
                        Log.d(TAG, "Location obtained: " + userLatitude + ", " + userLongitude);
                        loadNearbyCinemas();
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
                        loadNearbyCinemas();
                    } else {
                        showError(getString(R.string.cinema_location_disabled));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get last location", e);
                    showError(getString(R.string.cinema_location_disabled));
                });
    }

    private void loadNearbyCinemas() {
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
                + "location=" + userLatitude + "," + userLongitude
                + "&radius=" + SEARCH_RADIUS
                + "&type=movie_theater"
                + "&language=vi"
                + "&key=" + apiKey;

        Log.d(TAG, "Fetching cinemas from: " + url);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "API call failed", e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showError(getString(R.string.cinema_error));
                    });
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = response.body().string();
                    Log.d(TAG, "API response received");

                    try {
                        List<Cinema> cinemas = parseCinemasFromJson(jsonData);

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                if (cinemas.isEmpty()) {
                                    showEmpty(getString(R.string.cinema_no_results));
                                } else {
                                    showCinemas(cinemas);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error", e);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                showError(getString(R.string.cinema_error));
                            });
                        }
                    }
                } else {
                    Log.e(TAG, "API response not successful: " + response.code());
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            showError(getString(R.string.cinema_error));
                        });
                    }
                }
            }
        });
    }

    private List<Cinema> parseCinemasFromJson(String jsonData) throws JSONException {
        List<Cinema> cinemas = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray results = jsonObject.getJSONArray("results");

        for (int i = 0; i < results.length(); i++) {
            JSONObject place = results.getJSONObject(i);

            Cinema cinema = new Cinema();
            cinema.setPlaceId(place.optString("place_id", ""));
            cinema.setName(place.optString("name", ""));
            cinema.setAddress(place.optString("vicinity", ""));

            // Get location
            JSONObject geometry = place.optJSONObject("geometry");
            if (geometry != null) {
                JSONObject location = geometry.optJSONObject("location");
                if (location != null) {
                    cinema.setLatitude(location.optDouble("lat", 0));
                    cinema.setLongitude(location.optDouble("lng", 0));
                }
            }

            // Get rating
            cinema.setRating(place.optDouble("rating", 0));
            cinema.setUserRatingsTotal(place.optInt("user_ratings_total", 0));

            // Get opening hours
            JSONObject openingHours = place.optJSONObject("opening_hours");
            if (openingHours != null) {
                cinema.setHasOpeningHours(true);
                cinema.setOpenNow(openingHours.optBoolean("open_now", false));
            }

            // Get photo reference
            JSONArray photos = place.optJSONArray("photos");
            if (photos != null && photos.length() > 0) {
                JSONObject firstPhoto = photos.getJSONObject(0);
                cinema.setPhotoReference(firstPhoto.optString("photo_reference", null));
            }

            // Calculate distance from user
            cinema.calculateDistance(userLatitude, userLongitude);

            cinemas.add(cinema);
        }

        // Sort by distance (nearest first)
        Collections.sort(cinemas, (c1, c2) -> Double.compare(c1.getDistance(), c2.getDistance()));

        return cinemas;
    }

    private void showLoading() {
        loadingLayout.setVisibility(View.VISIBLE);
        emptyLayout.setVisibility(View.GONE);
        recyclerCinemas.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void showCinemas(List<Cinema> cinemas) {
        loadingLayout.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.GONE);
        recyclerCinemas.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);

        cinemaAdapter.setCinemaList(cinemas);

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
        btnGrantPermission.setVisibility(View.GONE);
    }

    private void showError(String message) {
        loadingLayout.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.VISIBLE);
        recyclerCinemas.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);

        tvEmptyMessage.setText(message);
        btnGrantPermission.setVisibility(View.GONE);

        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showPermissionDenied() {
        loadingLayout.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.VISIBLE);
        recyclerCinemas.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);

        tvEmptyMessage.setText(R.string.cinema_location_permission_denied);
        btnGrantPermission.setVisibility(View.VISIBLE);
    }
}
