package com.example.app_movie_booking_ticket;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.app_movie_booking_ticket.adapter.SliderAdapter;
import com.example.app_movie_booking_ticket.adapter.TopMovieAdapter;
import com.example.app_movie_booking_ticket.databinding.LayoutsFragmentsHomeBinding;
import com.example.app_movie_booking_ticket.model.Movie;
import com.example.app_movie_booking_ticket.model.SliderItems;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment Trang chủ (Home)
 * Hiển thị các danh sách phim nổi bật, phim sắp chiếu, banner quảng cáo.
 * Cung cấp chức năng tìm kiếm phim và xem thông tin người dùng cơ bản.
 */
public class fragments_home extends Fragment {

    private LayoutsFragmentsHomeBinding binding;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private DatabaseReference userRef;

    // Danh sách phim Top và Adapter
    private List<Movie> movieListTop = new ArrayList<>();
    private TopMovieAdapter topMovieAdapter;

    // Danh sách phim Upcoming và Adapter
    private List<Movie> upcomingMoviesList = new ArrayList<>();
    private TopMovieAdapter upcomingAdapter;

    // Danh sách TẤT CẢ phim (Top + Upcoming) cho search và All Movies section
    private List<Movie> allMoviesList = new ArrayList<>();
    private TopMovieAdapter searchAdapter;
    private TopMovieAdapter allMoviesAdapter;

    private final Handler sliderHandler = new Handler();

    private final Runnable sliderRunnable = () -> binding.viewPager2
            .setCurrentItem(binding.viewPager2.getCurrentItem() + 1);

    /**
     * Tạo View cho fragment.
     */
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
        auth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");

        initBanner();
        loadUserInfo();
        loadTopMovies();
        loadUpcomingMovies();
        loadAllMovies();

        // =========================
        // SETUP SEARCH
        // =========================
        ImageView btnSearch = binding.imgSearch;
        TextInputLayout searchBox = binding.searchBoxLayout;
        TextInputEditText inputSearch = binding.inputSearch;

