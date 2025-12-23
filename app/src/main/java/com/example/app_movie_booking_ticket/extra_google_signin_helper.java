package com.example.app_movie_booking_ticket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Helper class để xử lý Google Sign-In
 * Hỗ trợ đăng nhập/đăng ký qua tài khoản Google trên máy
 */
public class extra_google_signin_helper {

    private final Context context;
    private final GoogleSignInClient googleSignInClient;
    private final FirebaseAuth firebaseAuth;
    private final DatabaseReference usersRef;

    // Callback interface
    public interface GoogleSignInCallback {
        void onLoginSuccess(FirebaseUser user);

        void onNeedRegistration(String email, String displayName, String photoUrl);

        void onError(String errorMessage);

        void onCancelled();
    }

    public extra_google_signin_helper(Context context) {
        this.context = context;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Cấu hình Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        this.googleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    /**
     * Lấy Intent để khởi động Google Sign-In
     */
    public Intent getSignInIntent() {
        return googleSignInClient.getSignInIntent();
    }

    /**
     * Đăng xuất khỏi Google (dùng khi cần chọn tài khoản khác)
     */
    public void signOut() {
        googleSignInClient.signOut();
        firebaseAuth.signOut();
    }

    /**
     * Xử lý kết quả từ Google Sign-In cho màn hình LOGIN
     * - Nếu email đã có trong database → đăng nhập luôn
     * - Nếu email chưa có → yêu cầu đăng ký
     */
    public void handleSignInResultForLogin(Intent data, GoogleSignInCallback callback) {
        try {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = task.getResult(ApiException.class);

            if (account != null) {
                String email = account.getEmail();
                String displayName = account.getDisplayName();
                String photoUrl = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "";
                String idToken = account.getIdToken();

                // Kiểm tra email đã tồn tại trong database chưa
                checkUserExistsAndLogin(email, idToken, displayName, photoUrl, callback);
            }
        } catch (ApiException e) {
            if (e.getStatusCode() == 12501) {
                // User cancelled the sign-in
                callback.onCancelled();
            } else {
                callback.onError(e.getMessage());
            }
        }
    }

    /**
     * Xử lý kết quả từ Google Sign-In cho màn hình SIGNUP
     * Chỉ lấy thông tin email, không tự động đăng nhập
     */
    public void handleSignInResultForSignup(Intent data, GoogleSignInCallback callback) {
        try {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = task.getResult(ApiException.class);

            if (account != null) {
                String email = account.getEmail();
                String displayName = account.getDisplayName() != null ? account.getDisplayName() : "";
                String photoUrl = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "";

                // Trả về thông tin để điền vào form đăng ký
                callback.onNeedRegistration(email, displayName, photoUrl);

                // Đăng xuất Google để không tự động đăng nhập
                googleSignInClient.signOut();
            }
        } catch (ApiException e) {
            if (e.getStatusCode() == 12501) {
                callback.onCancelled();
            } else {
                callback.onError(e.getMessage());
            }
        }
    }

    /**
     * Kiểm tra user đã tồn tại và thực hiện đăng nhập
     */
    private void checkUserExistsAndLogin(String email, String idToken, String displayName,
            String photoUrl, GoogleSignInCallback callback) {
        // Tìm user theo email trong database
        usersRef.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Email đã tồn tại → đăng nhập với Firebase Auth
                            firebaseAuthWithGoogle(idToken, callback);
                        } else {
                            // Email chưa tồn tại → yêu cầu đăng ký
                            // Đăng xuất Google trước
                            googleSignInClient.signOut();
                            callback.onNeedRegistration(email, displayName, photoUrl);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    /**
     * Xác thực với Firebase sử dụng Google credential
     */
    private void firebaseAuthWithGoogle(String idToken, GoogleSignInCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            callback.onLoginSuccess(user);
                        } else {
                            callback.onError("User is null after authentication");
                        }
                    } else {
                        Exception e = task.getException();
                        callback.onError(e != null ? e.getMessage() : "Unknown error");
                    }
                });
    }

    /**
     * Liên kết tài khoản Google với tài khoản Firebase hiện có (cho đăng ký)
     * Sử dụng khi user đăng ký bằng form nhưng chọn email từ Google
     */
    public void linkGoogleAccountWithExistingUser(String idToken, OnCompleteListener<AuthResult> listener) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            currentUser.linkWithCredential(credential).addOnCompleteListener(listener);
        }
    }

    /**
     * Đăng nhập trực tiếp với Google credential (không cần kiểm tra database)
     * Dùng cho trường hợp đã biết user tồn tại
     */
    public void signInWithGoogleCredential(String idToken, GoogleSignInCallback callback) {
        firebaseAuthWithGoogle(idToken, callback);
    }

    /**
     * Đăng xuất Google trước rồi mở danh sách chọn tài khoản
     * Sử dụng signOut() để xóa cache tài khoản và luôn hiển thị Account Picker
     * 
     * @param launcher ActivityResultLauncher để khởi động Sign-In Intent
     */
    public void revokeAccessAndSignIn(androidx.activity.result.ActivityResultLauncher<Intent> launcher) {
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            // Sau khi đăng xuất xong, mở danh sách chọn tài khoản
            launcher.launch(googleSignInClient.getSignInIntent());
        });
    }
}
