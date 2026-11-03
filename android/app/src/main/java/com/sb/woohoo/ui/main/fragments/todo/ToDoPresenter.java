package com.sb.woohoo.ui.main.fragments.todo;

import com.sb.woohoo.R;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.todo.pojo.ToDoData;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.Utterances;
import com.sb.woohoo.utils.others.MethodUtils;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import static com.sb.woohoo.ui.main.Categories.Skills.ToDo.TODO_ADD;
import static com.sb.woohoo.ui.main.Categories.Skills.ToDo.TODO_DELETE_ALL;
import static com.sb.woohoo.ui.main.Categories.Skills.ToDo.TODO_DELETE_ORDINAL;
import static com.sb.woohoo.ui.main.Categories.Skills.ToDo.TODO_SHOW;

/**
 * Presenter for {@link ToDoFragment}.
 *
 * @author Ritwik Jamuar.
 */
public class ToDoPresenter
        extends BasePresenter
        implements ToDoContract.Actions {
    private ToDoFragment mView; // View
    private ToDoRepository mRepository; // Repository

    @Inject
    public ToDoPresenter(ToDoFragment view, ToDoRepository repository) {
        this.mView = view;
        this.mRepository = repository;
        this.mRepository.onAttach(ToDoPresenter.this);
    }

    /**
     * Requests {@link ToDoRepository} to perform an API call for fetching To Do's List.
     */
    private void getToDoList() {
        mView.showProgress();
        mRepository.getToDos(false);
    }

    @Override
    public void setToDos(List<ToDoData> toDoList, boolean scrollToLast) {
        mView.removeProgress();
        mView.setToDosToRecyclerView(toDoList);
        if (scrollToLast) {
            mView.scrollToLast();
        }
    }

    @Override
    public void onToDosNotFound() {
        mView.removeProgress();
        mView.showEmptyToDoList();
        mView.respondToUser(Utterances.ToDo.NO_TODO);
    }

    @Override
    public void onAToDoListCreated(ToDoData data) {
        mView.removeProgress();
        if (data == null) {
            mView.showError(mView.getString(R.string.postresponse_corrupted));
        } else {
            mView.hideEmptyToDoList();
            mView.addNewToDoItemsToRecyclerView(data);
            mView.scrollToLast();
        }
    }

    /**
     * Requests {@link ToDoRepository} to perform an API call for Creating a new To Do's RemoteAlertModel
     * in the To Do's List.
     *
     * @param todosTitle Title of Created To Do RemoteAlertModel.
     */
    private void addANewToDo(String todosTitle) {
        // Null Check the parameters before telling the Repository to call the API.
        if (todosTitle.isEmpty()) {
            mView.respondToUser(Utterances.CommonError.UNKNOWN);
            mView.showError(mView.getString(R.string.params_not_provided));
        } else {
            mView.showProgress();
            mRepository.createToDo(todosTitle);
        }
    }

    @Override
    public void onAllToDosDeleted() {
        mView.removeProgress();
        mView.showEmptyToDoList();
    }

    @Override
    public void onAToDoDeleted(int position) {
        mView.removeProgress();
        mView.notifyAdapter(position);
    }

    /**
     * Requests {@link ToDoRepository} to perform an API call for Deleting a To Do Task
     * in a To Do List.
     */
//    private void deleteAToDoItem(int index) {
//        mView.showProgress();
//        ToDoData toDoData = mView.getToDoItemFromAdapter(index);
//        mRepository.deleteASpecificToDo(toDoData.getToDoID(), index);
//    }

    /**
     * Requests {@link ToDoRepository} to perform an API call to Delete all the To Do Items
     * from the To Do's List.
     */
    private void deleteAllToDoItems() {
        mView.showProgress();
        mRepository.deleteAllToDos();
    }

    @Override
    public void onAPICallError(String errorCause) {
        mView.respondToUser(Utterances.CommonError.UNKNOWN);
        mView.removeProgress();
        mView.showError(errorCause);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {
        if (intentInfo != null) {
            switch (intentInfo.getAction()) {
                case TODO_SHOW: {
                    getToDoList();
                }
                break;

                case TODO_ADD: { // Handle Adding a new To Do RemoteAlertModel.
                    HashMap<String, String> intentParameters = intentInfo.getParameters();
                    if (intentParameters.containsKey("name")) {
                        String title = intentParameters.get("name");
                        if (title != null) {
                            title = title.substring(0, 1).toUpperCase() + title.substring(1);
                            addANewToDo(title);
                        }
                    }
                }
                break;

                case TODO_DELETE_ORDINAL:
                    callDeleteToDoByNumber(intentInfo);
                    break;

                case TODO_DELETE_ALL:
                    deleteAllToDoItems();
                    break;
            }
        } else {
            getToDoList();
        }
    }

    private void callDeleteToDoByNumber(IntentInfo intentInfo) {
        HashMap<String, String> intentParameters = intentInfo.getParameters();
        if (intentParameters.containsKey("number")) {
            //FIXME THIS received Double but required int(problem on ASR)
            //FIXME Below is the temporary workaround
            int deleteItemIndex = Integer.parseInt(intentParameters.get("number")) - 1;

            if (deleteItemIndex < 0) {
                mView.respondToUser("It is an invalid Index!");
            } else if (mView.getToDoAdapterItemCount() == 0) {
                mRepository.deleteASpecificToDo(deleteItemIndex);
                //TODO call below after success..
                // deleteAToDoItem(deleteItemIndex);

            } else if (mView.getToDoAdapterItemCount() > 0) {
                ToDoData item = mView.getToDoItemFromAdapter(deleteItemIndex);
                mRepository.deleteASpecificToDo(item.getToDoID(), deleteItemIndex);
            }

//            if (deleteItemIndex < 0 || deleteItemIndex >= mView.getToDoAdapterItemCount()) {
//                mView.respondToUser("To Do RemoteAlertModel not found");
//            } else {
//                ToDoData item = mView.getToDoItemFromAdapter(deleteItemIndex);
//                if (item == null) {
//                    mView.showToastMessage("To Do RemoteAlertModel not found");
//                } else {
//                    deleteAToDoItem(deleteItemIndex);
//                }
//            }
        }
    }

    public void sayToUser(String message) {
        mView.respondToUser(message);
    }

    //TODO Use it with Smart Flow
    public int getOrdinalInteger(String value) {
        double d = 0.0;
        try {
            d = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            MethodUtils.printLog(ToDoPresenter.class, e.getMessage());
        }
        return (int) d;
    }
}