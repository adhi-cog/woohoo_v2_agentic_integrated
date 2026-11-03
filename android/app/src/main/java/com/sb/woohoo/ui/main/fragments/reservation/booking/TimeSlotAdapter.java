package com.sb.woohoo.ui.main.fragments.reservation.booking;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.ItemTimeSlotBinding;
import com.sb.woohoo.ui.main.fragments.reservation.booking.pojo.TimeSlot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.ViewHolder> {

    private ReservationFragment reservationFragment;
    private ArrayList<TimeSlot> slots = new ArrayList<>();
    private ArrayList<TimeSlot> allSlots = new ArrayList<>();
    private int oldPosition = 0;
    private int LOAD_MORE_THRESHOLD = 10;
    private boolean isLoadMore = false;

    @Inject
    public TimeSlotAdapter(ReservationFragment reservationFragment) {
        this.reservationFragment = reservationFragment;
    }

    List<TimeSlot> getDateModelList() {
        return slots;
    }

    TimeSlot getDateModel(int ordinal) {
        return slots.get(ordinal);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemTimeSlotBinding binding = ItemTimeSlotBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(slots.get(position), position);
    }

    @Override
    public int getItemCount() {
        return slots.size();
    }

    public void updateList(ArrayList<TimeSlot> slots) {
        this.allSlots.clear();
        this.slots.clear();
        this.allSlots.addAll(slots);
        if (allSlots.size() <= LOAD_MORE_THRESHOLD) {
            isLoadMore = false;
            this.slots.addAll(allSlots);
            notifyDataSetChanged();
            reservationFragment.updateLoadMoreVisibility(isLoadMore);
        } else {
            populateList();
        }
    }

    public void populateList() {
        if (slots.size() < allSlots.size()) {
            int currentSlotsSize = slots.size();
            if (allSlots.size() - currentSlotsSize > LOAD_MORE_THRESHOLD) {
                isLoadMore = true;
                for (int i = currentSlotsSize; i < currentSlotsSize + LOAD_MORE_THRESHOLD; i++) {
                    slots.add(allSlots.get(i));
                }
            } else {
                isLoadMore = false;
                for (int i = 0; i < allSlots.size() - currentSlotsSize; i++) {
                    slots.add(allSlots.get(currentSlotsSize + i));
                }
            }
            notifyDataSetChanged();
            reservationFragment.updateLoadMoreVisibility(isLoadMore);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ItemTimeSlotBinding binding;

        ViewHolder(ItemTimeSlotBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        public void bind(TimeSlot timeSlot, int position) {

            binding.itemSlotTime.setText(timeSlot.getTime());

            if (timeSlot.isSelected())
                binding.slotLayout.setBackground(ContextCompat.getDrawable(reservationFragment.getContext(), R.drawable.gradient_service_items));
            else
                binding.slotLayout.setBackground(ContextCompat.getDrawable(reservationFragment.getContext(), R.drawable.bg_rect_shape));

            /*if(timeSlot.isAvailable()){
                textTime.setTextColor(MethodUtils.getColorFromTheme(reservationFragment.getContext(), R.attr.text_color_primary));
            } else {
                textTime.setTextColor(ContextCompat.getColor(reservationFragment.getContext(), R.color.white_20_opacity));
                llTimeSlot.setBackground(ContextCompat.getDrawable(reservationFragment.getContext(), R.drawable.bg_rect_shape_grey));
            }*/

            binding.slotLayout.setOnClickListener(view -> onTimeSlotItemClicked(timeSlot, position));
        }
    }

    private void onTimeSlotItemClicked(TimeSlot item, int newPosition) {
        //if(item.isAvailable()) {
        if (newPosition == 0 && oldPosition == 0) {
            item.setSelected(true);
            notifyItemChanged(newPosition);
        } else if (newPosition != oldPosition) {
            TimeSlot oldItem = slots.get(oldPosition);
            oldItem.setSelected(false);
            notifyItemChanged(oldPosition);

            item.setSelected(true);
            notifyItemChanged(newPosition);
            oldPosition = newPosition;
        }
        reservationFragment.onTimeSlotItemClicked(item);
        //}
    }
}
