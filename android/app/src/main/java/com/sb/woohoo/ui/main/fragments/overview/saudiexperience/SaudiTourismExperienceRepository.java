package com.sb.woohoo.ui.main.fragments.overview.saudiexperience;

import static com.sb.woohoo.ui.main.helpers.NetworkHelper.EMPTY_RESULT_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.NETWORK_SUCCESS_CODE;
import static com.sb.woohoo.utils.Constants.SAUDI_TOURISM_DEFAULT_PAGE_LIMIT;

import android.util.Log;

import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.overview.saudiexperience.pojo.SaudiTourismExperienceResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SaudiTourismExperienceRepository extends BaseRepository<SaudiTourismExperiencePresenter> {
    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    public SaudiTourismExperienceRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    public void experienceAPIRequest(String language) {
        compositeDisposable.add(wooHooApiCall.getExperienceForSaudiTourism(language,SAUDI_TOURISM_DEFAULT_PAGE_LIMIT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<SaudiTourismExperienceResponse>() {
            @Override
            public void onSuccess(SaudiTourismExperienceResponse saudiTourismResponse) {
                onSaudiTourismResponse(saudiTourismResponse);
                Log.e("aboutUsResponse", "onError - " + saudiTourismResponse);

            }

            @Override
            public void onError(Throwable e) {
                Log.e("aboutUsResponse", "onError - " + e.getMessage());

            }
        }));
    }

    private void onSaudiTourismResponse(SaudiTourismExperienceResponse saudiTourismResponse) {
        int status = saudiTourismResponse.getStatus();
        if (status == NETWORK_SUCCESS_CODE) {
            if (!saudiTourismResponse.getData().getDataList().isEmpty()) {
                getActions().onSuccess(saudiTourismResponse.getData().getDataList());
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