        // Mở / đóng search box
        btnSearch.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(requireContext());
            if (searchBox.getVisibility() == View.GONE) {
                searchBox.setVisibility(View.VISIBLE);
                searchBox.setAlpha(0f);
                searchBox.animate().alpha(1f).setDuration(200).start();
            } else {
                searchBox.animate().alpha(0f).setDuration(150).withEndAction(() -> {
                    searchBox.setVisibility(View.GONE);
                    inputSearch.setText("");
                    // Reset UI về trạng thái ban đầu
                    binding.recyclerSearchResults.setVisibility(View.GONE);
                    binding.tvNoResults.setVisibility(View.GONE);
                    binding.recyclerTopMovie.setVisibility(View.VISIBLE);
                    binding.recyclerUpcomingMovies.setVisibility(View.VISIBLE);
                }).start();
            }
        });

        // TextWatcher lọc phim theo tên
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMovies(s.toString());
            }
        });

        // =========================
        // XEM TẤT CẢ PHIM
        // =========================
        binding.tvViewAll.setOnClickListener(v -> {
            // Âm thanh click
            extra_sound_manager.playUiClick(requireContext());
            startActivity(new Intent(requireContext(), parthome_AllMoviesActivity.class));
        });

        binding.tvViewAllUpcoming.setOnClickListener(v -> {
            // Âm thanh click
            extra_sound_manager.playUiClick(requireContext());
            startActivity(new Intent(requireContext(), parthome_AllUpcomingActivity.class));
        });

        // =========================
        // TRANG NGƯỜI DÙNG
        // =========================
        binding.userInfoLayout.setOnClickListener(v -> {
            if (getActivity() instanceof activities_2_a_menu_manage_fragments) {
                ((activities_2_a_menu_manage_fragments) getActivity())
                        .selectBottomNavItem(R.id.nav_user);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left,
                                R.anim.slide_in_left,
                                R.anim.slide_out_right)
                        .replace(R.id.container, new fragments_user())
                        .addToBackStack(null)
                        .commit();
            }
        });

        binding.imgAvatar.setOnClickListener(v -> binding.userInfoLayout.performClick());
        binding.tvFullName.setOnClickListener(v -> binding.userInfoLayout.performClick());

        // =========================
        // CHATBOT FAB
        // =========================
        binding.fabChatbot.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(requireContext());
            startActivity(new Intent(requireContext(), activities_2_chatbot.class));
        });
    }

    // =====================================================
    // LOAD TOP MOVIES VÀ KẾT HỢP SEARCH
    // =====================================================
    private void loadTopMovies() {
        DatabaseReference movieRef = FirebaseDatabase.getInstance().getReference("Movies");

        topMovieAdapter = new TopMovieAdapter(requireContext(), movieListTop);
        binding.recyclerTopMovie.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerTopMovie.setAdapter(topMovieAdapter);

        movieRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                movieListTop.clear();
                upcomingMoviesList.clear();

                for (DataSnapshot itemSnap : snapshot.getChildren()) {
                    Movie movie = itemSnap.getValue(Movie.class);
                    if (movie != null) {
                        if (movie.isUpcomingMovie()) {
                            upcomingMoviesList.add(movie);
                        } else if (movie.isTrendingMovie()) {
                            movieListTop.add(movie);
                        }
                        // Movies with isUpcoming = null will only be in allMoviesList
                    }
                }

                // Sắp xếp theo IMDb giảm dần
                movieListTop.sort((m1, m2) -> Double.compare(m2.getImdb(), m1.getImdb()));
                topMovieAdapter.updateList(movieListTop);

                // Sắp xếp upcoming theo năm
                upcomingMoviesList.sort((m1, m2) -> Integer.compare(m2.getYear(), m1.getYear()));
                if (upcomingAdapter != null) {
                    upcomingAdapter.notifyDataSetChanged();
                }

                // Cập nhật allMoviesList
                updateAllMoviesList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // =====================================================
    // LOAD UPCOMING MOVIES - Setup adapter only
    // =====================================================
    private void loadUpcomingMovies() {
        upcomingAdapter = new TopMovieAdapter(requireContext(), upcomingMoviesList);
        binding.recyclerUpcomingMovies.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerUpcomingMovies.setAdapter(upcomingAdapter);
        // Data is loaded from loadTopMovies since they share the same "Movies" node
    }

    // =====================================================
    // LOAD ALL MOVIES - Setup adapter for all movies section
    // =====================================================
    private void loadAllMovies() {
        allMoviesAdapter = new TopMovieAdapter(requireContext(), allMoviesList);
        binding.recyclerAllMovies.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerAllMovies.setAdapter(allMoviesAdapter);

        // View All button click - opens AllMoviesFullActivity showing ALL movies
        binding.tvViewAllMovies.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(requireContext());
            startActivity(new Intent(requireContext(), parthome_AllMoviesFullActivity.class));
        });
    }

    // =====================================================
    // FILTER PHIM THEO TÊN - TÌM TRONG TẤT CẢ PHIM
    // =====================================================
    private void filterMovies(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            // Không có từ khóa -> hiển thị UI gốc
            binding.recyclerSearchResults.setVisibility(View.GONE);
            binding.tvNoResults.setVisibility(View.GONE);
            binding.recyclerTopMovie.setVisibility(View.VISIBLE);
            binding.recyclerUpcomingMovies.setVisibility(View.VISIBLE);
            return;
        }

        // Lọc phim từ allMoviesList
        List<Movie> filteredList = new ArrayList<>();
        for (Movie movie : allMoviesList) {
            if (movie.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(movie);
            }
        }

        // Hiển thị kết quả
        if (filteredList.isEmpty()) {
            // Không tìm thấy kết quả
            binding.recyclerSearchResults.setVisibility(View.GONE);
            binding.tvNoResults.setVisibility(View.VISIBLE);
            binding.recyclerTopMovie.setVisibility(View.GONE);
            binding.recyclerUpcomingMovies.setVisibility(View.GONE);
        } else {
            // Có kết quả -> hiển thị RecyclerView search
            binding.tvNoResults.setVisibility(View.GONE);
            binding.recyclerTopMovie.setVisibility(View.GONE);
            binding.recyclerUpcomingMovies.setVisibility(View.GONE);
            binding.recyclerSearchResults.setVisibility(View.VISIBLE);

            // Khởi tạo adapter nếu chưa có
            if (searchAdapter == null) {
                searchAdapter = new TopMovieAdapter(requireContext(), filteredList);
                binding.recyclerSearchResults.setLayoutManager(
                        new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
                binding.recyclerSearchResults.setAdapter(searchAdapter);
            } else {
                searchAdapter.updateList(filteredList);
            }
        }
    }

    // =====================================================
    // CẬP NHẬT DANH SÁCH TẤT CẢ PHIM
    // =====================================================
    private void updateAllMoviesList() {
        allMoviesList.clear();
        allMoviesList.addAll(movieListTop);
        allMoviesList.addAll(upcomingMoviesList);

        // Notify adapter for All Movies section
        if (allMoviesAdapter != null) {
            allMoviesAdapter.notifyDataSetChanged();
        }
    }

    // =====================================================
    // BANNER
    // =====================================================
    private void initBanner() {
        DatabaseReference myRef = database.getReference("Banners");
        binding.progressBarSlider.setVisibility(View.VISIBLE);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding == null || getView() == null)
                    return;

                List<SliderItems> lists = new ArrayList<>();
                for (DataSnapshot i : snapshot.getChildren()) {
                    SliderItems item = i.getValue(SliderItems.class);
                    if (item != null)
                        lists.add(item);
                }

                binding.progressBarSlider.setVisibility(View.GONE);
                setupBanners(lists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // =====================================================
    // USER INFO
    // =====================================================
    private void loadUserInfo() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null)
            return;

        String uid = currentUser.getUid();

        userRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding == null)
                    return;
                if (!snapshot.exists())
                    return;

                String fullName = snapshot.child("fullName").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String avatarUrl = snapshot.child("avatarUrl").getValue(String.class);
                Long balance = snapshot.child("balance").getValue(Long.class);
                if (balance == null)
                    balance = 0L;

                binding.tvFullName.setText(fullName != null ? fullName : getString(R.string.user_name));
                binding.tvEmail.setText(email != null ? email : "");
                binding.tvBalance.setText(
                        "Số dư: " + formatMoney(balance) + " đ");

                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    Glide.with(requireContext())
                            .load(avatarUrl)
                            .placeholder(R.drawable.ic_default_avatar)
                            .error(R.drawable.ic_default_avatar)
                            .into(binding.imgAvatar);
                } else {
                    binding.imgAvatar.setImageResource(R.drawable.ic_default_avatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), getString(R.string.toast_load_user_error), Toast.LENGTH_SHORT).show();
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

    private String formatMoney(long amount) {
        return String.format("%,d", amount).replace(',', '.');
    }

}
