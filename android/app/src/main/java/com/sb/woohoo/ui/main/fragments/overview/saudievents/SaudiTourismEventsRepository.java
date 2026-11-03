package com.sb.woohoo.ui.main.fragments.overview.saudievents;

import static com.sb.woohoo.ui.main.helpers.NetworkHelper.EMPTY_RESULT_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.NETWORK_SUCCESS_CODE;
import static com.sb.woohoo.utils.Constants.SAUDI_TOURISM_DEFAULT_LIMIT;

import android.util.Log;

import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.overview.saudievents.pojo.SaudiTourismResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SaudiTourismEventsRepository extends BaseRepository<SaudiTourismEventsPresenter> {
    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    public SaudiTourismEventsRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    public void eventsAPIRequest(String language) {
        compositeDisposable.add(wooHooApiCall.getEventsForSaudiTourism(language,SAUDI_TOURISM_DEFAULT_LIMIT).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<SaudiTourismResponse>() {
            @Override
            public void onSuccess(SaudiTourismResponse saudiTourismResponse) {
                onSaudiTourismResponse(saudiTourismResponse);
            }

            @Override
            public void onError(Throwable e) {
                Log.e("aboutUsResponse", "onError - " + e.getMessage());
            }
        }));
    }

    private void onSaudiTourismResponse(SaudiTourismResponse saudiTourismResponse) {
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
