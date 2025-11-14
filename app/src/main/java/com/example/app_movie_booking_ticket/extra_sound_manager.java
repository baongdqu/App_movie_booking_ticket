package com.example.app_movie_booking_ticket;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.SoundPool;

public class extra_sound_manager {

    private static SoundPool soundPool;
    private static boolean isLoaded = false;

    private static int soundUiClick;
    private static int soundMenuClick;
    private static int soundToggle;
    private static int soundError;
    private static int soundSuccess;
    private static int soundOpening;
    private static int soundNotification;

    // ================================
    //  CHECK SETTINGS BẬT/TẮT ÂM THANH
    // ================================
    public static boolean isSoundEnabled(Context ctx) {
        return ctx.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
                .getBoolean("sound_enabled", true);
    }

    private static void ensureInit(Context context) {
        if (soundPool != null && isLoaded) return;

        if (soundPool == null) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(6)
                    .setAudioAttributes(audioAttributes)
                    .build();
        }

        soundUiClick       = soundPool.load(context, R.raw.sound_ui_click, 1);
        soundMenuClick     = soundPool.load(context, R.raw.sound_menu_click, 1);
        soundToggle        = soundPool.load(context, R.raw.sound_toggle_onoff, 1);
        soundError         = soundPool.load(context, R.raw.sound_error_buzz, 1);
        soundSuccess       = soundPool.load(context, R.raw.sound_success_chime, 1);
        soundOpening       = soundPool.load(context, R.raw.sound_opening_app, 1);
        soundNotification  = soundPool.load(context, R.raw.sound_notification_appear, 1);

        isLoaded = true;
    }

    // ================================
    //  HÀM PLAY CÓ KIỂM TRA ÂM THANH
    // ================================
    private static void play(Context context, int soundId) {
        if (context == null) return;

        // Nếu người dùng tắt âm thanh → Không phát
        if (!isSoundEnabled(context)) return;

        ensureInit(context.getApplicationContext());

        if (soundPool != null && soundId != 0) {
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f);
        }
    }

    // ================================
    //  CÁC HÀM PUBLIC PLAY
    // ================================
    public static void playUiClick(Context context) {
        play(context, soundUiClick);
    }

    public static void playMenuClick(Context context) {
        play(context, soundMenuClick);
    }

    public static void playToggle(Context context) {
        play(context, soundToggle);
    }

    public static void playError(Context context) {
        play(context, soundError);
    }

    public static void playSuccess(Context context) {
        play(context, soundSuccess);
    }

    public static void playOpening(Context context) {
        play(context, soundOpening);
    }

    public static void playNotification(Context context) {
        play(context, soundNotification);
    }

    public static void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
            isLoaded = false;
        }
    }
}
