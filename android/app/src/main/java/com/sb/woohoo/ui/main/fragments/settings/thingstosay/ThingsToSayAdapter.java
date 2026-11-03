package com.sb.woohoo.ui.main.fragments.settings.thingstosay;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.databinding.ItemThingsToSayBinding;
import com.sb.woohoo.databinding.ItemThingsToSayHeaderBinding;
import com.sb.woohoo.ui.main.fragments.settings.thingstosay.pojo.ThingsToSayItems;

import java.util.ArrayList;
import java.util.List;

public class ThingsToSayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_HEADER = 0;
    private final int TYPE_UTTERANCE = 1;
    private List<ThingsToSayItems> thingsToSayList = new ArrayList<>(0);

    void initializeList(List<ThingsToSayItems> thingsToSays) {
        thingsToSayList.clear();
        thingsToSayList.addAll(thingsToSays);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (thingsToSayList.get(position).isHeader()) {
            return TYPE_HEADER;
        } else {
            return TYPE_UTTERANCE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == TYPE_HEADER) {
            ItemThingsToSayHeaderBinding binding = ItemThingsToSayHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            viewHolder = new ThingsToSayViewHolderHeader(binding);

        } else {
            ItemThingsToSayBinding binding = ItemThingsToSayBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            viewHolder = new ThingsToSayViewHolder(binding);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ThingsToSayItems thingsToSayItems = thingsToSayList.get(position);
        if (holder instanceof ThingsToSayViewHolder) {
            ((ThingsToSayViewHolder) holder).onBind(thingsToSayItems);
        } else {
            ((ThingsToSayViewHolderHeader) holder).onBind(thingsToSayItems);
        }
    }

    @Override
    public int getItemCount() {
        return thingsToSayList == null ? 0 : thingsToSayList.size();
    }

    class ThingsToSayViewHolder extends RecyclerView.ViewHolder {

        public ItemThingsToSayBinding binding;

        ThingsToSayViewHolder(ItemThingsToSayBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        public void onBind(ThingsToSayItems thingsToSay) {
            binding.itemThingsToSayTitle.setText(thingsToSay.getName());
        }
    }

    class ThingsToSayViewHolderHeader extends RecyclerView.ViewHolder {

        public ItemThingsToSayHeaderBinding binding;

        ThingsToSayViewHolderHeader(ItemThingsToSayHeaderBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }
        public void onBind(ThingsToSayItems thingsToSay) {
            binding.skillName.setText(thingsToSay.getName());
        }
    }

}