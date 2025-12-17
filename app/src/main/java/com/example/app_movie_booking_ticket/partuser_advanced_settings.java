package com.example.app_movie_booking_ticket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class partuser_advanced_settings extends extra_manager_language {

    private Switch switchDarkMode, switchNotification, switchSound, switchPinLock;
    private Button btnChangePassword, btnDeleteAccount, btnBackSettings, btnChangeLanguage, btnChangePin;
    private android.widget.TextView txtPinStatus;

    private SharedPreferences prefs;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extra_themeutils.applySavedTheme(this);
        setContentView(R.layout.partuser_advanced_settings);

        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchNotification = findViewById(R.id.switchNotification);
        switchSound = findViewById(R.id.switchSound);
        switchPinLock = findViewById(R.id.switchPinLock);
        txtPinStatus = findViewById(R.id.txtPinStatus);

        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        btnBackSettings = findViewById(R.id.btnBackSettings);
        btnChangeLanguage = findViewById(R.id.btnChangeLanguage);
        btnChangePin = findViewById(R.id.btnChangePin);

        mAuth = FirebaseAuth.getInstance();
        prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);

        switchDarkMode.setChecked(extra_themeutils.isDarkMode(this));
        switchNotification.setChecked(prefs.getBoolean("notifications", true));
        switchSound.setChecked(prefs.getBoolean("sound_enabled", true));

        // Init PIN state
        updatePinUI();

        // Switch Dark Mode
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            extra_sound_manager.playToggle(this);
            extra_themeutils.setDarkMode(this, isChecked);
            recreate();
        });

        // Switch Notification
        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            extra_sound_manager.playToggle(this);
            prefs.edit().putBoolean("notifications", isChecked).apply();
            Toast.makeText(this,
                    isChecked ? getString(R.string.toast_notification_on) : getString(R.string.toast_notification_off),
                    Toast.LENGTH_SHORT).show();
        });

        // Switch Sound
        switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            extra_sound_manager.playToggle(this);
            prefs.edit().putBoolean("sound_enabled", isChecked).apply();
            Toast.makeText(this, isChecked ? getString(R.string.toast_sound_on) : getString(R.string.toast_sound_off),
                    Toast.LENGTH_SHORT).show();
        });

        // Switch PIN Lock
        switchPinLock.setOnClickListener(v -> {
            extra_sound_manager.playToggle(this);
            boolean isChecked = switchPinLock.isChecked();
            // Revert state temporarily, change only on success
            switchPinLock.setChecked(!isChecked);

            if (isChecked) {
                // User wants to enable PIN
                showSetPinDialog();
            } else {
                // User wants to disable PIN
                showConfirmPinDialogForDisable();
            }
        });

        // Button Change PIN
        btnChangePin.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            showChangePinDialog();
        });

        // Nút Thay đổi Mật khẩu
        btnChangePassword.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            startActivity(new Intent(this, partuser_change_password.class));
        });

        // Nút Xóa Tài khoản
        btnDeleteAccount.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);

            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

            new MaterialAlertDialogBuilder(this, com.google.android.material.R.style.MaterialAlertDialog_Material3)
                    .setIcon(R.drawable.ic_warning_red)
                    .setTitle(getString(R.string.dialog_confirm_delete_title))
                    .setMessage(
                            getString(R.string.dialog_confirm_delete_message))
                    .setPositiveButton(getString(R.string.dialog_delete), (dialog, which) -> {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user == null) {
                            extra_sound_manager.playError(this);
                            Toast.makeText(this, getString(R.string.toast_user_not_found), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        TextInputLayout layout = new TextInputLayout(this, null,
                                com.google.android.material.R.style.Widget_Material3_TextInputLayout_OutlinedBox);
                        layout.setHint(getString(R.string.dialog_enter_password_hint));
                        layout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);

                        final TextInputEditText input = new TextInputEditText(this);
                        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        layout.addView(input);
                        layout.setEndIconActivated(false);
                        input.setSingleLine(true);
                        input.setImeOptions(EditorInfo.IME_ACTION_DONE);

                        layout.setEndIconOnClickListener(view -> {
                            if (input.getTransformationMethod() instanceof PasswordTransformationMethod) {
                                input.setTransformationMethod(null);
                                layout.setEndIconActivated(true);
                            } else {
                                input.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                layout.setEndIconActivated(false);
                            }
                            if (input.getText() != null)
                                input.setSelection(input.getText().length());
                        });

                        new MaterialAlertDialogBuilder(this)
                                .setTitle(getString(R.string.dialog_reauthenticate_title))
                                .setView(layout)
                                .setPositiveButton(getString(R.string.dialog_confirm), (d2, w2) -> {
                                    String password = input.getText() != null ? input.getText().toString().trim() : "";
                                    dialogConfirmDelete(user, password);
                                })
                                .setNegativeButton(getString(R.string.cancel), (d2, w2) -> d2.dismiss())
                                .show();

                        input.setOnEditorActionListener((v1, actionId, event) -> {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                String password = input.getText() != null ? input.getText().toString().trim() : "";
                                dialogConfirmDelete(user, password);
                                return true;
                            }
                            return false;
                        });

                    })
                    .setNegativeButton(getString(R.string.cancel), (dialog, which2) -> {
                        if (vibrator != null) {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                                vibrator.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE));
                            else
                                vibrator.vibrate(80);
                        }
                        dialog.dismiss();
                    })
                    .setCancelable(true)
                    .show();

            if (vibrator != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                    vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                else
                    vibrator.vibrate(100);
            }
        });

        // Nút Đổi Ngôn Ngữ
        // Nút Đổi Ngôn Ngữ
        btnChangeLanguage.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            String[] languages = {
                    getString(R.string.lang_vi),
                    getString(R.string.lang_en),
                    getString(R.string.lang_ru),
                    getString(R.string.lang_ja),
                    getString(R.string.lang_ko),
                    getString(R.string.lang_zh)
            };

            String currentLang = extra_language_helper.getLanguage(this);
            int checkedItem;
            switch (currentLang) {
                case "en":
                    checkedItem = 1;
                    break;
                case "ru":
                    checkedItem = 2;
                    break;
                case "ja":
                    checkedItem = 3;
                    break;
                case "ko":
                    checkedItem = 4;
                    break;
                case "zh":
                    checkedItem = 5;
                    break;
                default:
                    checkedItem = 0; // vi
            }

            new MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.language_selection_title))
                    .setSingleChoiceItems(languages, checkedItem, (dialog, which) -> {
                        String selectedLang;
                        switch (which) {
                            case 1:
                                selectedLang = "en";
                                break;
                            case 2:
                                selectedLang = "ru";
                                break;
                            case 3:
                                selectedLang = "ja";
                                break;
                            case 4:
                                selectedLang = "ko";
                                break;
                            case 5:
                                selectedLang = "zh";
                                break;
                            default:
                                selectedLang = "vi";
                        }

                        if (!currentLang.equals(selectedLang)) {
                            extra_language_helper.setLocale(this, selectedLang);
                            dialog.dismiss();
                            // Restart Application to apply language changes
                            Intent i = getBaseContext().getPackageManager()
                                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
                            if (i != null) {
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                            } else {
                                recreate();
                            }
                        } else {
                            dialog.dismiss();
                        }
                    })
                    .show();
        });

        // Nút Quay lại
        btnBackSettings.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            finish();
        });
    }

    // --- PIN Logic Helpers ---

    private void updatePinUI() {
        boolean isEmabled = prefs.getBoolean("pin_enabled", false);
        switchPinLock.setChecked(isEmabled);
        if (isEmabled) {
            btnChangePin.setVisibility(android.view.View.VISIBLE);
            txtPinStatus.setText(R.string.pin_status_active);
            txtPinStatus.setVisibility(android.view.View.VISIBLE);
        } else {
            btnChangePin.setVisibility(android.view.View.GONE);
            txtPinStatus.setText(R.string.pin_status_inactive);
            txtPinStatus.setVisibility(android.view.View.GONE);
        }
    }

    private void showSetPinDialog() {
        TextInputLayout layout = new TextInputLayout(this, null,
                com.google.android.material.R.style.Widget_Material3_TextInputLayout_OutlinedBox);
        layout.setHint(getString(R.string.dialog_enter_new_pin));

        // Cần padding cho đẹp
        android.widget.FrameLayout container = new android.widget.FrameLayout(this);
        android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(60, 20, 60, 10); // left, top, right, bottom
        layout.setLayoutParams(params);
        container.addView(layout);

        final TextInputEditText input = new TextInputEditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        input.setFilters(new android.text.InputFilter[] { new android.text.InputFilter.LengthFilter(6) });
        input.setSingleLine(true);
        layout.addView(input);

        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.dialog_setup_pin_title))
                .setView(container)
                .setPositiveButton(getString(R.string.btn_continue), (dialog, which) -> {
                    String pin = input.getText() != null ? input.getText().toString().trim() : "";
                    if (pin.length() == 6) {
                        showConfirmPinDialogForSetup(pin);
                    } else {
                        extra_sound_manager.playError(this);
                        Toast.makeText(this, getString(R.string.toast_pin_invalid_length), Toast.LENGTH_SHORT).show();
                        updatePinUI(); // revert if failed
                    }
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> updatePinUI())
                .setOnCancelListener(dialog -> updatePinUI())
                .show();
    }

    private void showConfirmPinDialogForSetup(String firstPin) {
        TextInputLayout layout = new TextInputLayout(this, null,
                com.google.android.material.R.style.Widget_Material3_TextInputLayout_OutlinedBox);
        layout.setHint(getString(R.string.dialog_confirm_pin));

        android.widget.FrameLayout container = new android.widget.FrameLayout(this);
        android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(60, 20, 60, 10);
        layout.setLayoutParams(params);
        container.addView(layout);

        final TextInputEditText input = new TextInputEditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        input.setFilters(new android.text.InputFilter[] { new android.text.InputFilter.LengthFilter(6) });
        input.setSingleLine(true);
        layout.addView(input);

        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.dialog_confirm_pin))
                .setView(container)
                .setPositiveButton(getString(R.string.save), (dialog, which) -> {
                    String confirmPin = input.getText() != null ? input.getText().toString().trim() : "";
                    if (confirmPin.equals(firstPin)) {
                        prefs.edit().putBoolean("pin_enabled", true).putString("app_pin", confirmPin).apply();
                        extra_sound_manager.playSuccess(this);
                        Toast.makeText(this, getString(R.string.toast_pin_set_success), Toast.LENGTH_SHORT).show();
                        updatePinUI();
                    } else {
                        extra_sound_manager.playError(this);
                        Toast.makeText(this, getString(R.string.toast_pin_mismatch), Toast.LENGTH_SHORT).show();
                        updatePinUI();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> updatePinUI())
                .setOnCancelListener(dialog -> updatePinUI())
                .show();
    }

    private void showConfirmPinDialogForDisable() {
        TextInputLayout layout = new TextInputLayout(this, null,
                com.google.android.material.R.style.Widget_Material3_TextInputLayout_OutlinedBox);
        layout.setHint(getString(R.string.dialog_enter_current_pin));

        android.widget.FrameLayout container = new android.widget.FrameLayout(this);
        android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(60, 20, 60, 10);
        layout.setLayoutParams(params);
        container.addView(layout);

        final TextInputEditText input = new TextInputEditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        input.setFilters(new android.text.InputFilter[] { new android.text.InputFilter.LengthFilter(6) });
        input.setSingleLine(true);
        layout.addView(input);

        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.dialog_remove_pin_title))
                .setMessage(getString(R.string.dialog_remove_pin_message))
                .setView(container)
                .setPositiveButton(getString(R.string.dialog_delete), (dialog, which) -> {
                    String enteredPin = input.getText() != null ? input.getText().toString().trim() : "";
                    String storedPin = prefs.getString("app_pin", "");
                    if (enteredPin.equals(storedPin)) {
                        prefs.edit().putBoolean("pin_enabled", false).remove("app_pin").apply();
                        extra_sound_manager.playSuccess(this);
                        Toast.makeText(this, getString(R.string.toast_pin_removed), Toast.LENGTH_SHORT).show();
                        updatePinUI();
                    } else {
                        extra_sound_manager.playError(this);
                        Toast.makeText(this, getString(R.string.toast_pin_wrong), Toast.LENGTH_SHORT).show();
                        updatePinUI();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> updatePinUI())
                .setOnCancelListener(dialog -> updatePinUI())
                .show();
    }

    private void showChangePinDialog() {
        // Verify Old PIN first
        TextInputLayout layout = new TextInputLayout(this, null,
                com.google.android.material.R.style.Widget_Material3_TextInputLayout_OutlinedBox);
        layout.setHint(getString(R.string.dialog_enter_current_pin));

        android.widget.FrameLayout container = new android.widget.FrameLayout(this);
        android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(60, 20, 60, 10);
        layout.setLayoutParams(params);
        container.addView(layout);

        final TextInputEditText input = new TextInputEditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        input.setFilters(new android.text.InputFilter[] { new android.text.InputFilter.LengthFilter(6) });
        input.setSingleLine(true);
        layout.addView(input);

        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.dialog_change_pin_title))
                .setView(container)
                .setPositiveButton(getString(R.string.btn_continue), (dialog, which) -> {
                    String enteredPin = input.getText() != null ? input.getText().toString().trim() : "";
                    String storedPin = prefs.getString("app_pin", "");
                    if (enteredPin.equals(storedPin)) {
                        // Correct old PIN -> Setup new PIN
                        showSetPinDialog();
                    } else {
                        extra_sound_manager.playError(this);
                        Toast.makeText(this, getString(R.string.toast_pin_wrong), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    private void dialogConfirmDelete(FirebaseUser user, String password) {
        if (password.isEmpty()) {
            extra_sound_manager.playError(this);
            Toast.makeText(this, getString(R.string.toast_enter_password), Toast.LENGTH_SHORT).show();
            return;
        }

        String email = user.getEmail();
        if (email == null) {
            extra_sound_manager.playError(this);
            Toast.makeText(this, getString(R.string.toast_no_email), Toast.LENGTH_SHORT).show();
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        user.reauthenticate(credential)
                .addOnSuccessListener(unused -> {
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                    usersRef.child(user.getUid()).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                user.delete()
                                        .addOnSuccessListener(unused2 -> {
                                            extra_sound_manager.playSuccess(this);
                                            // Xóa UserPrefs
                                            getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().clear().apply();
                                            // Xóa AppSettings (PIN)
                                            prefs.edit().clear().apply();

                                            FirebaseAuth.getInstance().signOut();
                                            Toast.makeText(this, getString(R.string.toast_account_deleted),
                                                    Toast.LENGTH_SHORT)
                                                    .show();

                                            Intent intent = new Intent(this, activities_1_login.class);
                                            intent.setFlags(
                                                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            extra_sound_manager.playError(this);
                                            Toast.makeText(this,
                                                    String.format(getString(R.string.toast_delete_account_error),
                                                            e.getMessage()),
                                                    Toast.LENGTH_LONG).show();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                extra_sound_manager.playError(this);
                                Toast.makeText(this,
                                        String.format(getString(R.string.toast_delete_data_error), e.getMessage()),
                                        Toast.LENGTH_LONG)
                                        .show();
                            });
                })
                .addOnFailureListener(e -> {
                    extra_sound_manager.playError(this);
                    Toast.makeText(this, getString(R.string.toast_session_expired), Toast.LENGTH_LONG)
                            .show();
                });
    }
}