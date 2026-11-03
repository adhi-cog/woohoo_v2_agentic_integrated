package com.sb.woohoo.ui.main.fragments.reservation.booking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.ItemDateViewBinding;
import com.sb.woohoo.ui.main.fragments.reservation.booking.pojo.ReservationDateSlot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DatesAdapter extends RecyclerView.Adapter<DatesAdapter.DatesViewHolder> {

    private ArrayList<ReservationDateSlot> dateArray = new ArrayList<>();
    ReservationFragment reservationFragment;
    private int oldPosition = -1;
    private boolean isFromDiningRestaurants = false;

    public DatesAdapter(ReservationFragment reservationFragment, ArrayList<ReservationDateSlot> dateArray) {
        this.dateArray = dateArray;
        this.reservationFragment = reservationFragment;
    }

    @Override
    public DatesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemDateViewBinding binding = ItemDateViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DatesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(DatesViewHolder holder, int position) {
        holder.bind(dateArray.get(position), position);
    }

    public void reset() {
        for(ReservationDateSlot slot: dateArray) {
            slot.setSelected(false);
        }
        oldPosition = -1;
        notifyDataSetChanged();
    }

    public void updateList(ArrayList<ReservationDateSlot> dateArray) {
        this.dateArray = dateArray;
        notifyDataSetChanged();
    }

    public void setFromDiningRestaurants(boolean isFromDiningRestaurants) {
        this.isFromDiningRestaurants = isFromDiningRestaurants;
    }

    @Override
    public int getItemCount() {
        return dateArray.size();
    }

    public class DatesViewHolder extends RecyclerView.ViewHolder {

        public ItemDateViewBinding binding;

        DatesViewHolder(ItemDateViewBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        public void bind(ReservationDateSlot item, int position) {
            SimpleDateFormat dayFormat = new SimpleDateFormat("E", Locale.US);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.US);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(item.getDate());
            String day =  dayFormat.format(calendar.getTime());
            String date = dateFormat.format(calendar.getTime());

            binding.itemServiceDay.setText(day);
            binding.itemServiceDate.setText(date);
            if (isFromDiningRestaurants) {
                binding.itemServiceMonth.setVisibility(View.VISIBLE);
                binding.itemServiceMonth.setText(item.getDateMonth());
            } else {
                binding.itemServiceMonth.setVisibility(View.GONE);
            }

            if(item.isSelected())
                binding.dateLayout.setBackground(ContextCompat.getDrawable(reservationFragment.getContext(), R.drawable.gradient_service_items));
            else
                binding.dateLayout.setBackground(ContextCompat.getDrawable(reservationFragment.getContext(), R.drawable.bg_rect_shape));

            binding.dateLayout.setOnClickListener(view -> onDateItemClicked(item, position));
        }
    }

    private void onDateItemClicked(ReservationDateSlot item, int newPosition) {

        if(oldPosition != -1) {
            ReservationDateSlot oldItem = dateArray.get(oldPosition);
            oldItem.setSelected(false);
            notifyItemChanged(oldPosition);
        }

        if(newPosition != oldPosition) {
            item.setSelected(true);
            notifyItemChanged(newPosition);
        }
        oldPosition = newPosition;
        reservationFragment.onDateItemClicked(item);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
