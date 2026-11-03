package com.sb.woohoo.ui.main.fragments.overview.saudidestinations;

import static com.sb.woohoo.ui.main.helpers.NetworkHelper.EMPTY_RESULT_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.NETWORK_SUCCESS_CODE;
import static com.sb.woohoo.utils.Constants.SAUDI_TOURISM_DEFAULT_LIMIT;

import android.util.Log;

import com.sb.woohoo.ui.main.fragments.overview.saudidestinations.pojo.SaudiTourismDestinationsResponse;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SaudiTourismDestinationsRepository extends BaseRepository<SaudiTourismDestinationsPresenter> {
    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    public SaudiTourismDestinationsRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    public void destinationsAPIRequest(String language) {
        compositeDisposable.add(wooHooApiCall.getDestinationsForSaudiTourism(language,SAUDI_TOURISM_DEFAULT_LIMIT).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<SaudiTourismDestinationsResponse>() {
            @Override
            public void onSuccess(SaudiTourismDestinationsResponse saudiTourismResponse) {
                onSaudiTourismResponse(saudiTourismResponse);
            }

            @Override
            public void onError(Throwable e) {
                Log.e("aboutUsResponse", "onError - " + e.getMessage());
            }
        }));
    }

    private void onSaudiTourismResponse(SaudiTourismDestinationsResponse saudiTourismResponse) {
        int status = saudiTourismResponse.getStatus();
        if (status == NETWORK_SUCCESS_CODE) {
            saudiTourismResponse.getData();
            getActions().onSuccess(saudiTourismResponse.getData());
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
