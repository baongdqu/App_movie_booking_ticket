package com.example.app_movie_booking_ticket;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class partuser_movie_preferences extends BaseActivity {

    private TextInputEditText inputGenre, inputMovieLanguage;
    private RadioGroup radioGroupSubtitle;
    private RadioButton radioDubbed, radioSubtitled, radioBoth;
    private Button btnSave, btnCancel;
    private ImageView btnBack;

    private DatabaseReference usersRef;
    private FirebaseAuth mAuth;
    private SharedPreferences prefs;
    private String currentUid;

    // Arrays for dropdown selections
    private String[] genres;
    private String[] movieLanguages;

    // Indices to store selection (Language independent)
    private int selectedGenreIndex = -1;
    private int selectedLanguageIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extra_themeutils.applySavedTheme(this);
        setContentView(R.layout.partuser_movie_preferences);

        // Initialize views
        btnBack = findViewById(R.id.btnBack);
        inputGenre = findViewById(R.id.inputGenre);
        inputMovieLanguage = findViewById(R.id.inputMovieLanguage);
        radioGroupSubtitle = findViewById(R.id.radioGroupSubtitle);
        radioDubbed = findViewById(R.id.radioDubbed);
        radioSubtitled = findViewById(R.id.radioSubtitled);
        radioBoth = findViewById(R.id.radioBoth);
        btnSave = findViewById(R.id.btnSavePreferences);
        btnCancel = findViewById(R.id.btnCancelPreferences);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Initialize arrays from resources (Auto-localized)
        genres = getResources().getStringArray(R.array.movie_genres_array);
        movieLanguages = getResources().getStringArray(R.array.movie_languages_array);

        // Get current user UID
        currentUid = prefs.getString("uid", null);
        if (currentUid == null && mAuth.getCurrentUser() != null) {
            currentUid = mAuth.getCurrentUser().getUid();
        }

        if (currentUid == null) {
            extra_sound_manager.playError(this);
            Toast.makeText(this, getString(R.string.toast_user_not_found), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup dropdown clicks
        setupGenreDropdown();
        setupLanguageDropdown();

        // Load existing preferences
        loadUserPreferences();

        // Setup button listeners
        btnBack.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            finish();
        });

        btnSave.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            savePreferences();
        });

        btnCancel.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            finish();
        });
    }

    private void setupGenreDropdown() {
        inputGenre.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(this)
                    .setTitle(getString(R.string.select_genre))
                    .setSingleChoiceItems(genres, selectedGenreIndex, (dialog, which) -> {
                        selectedGenreIndex = which;
                        inputGenre.setText(genres[which]);
                        dialog.dismiss();
                    })
                    .show();
        });
    }

    private void setupLanguageDropdown() {
        inputMovieLanguage.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(this)
                    .setTitle(getString(R.string.select_movie_language))
                    .setSingleChoiceItems(movieLanguages, selectedLanguageIndex, (dialog, which) -> {
                        selectedLanguageIndex = which;
                        inputMovieLanguage.setText(movieLanguages[which]);
                        dialog.dismiss();
                    })
                    .show();
        });
    }

    private void loadUserPreferences() {
        usersRef.child(currentUid).child("moviePreferences")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Try to load indices first (New format)
                            Integer genreIdx = snapshot.child("genreIndex").getValue(Integer.class);
                            Integer langIdx = snapshot.child("languageIndex").getValue(Integer.class);

                            // Load legacy string data
                            String genre = snapshot.child("favoriteGenre").getValue(String.class);
                            String language = snapshot.child("favoriteLanguage").getValue(String.class);
                            String subtitlePref = snapshot.child("subtitlePreference").getValue(String.class);

                            // Apply Genre
                            if (genreIdx != null && genreIdx >= 0 && genreIdx < genres.length) {
                                selectedGenreIndex = genreIdx;
                                inputGenre.setText(genres[selectedGenreIndex]);
                            } else if (genre != null && !genre.isEmpty()) {
                                inputGenre.setText(genre); // Fallback to legacy text
                            }

                            // Apply Language
                            if (langIdx != null && langIdx >= 0 && langIdx < movieLanguages.length) {
                                selectedLanguageIndex = langIdx;
                                inputMovieLanguage.setText(movieLanguages[selectedLanguageIndex]);
                            } else if (language != null && !language.isEmpty()) {
                                inputMovieLanguage.setText(language); // Fallback to legacy text
                            }

                            // Apply Subtitle Preference
                            if (subtitlePref != null) {
                                switch (subtitlePref) {
                                    case "dubbed":
                                        radioDubbed.setChecked(true);
                                        break;
                                    case "subtitled":
                                        radioSubtitled.setChecked(true);
                                        break;
                                    case "both":
                                        radioBoth.setChecked(true);
                                        break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        extra_sound_manager.playError(partuser_movie_preferences.this);
                        Toast.makeText(partuser_movie_preferences.this,
                                String.format(getString(R.string.toast_load_error), error.getMessage()),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void savePreferences() {
        String selectedGenre = Objects.requireNonNull(inputGenre.getText()).toString().trim();
        String selectedLanguage = Objects.requireNonNull(inputMovieLanguage.getText()).toString().trim();

        // Get subtitle preference
        String subtitlePref = "";
        int selectedRadioId = radioGroupSubtitle.getCheckedRadioButtonId();
        if (selectedRadioId == R.id.radioDubbed) {
            subtitlePref = "dubbed";
        } else if (selectedRadioId == R.id.radioSubtitled) {
            subtitlePref = "subtitled";
        } else if (selectedRadioId == R.id.radioBoth) {
            subtitlePref = "both";
        }

        // Validate at least one field is filled
        if (selectedGenre.isEmpty() && selectedLanguage.isEmpty() && subtitlePref.isEmpty()) {
            extra_sound_manager.playError(this);
            Toast.makeText(this, getString(R.string.toast_select_at_least_one), Toast.LENGTH_SHORT).show();
            return;
        }

        // Create preferences map
        Map<String, Object> preferences = new HashMap<>();
        // Save both Text (for easy reading/legacy) and Index (for localization)
        preferences.put("favoriteGenre", selectedGenre);
        preferences.put("genreIndex", selectedGenreIndex);

        preferences.put("favoriteLanguage", selectedLanguage);
        preferences.put("languageIndex", selectedLanguageIndex);

        preferences.put("subtitlePreference", subtitlePref);

        // Save to Firebase
        usersRef.child(currentUid).child("moviePreferences").setValue(preferences)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        extra_sound_manager.playSuccess(this);
                        Toast.makeText(this, getString(R.string.toast_preferences_saved), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        extra_sound_manager.playError(this);
                        Toast.makeText(this,
                                String.format(getString(R.string.toast_update_error),
                                        Objects.requireNonNull(task.getException()).getMessage()),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
