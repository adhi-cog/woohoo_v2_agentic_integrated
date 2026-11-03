package com.sb.woohoo.ui.main.fragments.fnb.history;

import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.fnb.history.pojo.FnBHistoryResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FnBHistoryRepository extends BaseRepository<FnBHistoryPresenter> {

    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    public FnBHistoryRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    public void makeOrderHistoryRequest() {

        compositeDisposable.add(
                wooHooApiCall.getFnBOrdersHistory()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<FnBHistoryResponse>() {
                            @Override
                            public void onSuccess(FnBHistoryResponse fnBHistoryResponse) {
                                try {
                                    getActions().onSuccess(fnBHistoryResponse.getMessage(), fnBHistoryResponse.getData());

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    getActions().errorMessage();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().errorMessage();
                            }
                        }));
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
    }
}
