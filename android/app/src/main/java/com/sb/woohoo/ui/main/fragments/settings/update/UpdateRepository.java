package com.sb.woohoo.ui.main.fragments.settings.update;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UpdateRepository extends BaseRepository<UpdatePresenter> {

    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    UpdateRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {

        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    void checkForUpdates() {
        String hubID = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, null);
        compositeDisposable.add(wooHooApiCall.getUpdate(hubID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<UpdateModel>() {

                    @Override
                    public void onSuccess(UpdateModel updateModel) {
                        responseCall(updateModel);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getActions().onCheckUpdateFailed();
                    }
                }));
    }

    private void responseCall(UpdateModel updateModel) {
        if (updateModel != null) {
            UpdateDataModel updateDataModel = updateModel.getData();
            if (updateDataModel != null) {
                getActions().onCheckUpdateSuccess(updateDataModel);
            } else {
                getActions().onCheckUpdateFailed();
            }
        } else {
            getActions().onCheckUpdateFailed();
        }
    }
}
