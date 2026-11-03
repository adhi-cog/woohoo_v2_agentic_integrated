package com.sb.woohoo.ui.main.fragments.screensaver;


import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.screensaver.pojo.ScreenSaverMultiResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.sb.woohoo.data.prefs.SharedPrefsKey.PREF_KEY_DEVICE_ID;

public class ScreenSaverRepository extends BaseRepository<ScreenSaverPresenter> {

    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;
    private int count=0;

    @Inject
    public ScreenSaverRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }


    void getScreenSaverDetails (int i) {
        LoggerWrapper.i(ScreenSaverRepository.class.getSimpleName(),"screen saver api error "+(WooHooApp.getSharedPrefsHelper().get(PREF_KEY_DEVICE_ID,null)));

        count=i;
        compositeDisposable.add(
                    wooHooApiCall.
                            getScreenSaverMulti(WooHooApp.getSharedPrefsHelper().get(PREF_KEY_DEVICE_ID, null))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<ScreenSaverMultiResponse>() {
                                @Override
                                public void onSuccess(ScreenSaverMultiResponse screenSaverResponse) {
                                    if (screenSaverResponse.getStatus() == 200 && screenSaverResponse.getData()!=null) {

                                        LoggerWrapper.i(ScreenSaverRepository.class.getSimpleName(),"screen saver api 200 response "
                                                +"\ntime:"+screenSaverResponse.getData().getScreenSaverTime()+
                                                "\nslide show:"+screenSaverResponse.getData().getSlideShow()+
                                                "\nimage :"+screenSaverResponse.getData().getImageUrl());
                                        getActions().onDetailsFetchingSuccess(screenSaverResponse.getData());
                                    } else {

                                        if(count<3){
                                            count++;
                                            getScreenSaverDetails(count);
                                        }
                                        LoggerWrapper.e(ScreenSaverRepository.class.getSimpleName(),"screen saver api error "+screenSaverResponse.getError());
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {

                                    if(count<3){
                                        count++;
                                        getScreenSaverDetails(count);

                                    }

                                    LoggerWrapper.e(ScreenSaverRepository.class.getSimpleName(),"screen saver throw error "+e.getMessage());
                                    getActions().onError(e.getMessage());
                                }
                            })
            );
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
        compositeDisposable.clear();
        wooHooApiCall=null;
    }
}
