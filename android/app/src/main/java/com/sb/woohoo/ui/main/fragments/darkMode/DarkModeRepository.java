package com.sb.woohoo.ui.main.fragments.darkMode;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceon.SurveillancePresenter;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import retrofit2.Retrofit;

public class DarkModeRepository extends BaseRepository<DarkModePresenter> {

    private final CompositeDisposable compositeDisposable;
    private WooHooApiCall apicall;
    private String deviceId;

    @Inject
    public DarkModeRepository(Retrofit retrofit, CompositeDisposable compositeDisposable) {
        apicall = retrofit.create(WooHooApiCall.class);
        deviceId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");
        this.compositeDisposable = compositeDisposable;
    }


    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
        compositeDisposable.clear();
    }
}
