package com.example.app_movie_booking_ticket;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.app_movie_booking_ticket.adapter.SliderAdapter;
import com.example.app_movie_booking_ticket.databinding.LayoutsFragmentsHomeBinding;
import com.example.app_movie_booking_ticket.model.SliderItems;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.app_movie_booking_ticket.adapter.TopMovieAdapter;
import com.example.app_movie_booking_ticket.model.extra_Movie;



public class fragments_home extends Fragment {

    private LayoutsFragmentsHomeBinding binding;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private DatabaseReference userRef;

    private final Handler sliderHandler = new Handler();

    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            binding.viewPager2.setCurrentItem(binding.viewPager2.getCurrentItem() + 1);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = LayoutsFragmentsHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = FirebaseDatabase.getInstance();
        initBanner();
        auth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");

// gọi hàm load user info
        loadUserInfo();
        loadTopMovies();


    }

    private void initBanner() {
        DatabaseReference myRef = database.getReference("Banners");
        binding.progressBarSlider.setVisibility(View.VISIBLE);



        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding == null || getView() == null) return;
                List<SliderItems> lists = new ArrayList<>();
                for (DataSnapshot i : snapshot.getChildren()) {
                    SliderItems item = i.getValue(SliderItems.class);
                    if (item != null) {
                        lists.add(item);
                    }
                }
                binding.progressBarSlider.setVisibility(View.GONE);
                setupBanners(lists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // handle error if needed
            }
        });
    }
    private void loadUserInfo() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) return;

        String uid = currentUser.getUid();

        userRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;

                String fullName = snapshot.child("fullName").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String avatarUrl = snapshot.child("avatarUrl").getValue(String.class);

                // Hiển thị tên và email
                binding.tvFullName.setText(fullName != null ? fullName : "Người dùng");
                binding.tvEmail.setText(email != null ? email : "");

                // Hiển thị avatar (nếu không có thì dùng mặc định)
                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    Glide.with(requireContext())
                            .load(avatarUrl)
                            .placeholder(R.drawable.ic_default_avatar) // ảnh tạm khi đang tải
                            .error(R.drawable.ic_default_avatar)        // ảnh fallback khi lỗi
                            .into(binding.imgAvatar);
                } else {
                    binding.imgAvatar.setImageResource(R.drawable.ic_default_avatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Lỗi tải thông tin user", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadTopMovies() {
        DatabaseReference movieRef = FirebaseDatabase.getInstance().getReference("Items");

        List<extra_Movie> movieList = new ArrayList<>();
        TopMovieAdapter adapter = new TopMovieAdapter(requireContext(), movieList);
        binding.recyclerTopMovie.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerTopMovie.setAdapter(adapter);

        movieRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                movieList.clear();
                for (DataSnapshot itemSnap : snapshot.getChildren()) {
                    extra_Movie movie = itemSnap.getValue(extra_Movie.class);
                    if (movie != null) {
                        movieList.add(movie);
                    }
                }

                // sắp xếp theo IMDb giảm dần (Top Movie)
                movieList.sort((m1, m2) -> Double.compare(m2.getImdb(), m1.getImdb()));

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Lỗi tải Top Movies!", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void setupBanners(List<SliderItems> lists) {
        binding.viewPager2.setAdapter(new SliderAdapter(lists, binding.viewPager2));
        binding.viewPager2.setClipToPadding(false);
        binding.viewPager2.setClipChildren(false);
        binding.viewPager2.setOffscreenPageLimit(3);
        binding.viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        transformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });

        binding.viewPager2.setPageTransformer(transformer);
        binding.viewPager2.setCurrentItem(1);

        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sliderHandler.removeCallbacks(sliderRunnable);
        binding = null;
    }
}
