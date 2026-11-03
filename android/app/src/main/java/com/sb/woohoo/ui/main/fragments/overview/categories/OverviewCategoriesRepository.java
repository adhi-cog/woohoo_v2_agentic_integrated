package com.sb.woohoo.ui.main.fragments.overview.categories;

import static com.sb.woohoo.ui.main.helpers.NetworkHelper.EMPTY_RESULT_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.NETWORK_SUCCESS_CODE;

import android.util.Log;

import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.overview.categories.pojo.OverviewCategoriesResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OverviewCategoriesRepository extends BaseRepository<OverviewCategoriesPresenter> {
    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    public OverviewCategoriesRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    public void makeAboutUsRequest() {
        compositeDisposable.add(wooHooApiCall.getOverviewCategories().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<OverviewCategoriesResponse>() {
            @Override
            public void onSuccess(OverviewCategoriesResponse overviewCategoriesResponse) {
                onCategoriesResponse(overviewCategoriesResponse);
            }

            @Override
            public void onError(Throwable e) {
                Log.e("aboutUsResponse", "onError - " + e.getMessage());

            }
        }));
    }

    private void onCategoriesResponse(OverviewCategoriesResponse response) {
        int status = response.getStatus();
        if (status == NETWORK_SUCCESS_CODE) {
            if (!response.getData().getCategories().isEmpty()) {
                getActions().onSuccess(response.getData().getCategories());
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
