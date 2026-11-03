package com.sb.woohoo.ui.main.fragments.settings.thingstosay;

import android.util.Log;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.settings.thingstosay.pojo.ThingsToSayResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ThingsToSayRepository extends BaseRepository<ThingsToSayPresenter> {

    private CompositeDisposable compositeDisposable;
    private WooHooApiCall mAPICall;

    @Inject
    public ThingsToSayRepository(CompositeDisposable compositeDisposable, WooHooApiCall apiCall) {
        this.compositeDisposable = compositeDisposable;
        this.mAPICall = apiCall;
    }

    void getList() {
        compositeDisposable.add(
                mAPICall.getThingsToSay()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ThingsToSayResponse>() {
                            @Override
                            public void onSuccess(ThingsToSayResponse thingsToSayResponse) {
                                getActions().showList(thingsToSayResponse.getData());
                            }

                            @Override
                            public void onError(Throwable e) {
                                LoggerWrapper.e("ThingsToSay", e.getMessage());
                            }
                        }));
    }
}
