package com.example.app_movie_booking_ticket;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_movie_booking_ticket.adapter.NotificationAdapter;
import com.example.app_movie_booking_ticket.model.AppNotification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class fragments_notifications extends Fragment {

    private RecyclerView rv;
    private TextView btnClear;
    private NotificationAdapter adapter;
    private List<AppNotification> list;
    private DatabaseReference ref;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.layouts_fragments_notifications,
                container,
                false
        );

        rv = view.findViewById(R.id.rvNotifications);
        btnClear = view.findViewById(R.id.btnClearAll);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<>();
        adapter = new NotificationAdapter(getContext(), list);
        rv.setAdapter(adapter);

        String uid = FirebaseAuth.getInstance().getUid();
        ref = FirebaseDatabase.getInstance()
                .getReference("notifications")
                .child(uid);

        loadData();

        btnClear.setOnClickListener(v -> {
            ref.removeValue();
            list.clear();
            adapter.notifyDataSetChanged();
        });

        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(
                        0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(
                            @NonNull RecyclerView rv,
                            @NonNull RecyclerView.ViewHolder vh,
                            @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(
                            @NonNull RecyclerView.ViewHolder vh,
                            int dir) {
                        int pos = vh.getAdapterPosition();
                        AppNotification n = list.get(pos);
                        ref.child(n.getId()).removeValue();
                        list.remove(pos);
                        adapter.notifyItemRemoved(pos);
                    }
                });

        helper.attachToRecyclerView(rv);

        return view;
    }

    private void loadData() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                list.clear();
                for (DataSnapshot c : snap.getChildren()) {
                    AppNotification n =
                            c.getValue(AppNotification.class);
                    if (n != null) {
                        n.setId(c.getKey());
                        list.add(n);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }
}
