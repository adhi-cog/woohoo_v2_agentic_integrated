package com.sb.woohoo.ui.main.fragments.notification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.databinding.ItemNotificationBinding;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Notification> notificationList = new ArrayList<>(0);

    public NotificationAdapter() {
    }

    public void addIntoList(Notification notification) {
        notificationList.add(0, notification);
        notifyDataSetChanged();
    }

    public void updateList(List<Notification> notifications) {
        notificationList.clear();
        notificationList.addAll(notifications);
        notifyDataSetChanged();
    }

    public void addMoreItems(List<Notification> notifications) {
        notificationList.addAll(notifications);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        ItemNotificationBinding binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        viewHolder = new NotificationAdapter.ViewHeader(binding);
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        ((NotificationAdapter.ViewHeader) holder).onBind(notification, position);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class ViewHeader extends RecyclerView.ViewHolder {

        public ItemNotificationBinding binding;

        ViewHeader(ItemNotificationBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        public void onBind(Notification notification, int position) {
            binding.notificationSRNo.setText(String.valueOf(position + 1));
            binding.notificationTitle.setText(notification.getNotificationTitle());
            binding.notificationContent.setText(notification.getNotificationContent());

            if ((position + 1) == getItemCount()) {
                binding.divider.setVisibility(View.GONE);
                binding.dividerLastItem.setVisibility(View.VISIBLE);
            }
        }
    }
}
