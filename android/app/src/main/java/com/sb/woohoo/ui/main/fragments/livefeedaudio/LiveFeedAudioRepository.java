package com.sb.woohoo.ui.main.fragments.livefeedaudio;

import android.util.Log;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Retrofit;

public class LiveFeedAudioRepository extends BaseRepository<LiveFeedAudioPresenter> {

    private WooHooApiCall client;
    private CompositeDisposable compositeDisposable;

    @Inject
    public LiveFeedAudioRepository(Retrofit retrofit, CompositeDisposable compositeDisposable) {
        client = retrofit.create(WooHooApiCall.class);
        this.compositeDisposable = compositeDisposable;
    }

    public void updateHubStatus(boolean status) {

        String deviceId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");

        compositeDisposable.add(
                client.updateHubStatus(deviceId, status)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {

                            @Override
                            public void onComplete() {
                                LoggerWrapper.i("HubStatusUpdateSuccess", "true");
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                            }
                        }));
    }


    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
        compositeDisposable.clear();
    }
}