package com.sb.woohoo.ui.main.fragments.overview.saudiexperiencedetail;

import static com.sb.woohoo.ui.main.helpers.NetworkHelper.API_FAIL_ERROR_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.EMPTY_RESULT_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.NETWORK_SUCCESS_CODE;

import android.util.Log;

import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.overview.saudiexperiencedetail.pojo.SaudiTourismExperienceDetailsResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ExperienceDetailRepository extends BaseRepository<ExperienceDetailPresenter> {
    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    public ExperienceDetailRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    public void experienceAPIRequest(String experienceId,String language) {
        compositeDisposable.add(wooHooApiCall.getExperienceIDForSaudiTourism(experienceId,language).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<SaudiTourismExperienceDetailsResponse>() {
            @Override
            public void onSuccess(SaudiTourismExperienceDetailsResponse saudiTourismResponse) {
                onSaudiTourismResponse(saudiTourismResponse);

            }

            @Override
            public void onError(Throwable e) {
                Log.e("aboutUsResponse", "onError - " + e.getMessage());
                getActions().errorMessage(API_FAIL_ERROR_CODE);
            }
        }));
    }

    private void onSaudiTourismResponse(SaudiTourismExperienceDetailsResponse saudiTourismResponse) {
        int status = saudiTourismResponse.getStatus();
        if (status == NETWORK_SUCCESS_CODE) {
            if (saudiTourismResponse.getData()!= null) {
                getActions().onSuccess(saudiTourismResponse.getData());
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
