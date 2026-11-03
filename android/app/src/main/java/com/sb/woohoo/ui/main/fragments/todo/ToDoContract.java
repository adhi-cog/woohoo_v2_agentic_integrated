package com.sb.woohoo.ui.main.fragments.todo;

import com.sb.woohoo.ui.main.fragments.todo.pojo.ToDoData;

import java.util.List;

/**
 * MVP Interface for Providing Methods across Model, View and Presenter in {@link ToDoFragment}.
 *
 * @author Ritwik Jamuar
 */
public interface ToDoContract {
    interface View {
        void showEmptyToDoList();

        void hideEmptyToDoList();

        void showError(String message);

        void setToDosToRecyclerView(List<ToDoData> toDoList);

        void addNewToDoItemsToRecyclerView(ToDoData toDoList);

        void showProgress();

        void removeProgress();

        void scrollToLast();

        int getToDoAdapterItemCount();

        ToDoData getToDoItemFromAdapter(int position);

        void showToastMessage(String message);

        void notifyAdapter(int position);
    }

    interface Actions {
        void onAPICallError(String errorCause);

        void setToDos(List<ToDoData> toDoList, boolean scrollToLast);

        void onToDosNotFound();

        void onAToDoListCreated(ToDoData toDoData);

        void onAllToDosDeleted();

        void onAToDoDeleted(int position);

    }
}