package com.sb.woohoo.ui.main.fragments.todo;

import com.sb.woohoo.ui.main.fragments.todo.pojo.ToDoItem;

/**
 * Listener for RemoteAlertModel Clicked on RecyclerView for {@link ToDoItemViewHolder}.
 */
public interface ToDoListener {
    void onToDoDeleteClicked(ToDoItem toDo);

    void onToDoModifyClicked(ToDoItem toDo, int position);
}