// link firebase đã tạo:
// https://console.firebase.google.com/u/0/project/app-movie-booking-ticket/database/app-movie-booking-ticket-default-rtdb/data

// và dưới đây là các đoạn code ví dụ mà thôi

package com.example.app_movie_booking_ticket;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Lớp firebase_helper chứa các lệnh cơ bản để thao tác với Firebase Realtime Database.
 * (Mẫu minh họa, chưa cần chạy)
 */
public class extra_firebase_helper {

    private final DatabaseReference rootRef;

    //Hàm khởi tạo
    public extra_firebase_helper() {
        //Lấy instance của Realtime Database
        //Khai báo tham chiếu đến Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Lấy reference gốc (root node)
        rootRef = database.getReference();

        // Bạn có thể dùng rootRef.child("Users") để trỏ tới node cụ thể
    }

    //  Ghi dữ liệu (ghi đè node)
    public void writeData(String path, Object value) {
        rootRef.child(path).setValue(value);
    }

    //  Thêm dữ liệu (tạo node tự động bằng push ID)
    public void addData(String path, Object value) {
        rootRef.child(path).push().setValue(value);
    }

    //  Đọc dữ liệu 1 lần (chỉ lấy snapshot hiện tại)
    public void readDataOnce(String path) {
        rootRef.child(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object data = snapshot.getValue();
                System.out.println("Data (once) = " + data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Error reading data: " + error.getMessage());
            }
        });
    }

    //  Lắng nghe dữ liệu liên tục (real-time listener)
    public void listenData(String path) {
        rootRef.child(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object data = snapshot.getValue();
                System.out.println("Data changed: " + data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Listener cancelled: " + error.getMessage());
            }
        });
    }

    //  Xóa dữ liệu
    public void deleteData(String path) {
        rootRef.child(path).removeValue();
    }

    //  Cập nhật dữ liệu (ghi đè một phần)
    public void updateData(String path, Object newValue) {
        rootRef.child(path).setValue(newValue);
    }
}

/*
  Gợi ý sử dụng:
  <p>
  Sau này, khi có Activity như MainActivity.java, bạn có thể dùng:
  <p>
  firebase_helper db = new firebase_helper();
  <p>
  // Ghi dữ liệu
  db.writeData("Users/user1/name", "Worm Wood");
  <p>
  // Thêm mới user
  db.addData("Users", new User("Alice", 22));
  <p>
  // Đọc 1 lần
  db.readDataOnce("Users/user1");
  <p>
  // Theo dõi thay đổi realtime
  db.listenData("Users");
  <p>
  // Xóa dữ liệu
  db.deleteData("Users/user1");
 */

// và ta sẽ khởi tạo cấu trúc của database chung 1 cách thủ công
// và cũng sẽ quy định sơ đồ database của 1 user
// còn các chức năng thêm user, xóa user, cũng như các trường dữ liệu ở bên trong user
// sẽ được thực hiện qua application này