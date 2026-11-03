package com.sb.woohoo.ui.main.fragments.templates.text.list;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.databinding.ListTemplateListItemBinding;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {

    //FIXME change to Object to required Pojo
    private List<String> objectList;
    private ListFragment mView;

    @Inject
    public ItemAdapter(ListFragment listFragment) {
        this.mView = listFragment;
        objectList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListTemplateListItemBinding binding = ListTemplateListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.MyViewHolder holder, int position) {
        if (objectList != null && objectList.size() > position) {
            holder.bindViews(objectList.get(position), position + 1);
        }
    }

    @Override
    public int getItemCount() {
        return objectList != null ? objectList.size() : 0;
    }

    public void addList(List<String> objects) {
        objectList.clear();
        objectList.addAll(objects);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ListTemplateListItemBinding binding;

        MyViewHolder(ListTemplateListItemBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }
        public void bindViews(String text, int position) {
            binding.textViewItem.setText(text);
            binding.textViewSerial.setText(String.valueOf(position));
        }

    }
}
