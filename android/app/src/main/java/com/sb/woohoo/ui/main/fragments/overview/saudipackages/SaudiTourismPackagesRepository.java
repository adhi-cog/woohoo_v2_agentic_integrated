package com.sb.woohoo.ui.main.fragments.overview.saudipackages;

import static com.sb.woohoo.ui.main.helpers.NetworkHelper.EMPTY_RESULT_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.NETWORK_SUCCESS_CODE;

import android.util.Log;

import com.sb.woohoo.ui.main.fragments.overview.saudipackages.pojo.SaudiTourismPackagesResponse;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SaudiTourismPackagesRepository extends BaseRepository<SaudiTourismPackagesPresenter> {
    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    public SaudiTourismPackagesRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    public void packagesAPIRequest() {
        compositeDisposable.add(wooHooApiCall.getPackagesForSaudiTourism().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<SaudiTourismPackagesResponse>() {
            @Override
            public void onSuccess(SaudiTourismPackagesResponse saudiTourismResponse) {
                onSaudiTourismResponse(saudiTourismResponse);
                Log.e("aboutUsResponse", "onError - " + saudiTourismResponse);

            }

            @Override
            public void onError(Throwable e) {
                Log.e("aboutUsResponse", "onError - " + e.getMessage());

            }
        }));
    }

    private void onSaudiTourismResponse(SaudiTourismPackagesResponse saudiTourismResponse) {
        int status = saudiTourismResponse.getStatus();
        if (status == NETWORK_SUCCESS_CODE) {
            if (!saudiTourismResponse.getData().getData().isEmpty()) {
                getActions().onSuccess(saudiTourismResponse.getData().getData());
            } else {
                getActions().errorMessage(EMPTY_RESULT_CODE);
            }
        } else {
            getActions().errorMessage(status);
        }
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
    }
}
