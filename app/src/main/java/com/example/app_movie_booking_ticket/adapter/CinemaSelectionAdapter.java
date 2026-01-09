package com.example.app_movie_booking_ticket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_movie_booking_ticket.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Adapter for displaying cinemas in selection screen
 */
public class CinemaSelectionAdapter extends RecyclerView.Adapter<CinemaSelectionAdapter.CinemaViewHolder> {

    public interface OnCinemaSelectedListener {
        void onCinemaSelected(String cinemaId, String cinemaName, String address, int pricePerSeat);
    }

    private Context context;
    private List<Map<String, Object>> cinemaList;
    private OnCinemaSelectedListener listener;
    private int selectedPosition = -1;

    public CinemaSelectionAdapter(Context context, List<Map<String, Object>> cinemaList,
            OnCinemaSelectedListener listener) {
        this.context = context;
        this.cinemaList = cinemaList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CinemaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cinema_selection, parent, false);
        return new CinemaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CinemaViewHolder holder, int position) {
        Map<String, Object> cinema = cinemaList.get(position);

        String cinemaId = (String) cinema.get("id");
        String name = (String) cinema.get("name");
        String address = (String) cinema.get("address");
        int price = cinema.get("pricePerSeat") != null ? ((Long) cinema.get("pricePerSeat")).intValue() : 0;

        holder.tvCinemaName.setText(name != null ? name : "Unknown");
        holder.tvAddress.setText(address != null ? address : "");

        // Format price
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        holder.tvPrice.setText(context.getString(R.string.price_format_vnd, formatter.format(price)));

        // Handle selection state
        boolean isSelected = position == selectedPosition;
        holder.btnSelect.setSelected(isSelected);
        holder.btnSelect
                .setText(isSelected ? context.getString(R.string.selected) : context.getString(R.string.select));

        holder.btnSelect.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            if (oldPosition != -1) {
                notifyItemChanged(oldPosition);
            }
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onCinemaSelected(cinemaId, name, address, price);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cinemaList != null ? cinemaList.size() : 0;
    }

    public void updateData(List<Map<String, Object>> newList) {
        this.cinemaList = newList;
        this.selectedPosition = -1;
        notifyDataSetChanged();
    }

    public void clearSelection() {
        int oldPosition = selectedPosition;
        selectedPosition = -1;
        if (oldPosition != -1) {
            notifyItemChanged(oldPosition);
        }
    }

    public void setPreSelectedCinema(String preSelectedId) {
        if (preSelectedId == null || cinemaList == null)
            return;

        for (int i = 0; i < cinemaList.size(); i++) {
            String id = (String) cinemaList.get(i).get("id");
            if (preSelectedId.equals(id)) {
                selectedPosition = i; // Cập nhật vị trí được chọn trong Adapter
                notifyDataSetChanged(); // Làm mới giao diện để hiển thị trạng thái "Đã chọn"
                break;
            }
        }
    }

    static class CinemaViewHolder extends RecyclerView.ViewHolder {
        TextView tvCinemaName, tvAddress, tvPrice;
        Button btnSelect;

        public CinemaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCinemaName = itemView.findViewById(R.id.tvCinemaName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnSelect = itemView.findViewById(R.id.btnSelect);
        }
    }
}
