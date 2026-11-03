package com.sb.woohoo.ui.main.fragments.todo;

import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.todo.pojo.ToDoData;
import com.sb.woohoo.ui.main.fragments.todo.pojo.ToDoResponse;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ToDoOperation {

    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;
    private ToDoOperationCallback toDoOperationCallback;

    public ToDoOperation(WooHooApiCall wooHooApiCall,
                         CompositeDisposable compositeDisposable,
                         ToDoOperationCallback toDoOperationCallback) {

        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
        this.toDoOperationCallback = toDoOperationCallback;
    }

    public void fetchAllToDos() {
        compositeDisposable.add(
                wooHooApiCall.getToDos()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ToDoResponse>() {
                            @Override
                            public void onSuccess(ToDoResponse response) {
                                if (response != null) {
                                    List<ToDoData> toDoList = response.getData();
                                    toDoOperationCallback.onSuccess(toDoList);
                                } else {
                                    toDoOperationCallback.onError("No items found!");
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                toDoOperationCallback.onError(e.getMessage());
                            }
                        }));
    }

    public interface ToDoOperationCallback {
        void onSuccess(List<ToDoData> toDoDataList);

        void onError(String message);
    }
}
