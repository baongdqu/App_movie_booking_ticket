package com.example.app_movie_booking_ticket;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.net.Uri;
import android.util.Log;

import com.google.android.exoplayer2.ExoPlayer; // NHẬP KHẨU EXOPLAYER
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;

public class extra_sound_manager {

    private static SoundPool soundPool;
    // Sử dụng ExoPlayer cho nhạc nền/nhạc mở đầu dài
    private static ExoPlayer exoPlayerLogin; // KHAI BÁO EXOPLAYER

    // Đếm số lượng âm thanh đã tải thành công (Chỉ tính SoundPool)
    private static int loadedCount = 0;
    // Tổng số lượng âm thanh cần tải (Chỉ tính SoundPool)
    private static final int TOTAL_SOUNDS = 7; // Dùng 7 âm thanh cho SoundPool
    private static boolean isInitCompleted = false;

    private static int soundUiClick;
    private static int soundMenuClick;
    private static int soundToggle;
    private static int soundError;
    private static int soundSuccess;
    private static int soundOpening;
    private static int soundNotification;
    // Đã loại bỏ soundLogin vì nó dùng ExoPlayer/MediaPlayer

    // ================================
    //  CHECK SETTINGS BẬT/TẮT ÂM THANH
    // ================================
    public static boolean isSoundEnabled(Context ctx) {
        return ctx.getApplicationContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
                .getBoolean("sound_enabled", true);
    }

    private static void ensureInit(Context context) {
        if (isInitCompleted) return;

        Log.d("SoundManager", "Initializing SoundPool and ExoPlayer...");
        Context appContext = context.getApplicationContext();

        // ======================= KHỞI TẠO SOUNDPOOL =======================
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(6)
                .setAudioAttributes(audioAttributes)
                .build();

        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            if (status == 0) {
                loadedCount++;
            }
            if (loadedCount == TOTAL_SOUNDS) {
                isInitCompleted = true;
                Log.d("SoundManager", "All SoundPool sounds initialized successfully.");
            }
        });

        loadedCount = 0;
        soundUiClick       = soundPool.load(appContext, R.raw.sound_ui_click, 1);
        soundMenuClick     = soundPool.load(appContext, R.raw.sound_menu_click, 1);
        soundToggle        = soundPool.load(appContext, R.raw.sound_toggle_onoff, 1);
        soundError         = soundPool.load(appContext, R.raw.sound_error_buzz, 1);
        soundSuccess       = soundPool.load(appContext, R.raw.sound_success_chime, 1);
        soundOpening       = soundPool.load(appContext, R.raw.sound_opening_app, 1);
        soundNotification  = soundPool.load(appContext, R.raw.sound_notification_appear, 1);

        // ======================= KHỞI TẠO EXOPLAYER =======================
        try {
            // Tên file trong thư mục raw
            Uri uri = Uri.parse("android.resource://" + appContext.getPackageName() + "/" + R.raw.sound_opening_tieng_hop_nhac_keu_voi_am_thanh_lang_man);
            MediaItem mediaItem = MediaItem.fromUri(uri);

            exoPlayerLogin = new ExoPlayer.Builder(appContext).build();
            exoPlayerLogin.setMediaItem(mediaItem);
            exoPlayerLogin.prepare();
            exoPlayerLogin.setRepeatMode(Player.REPEAT_MODE_OFF); // Phát 1 lần
            exoPlayerLogin.setVolume(1.0f); // Đặt âm lượng tối đa (1.0f)

        } catch (Exception e) {
            Log.e("SoundManager", "Error initializing ExoPlayer for Login sound: " + e.getMessage());
        }
    }

    // ================================
    //  HÀM PLAY CÓ KIỂM TRA ÂM THANH (Dùng cho SoundPool)
    // ================================
    private static void play(Context context, int soundId) {
        if (context == null) return;
        ensureInit(context.getApplicationContext());

        if (!isSoundEnabled(context)) return;

        // **ĐÃ SỬA LỖI:** Loại bỏ điều kiện && isInitCompleted để cho phép phát tức thì
        if (soundPool != null && soundId != 0) {
            Log.d("SoundManager", "Attempting to play sound ID: " + soundId);
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f);
        }
    }

    // ================================
    //  CÁC HÀM PUBLIC PLAY
    // ================================
    public static void playUiClick(Context context) { play(context, soundUiClick); }
    public static void playMenuClick(Context context) { play(context, soundMenuClick); }
    public static void playToggle(Context context) { play(context, soundToggle); }
    public static void playError(Context context) { play(context, soundError); }
    public static void playSuccess(Context context) { play(context, soundSuccess); }
    public static void playOpening(Context context) { play(context, soundOpening); } // Sound mở màn hình loading
    public static void playNotification(Context context) { play(context, soundNotification); }

    // Dùng ExoPlayer để phát hết file âm thanh dài
    public static void playOpeningApp(Context context) {
        if (context == null) return;
        ensureInit(context.getApplicationContext());

        if (!isSoundEnabled(context)) return;

        if (exoPlayerLogin != null) {
            try {
                // Đảm bảo phát từ đầu
                exoPlayerLogin.seekTo(0);
                exoPlayerLogin.play();
                Log.d("SoundManager", "Playing Login sound via ExoPlayer.");
            } catch (Exception e) {
                Log.e("SoundManager", "Error starting ExoPlayer: " + e.getMessage());
            }
        }
    }

    public static void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        if (exoPlayerLogin != null) {
            exoPlayerLogin.release(); // Giải phóng ExoPlayer
            exoPlayerLogin = null;
        }
        isInitCompleted = false;
        loadedCount = 0;
        Log.d("SoundManager", "SoundPool and ExoPlayer released.");
    }
}