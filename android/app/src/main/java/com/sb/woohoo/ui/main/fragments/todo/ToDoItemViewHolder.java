package com.sb.woohoo.ui.main.fragments.todo;

import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.databinding.ItemToDoBinding;
import com.sb.woohoo.ui.main.fragments.todo.pojo.ToDoData;

/**
 * {@link RecyclerView.ViewHolder} for the {@link RecyclerView.Adapter} : {@link ToDoAdapter}.
 *
 * @author Ritwik Jamuar.
 */
class ToDoItemViewHolder extends RecyclerView.ViewHolder {

    public ItemToDoBinding binding;

    ToDoItemViewHolder(ItemToDoBinding itemRowBinding) {
        super(itemRowBinding.getRoot());
        this.binding = itemRowBinding;
    }

    void onBind(ToDoData toDo) {
        binding.itemToDoTextViewNumber.setText(String.valueOf(getAdapterPosition() + 1));
        binding.itemToDoTextViewToDoName.setText(toDo.getTitle());
    }
}
