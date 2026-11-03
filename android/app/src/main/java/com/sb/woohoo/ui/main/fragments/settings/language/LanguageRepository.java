package com.sb.woohoo.ui.main.fragments.settings.language;


import static com.sb.woohoo.data.prefs.SharedPrefsKey.PREF_KEY_DEVICE_ID;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.API_FAIL_ERROR_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.EMPTY_RESULT_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.NETWORK_SUCCESS_CODE;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.network.di.TimeZoneResponse;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.MainRepository;
import com.sb.woohoo.ui.main.fragments.screensaver.ScreenSaverPresenter;
import com.sb.woohoo.ui.main.fragments.screensaver.pojo.ScreenSaverMultiResponse;
import com.sb.woohoo.ui.main.fragments.settings.language.pojo.LanguageResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LanguageRepository extends BaseRepository<LanguagePresenter> {

    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    public LanguageRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    public void getAllLocale() {
        compositeDisposable.add(wooHooApiCall.getAllLocale().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<LanguageResponse>() {
            @Override
            public void onSuccess(LanguageResponse languageResponse) {
                LoggerWrapper.e(MainRepository.class.getSimpleName(), "**** Get All locale success " + languageResponse);
                int status = languageResponse.getStatus();
                if (status == NETWORK_SUCCESS_CODE) {
                    if (!languageResponse.getData().isEmpty()) {
                        getActions().onAllLocaleFetchSucess(languageResponse.getData());
                    } else {
                        getActions().onError(EMPTY_RESULT_CODE);
                    }
                } else {
                    getActions().onError(status);
                }
            }

            @Override
            public void onError(Throwable e) {
                LoggerWrapper.e(MainRepository.class.getSimpleName(), "**** Get All locale error " + e.getMessage());
                getActions().onError(API_FAIL_ERROR_CODE);
            }
        }));
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
        compositeDisposable.clear();
        wooHooApiCall = null;
    }
}
