package com.sb.woohoo.ui.main.fragments.todo.pojo;

import com.google.gson.annotations.SerializedName;
import com.sb.woohoo.ui.main.pojo.nlp.BaseModel;

import java.io.Serializable;
import java.util.HashMap;

/**
 * POJO containing individual To Do RemoteAlertModel which can be found in {@link ToDoData}.
 */
public class ToDoItem extends BaseModel<ToDoItem> implements Serializable {
    @SerializedName("todos_id")
    private String todosID;
    @SerializedName("checked")
    private boolean checked;
    @SerializedName("todo")
    private String todo;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getTodo() {
        return todo;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

    public String getTodosID() {
        return todosID;
    }

    public void setTodosID(String todosID) {
        this.todosID = todosID;
    }

    @Override
    public void parseModel(HashMap<String, String> hashMap) {
    }
}