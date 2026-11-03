package com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceinitial;

import android.util.Log;

import com.sb.woohoo.custom.LoggerWrapper;
import com.google.gson.Gson;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.surveillancemode.pojo.UserDetailModel;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Retrofit;

public class SurveillanceInitRepository
        extends BaseRepository<SurveillanceInitPresenter> {

    private WooHooApiCall apiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    public SurveillanceInitRepository(Retrofit retrofit, Gson gson, CompositeDisposable compositeDisposable) {
        apiCall = retrofit.create(WooHooApiCall.class);
        this.compositeDisposable = compositeDisposable;
    }

    void verifyUserPinExists() {
        String userId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_USER_ID, "");
        compositeDisposable.add(
                apiCall.getUserDetails(userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserDetailModel>() {
                            @Override
                            public void onSuccess(UserDetailModel userDetailModel) {
                                getActions().onUserDetailsFetched(userDetailModel);
                            }

                            @Override
                            public void onError(Throwable e) {
                                LoggerWrapper.e("Surveillance", e.getMessage());
                            }
                        })
        );
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        apiCall = null;
        compositeDisposable.clear();
        compositeDisposable.dispose();
    }
}
