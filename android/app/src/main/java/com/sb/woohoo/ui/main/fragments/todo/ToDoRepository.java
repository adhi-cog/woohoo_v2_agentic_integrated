package com.sb.woohoo.ui.main.fragments.todo;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.todo.pojo.ToDoData;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Repository for {@link ToDoPresenter}.
 *
 * @author Ritwik Jamuar
 */
public class ToDoRepository extends BaseRepository<ToDoPresenter> {
    private WooHooApiCall mAPICall;
    private CompositeDisposable compositeDisposable;
    private String hubId;

    @Inject
    ToDoRepository(WooHooApiCall apiCall, CompositeDisposable compositeDisposable) {
        this.mAPICall = apiCall;
        this.compositeDisposable = compositeDisposable;
        SharedPrefsHelper mSharedPrefsHelper = WooHooApp.getSharedPrefsHelper();
        hubId = mSharedPrefsHelper.get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");
    }

    /*------------------------------- Methods to Get To DOs --------------------------------------*/

    /**
     * Performs an API call for fetching To Do's List.
     */
    void getToDos(boolean scrollToLast) {
        ToDoOperation toDoOperation = new ToDoOperation(mAPICall,
                compositeDisposable,
                new ToDoOperation.ToDoOperationCallback() {
                    @Override
                    public void onSuccess(List<ToDoData> toDoDataList) {
                        if (toDoDataList.size() == 0) {
                            getActions().onToDosNotFound();
                        } else {
                            getActions().setToDos(toDoDataList, scrollToLast);
                        }
                    }

                    @Override
                    public void onError(String message) {
                        getActions().onAPICallError(message);
                    }
                });
        toDoOperation.fetchAllToDos();
    }

    /*------------------------------- Methods to create a To DOs ---------------------------------*/

    /**
     * Performs an API call for Creating a new To Do's List.
     */
    void createToDo(String todoList) {
        compositeDisposable.add(
                mAPICall.addNewToDo(todoList, hubId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ToDoData>() {
                            @Override
                            public void onSuccess(ToDoData response) {
//                                getActions().onAToDoListCreated(response);
                                getToDos(true);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                            }
                        })
        );
    }
    /*------------------------------ Methods to delete all To DOs --------------------------------*/

    /**
     * Performs an API call for Deleting all To Do List of User.
     */
    void deleteAllToDos() {
        compositeDisposable.add(mAPICall.deleteAllToDos()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {

                    @Override
                    public void onComplete() {
                        getActions().onAllToDosDeleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        getActions().onAPICallError(e.getMessage());
                    }
                })
        );
    }

    /*------------------------- Methods to delete a specific To Do List --------------------------*/

    /**
     * Performs an API call for Deleting a specific To Do List.
     */
    void deleteASpecificToDo(int position) {
        ToDoOperation toDoOperation = new ToDoOperation(mAPICall,
                compositeDisposable,
                new ToDoOperation.ToDoOperationCallback() {
                    @Override
                    public void onSuccess(List<ToDoData> toDoDataList) {
                        if (!toDoDataList.isEmpty()) {
                            getActions().setToDos(toDoDataList, false);
                            if (toDoDataList.size() > position) {
                                ToDoData item = toDoDataList.get(position);
                                deleteASpecificToDo(item.getToDoID(), position);
                            } else {
                                getActions().sayToUser("Index invalid!");
                            }
                        } else {
                            getActions().onToDosNotFound();
                            getActions().sayToUser("Your list is empty");
                        }
                    }

                    @Override
                    public void onError(String message) {
                        getActions().onAPICallError(message);
                    }
                });
        toDoOperation.fetchAllToDos();
    }

    void deleteASpecificToDo(String todoID, int position) {
        compositeDisposable.add(
                mAPICall.deleteASpecificToDo(todoID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {

                            @Override
                            public void onComplete() {
                                getActions().onAToDoDeleted(position);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                            }
                        })
        );
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
    }
}