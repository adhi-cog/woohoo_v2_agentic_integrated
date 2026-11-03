package com.sb.woohoo.ui.main.fragments.todo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.ItemSettingsBinding;
import com.sb.woohoo.databinding.ItemToDoBinding;
import com.sb.woohoo.ui.main.fragments.todo.pojo.ToDoData;
import com.sb.woohoo.ui.main.fragments.todo.pojo.ToDoItem;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoItemViewHolder> {
    private List<ToDoData> mToDO = new ArrayList<>(0);
    private ToDoFragment toDoFragment;

    @Inject
    ToDoAdapter(ToDoFragment fragment) {
        this.toDoFragment = fragment;
    }

    /**
     * Instantiates a new {@link List} of {@link ToDoItem} and notifies adapter for change in DeviceData Set.
     *
     * @param toDoList {@link List} of {@link ToDoItem}.
     */
    void initializeList(List<ToDoData> toDoList) {
        mToDO.clear();
        mToDO.addAll(toDoList);
        notifyDataSetChanged();
    }

    /**
     * Updates existing {@link List} of {@link ToDoItem} with new {@link List} of {@link ToDoItem}
     * and notifies adapter for change in DeviceData Set.
     *
     * @param toDoList {@link List} of {@link ToDoItem}.
     */
    public void updateList(List<ToDoData> toDoList) {
        mToDO.addAll(toDoList);
        notifyDataSetChanged();
    }

    void addItem(ToDoData toDoData) {
        mToDO.add(toDoData);
        notifyDataSetChanged();
    }

    void deleteItem(int position) {
        mToDO.remove(position);
        notifyDataSetChanged();
        if (getItemCount() == 0) {
            toDoFragment.showEmptyToDoList();
        }
    }

    ToDoData getToDoItem(int position) {
        ToDoData item;
        if (position < 0 || position > mToDO.size()) return null;
        else item = mToDO.get(position);
        return item;
    }

    @NonNull
    @Override
    public ToDoItemViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {
        ItemToDoBinding binding = ItemToDoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ToDoItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoItemViewHolder holder, int position) {
        holder.onBind(mToDO.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mToDO == null ? 0 : mToDO.size();
    }
}