package com.sb.woohoo.ui.main.fragments.reservation.booking;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.custom.CircleImageTransform;
import com.sb.woohoo.databinding.ItemReservationServiceBinding;
import com.sb.woohoo.ui.main.fragments.reservation.booking.pojo.ReservationServicesResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ReservationServicesAdapter extends RecyclerView.Adapter<ReservationServicesAdapter.ViewHolder> {

    private List<ReservationServicesResponse.ReservationService> reservationServicesList = new ArrayList<>(0);
    private ReservationFragment reservationFragment;
    int oldPosition = 0;

    @Inject
    public ReservationServicesAdapter(ReservationFragment reservationFragment) {
        this.reservationFragment = reservationFragment;
    }

    List<ReservationServicesResponse.ReservationService> getReservationServicesList() {
        return reservationServicesList;
    }

    ReservationServicesResponse.ReservationService getReservationService(int ordinal) {
        return reservationServicesList.get(ordinal);
    }

    public void updateList(List<ReservationServicesResponse.ReservationService> newServices) {
        this.reservationServicesList.clear();
        this.reservationServicesList.addAll(newServices);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemReservationServiceBinding binding = ItemReservationServiceBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(reservationServicesList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return reservationServicesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ItemReservationServiceBinding binding;

        ViewHolder(ItemReservationServiceBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        public void bind(ReservationServicesResponse.ReservationService service, int position) {
            binding.itemServiceName.setText(service.getName());
            binding.itemServiceImage.setTag(position);
            Picasso.get()
                    .load(reservationServicesList.get(position).getThumbnailUrl())
                    .transform(new CircleImageTransform())
                    .into(binding.itemServiceImage);

            if(service.isSelected())
                binding.serviceLayout.setBackground(ContextCompat.getDrawable(reservationFragment.getContext(), R.drawable.gradient_service_items));
            else
                binding.serviceLayout.setBackground(ContextCompat.getDrawable(reservationFragment.getContext(), R.drawable.bg_rect_shape));

            binding.serviceLayout.setOnClickListener(view -> onServiceItemClicked(reservationServicesList.get(position), position));
        }
    }

    private void onServiceItemClicked(ReservationServicesResponse.ReservationService item, int newPosition) {
        if (newPosition == 0 && oldPosition == 0) {
            item.setSelected(true);
            notifyItemChanged(newPosition);
        } else if (newPosition != oldPosition) {
            ReservationServicesResponse.ReservationService oldItem = reservationServicesList.get(oldPosition);
            oldItem.setSelected(false);
            notifyItemChanged(oldPosition);

            item.setSelected(true);
            notifyItemChanged(newPosition);
            oldPosition = newPosition;
        }
        reservationFragment.onServiceClicked(item);
    }
}
