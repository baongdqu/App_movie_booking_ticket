package com.example.app_movie_booking_ticket;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.app_movie_booking_ticket.adapter.NotificationAdapter;
import com.example.app_movie_booking_ticket.model.AppNotification;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment Thông báo (Notifications)
 * Hiển thị danh sách thông báo từ hệ thống (hoàn vé, khuyến mãi, v.v.).
 * Hỗ trợ xoá thông báo (vuốt hoặc xoá tất cả).
 */
public class fragments_notifications extends Fragment {

    private RecyclerView rvNotifications;
    private MaterialButton btnClearAll;
    private LinearLayout emptyState;
    private SwipeRefreshLayout swipeRefresh;

    private NotificationAdapter adapter;
    private final List<AppNotification> list = new ArrayList<>();

    private DatabaseReference ref;

    /**
     * Tạo View cho fragment.
     * Khởi tạo RecyclerView, Adapter và logic xoá thông báo.
     */
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.layouts_fragments_notifications,
                container,
                false);

        // Map views
        rvNotifications = view.findViewById(R.id.rvNotifications);
        btnClearAll = view.findViewById(R.id.btnClearAll);
        emptyState = view.findViewById(R.id.emptyState);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));

        // 🔥 Adapter có callback
        adapter = new NotificationAdapter(
                requireContext(),
                list,
                this::handleNotificationClick);
        rvNotifications.setAdapter(adapter);

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            showEmptyState(true);
            return view;
        }

        ref = FirebaseDatabase.getInstance()
                .getReference("notifications")
                .child(uid);

        loadNotifications();
        setupSwipeToDelete();
        setupSwipeRefresh();

        // Clear all với confirmation dialog
        btnClearAll.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(requireContext());
            if (list.isEmpty()) {
                Toast.makeText(getContext(), R.string.no_notifications, Toast.LENGTH_SHORT).show();
                return;
            }
            showClearAllConfirmation();
        });

        return view;
    }

    /**
     * Setup SwipeRefreshLayout
     */
    private void setupSwipeRefresh() {
        if (swipeRefresh != null) {
            swipeRefresh.setColorSchemeResources(
                    R.color.netflix_red,
                    R.color.netflix_green,
                    R.color.accent_blue);
            swipeRefresh.setOnRefreshListener(this::loadNotifications);
        }
    }

    /**
     * Show/hide empty state
     */
    private void showEmptyState(boolean show) {
        if (emptyState != null) {
            emptyState.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (rvNotifications != null) {
            rvNotifications.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Show confirmation dialog before clearing all notifications
     */
    private void showClearAllConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.clear_all)
                .setMessage(R.string.dialog_confirm_delete_all_notifications_message)
                .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {
                    ref.removeValue();
                    list.clear();
                    adapter.notifyDataSetChanged();
                    showEmptyState(true);
                    Toast.makeText(getContext(), R.string.all_notifications_cleared, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    // =====================================================
    // 🔥 LOAD NOTIFICATIONS
    // =====================================================
    private void loadNotifications() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();

                for (DataSnapshot c : snapshot.getChildren()) {
                    AppNotification n = c.getValue(AppNotification.class);
                    if (n != null) {
                        n.setId(c.getKey());
                        list.add(n);
                    }
                }

                // 🔥 sort mới → cũ
                list.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));

                adapter.notifyDataSetChanged();

                // Update empty state
                showEmptyState(list.isEmpty());

                // Stop refreshing
                if (swipeRefresh != null) {
                    swipeRefresh.setRefreshing(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (swipeRefresh != null) {
                    swipeRefresh.setRefreshing(false);
                }
            }
        });
    }

    // =====================================================
    // 🔥 CLICK NOTIFICATION
    // =====================================================
    private void handleNotificationClick(AppNotification n) {
        extra_sound_manager.playUiClick(requireContext());

        // Đánh dấu đã đọc
        ref.child(n.getId()).child("read").setValue(true);

        String type = n.getType();

        // PROFILE → đã xử lý trong adapter
        if ("PROFILE".equals(type))
            return;

        // REFUND → mở danh sách vé đã hoàn
        if ("REFUND".equals(type)) {

            Bundle bundle = new Bundle();
            bundle.putString("filter", "REFUNDED");

            fragments_mail fragment = new fragments_mail();
            fragment.setArguments(bundle);

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right)
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
        // LOGIN / loại khác → chỉ đánh dấu đã đọc
    }

    // =====================================================
    // 🔥 SWIPE TO DELETE
    // =====================================================
    private void setupSwipeToDelete() {

        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(
                        0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            int direction) {

                        int position = viewHolder.getAdapterPosition();
                        AppNotification n = list.get(position);

                        // Xoá Firebase
                        ref.child(n.getId()).removeValue();

                        // Xoá local
                        list.remove(position);
                        adapter.notifyItemRemoved(position);
                        extra_sound_manager.playUiClick(requireContext());

                        // Show toast
                        Toast.makeText(getContext(), R.string.notification_deleted, Toast.LENGTH_SHORT).show();

                        // Update empty state
                        if (list.isEmpty()) {
                            showEmptyState(true);
                        }
                    }
                });

        helper.attachToRecyclerView(rvNotifications);
    }
}
