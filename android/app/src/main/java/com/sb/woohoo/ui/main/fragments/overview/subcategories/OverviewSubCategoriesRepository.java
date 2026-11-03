package com.sb.woohoo.ui.main.fragments.overview.subcategories;

import static com.sb.woohoo.ui.main.helpers.NetworkHelper.EMPTY_RESULT_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.NETWORK_SUCCESS_CODE;

import android.util.Log;

import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.overview.subcategories.pojo.OverviewSubCategoriesResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OverviewSubCategoriesRepository extends BaseRepository<OverviewSubCategoriesPresenter> {
    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    public OverviewSubCategoriesRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    public void makeOverviewSubCategoriesRequest(String id) {
        compositeDisposable.add(
                wooHooApiCall.getOverviewSubCategories(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<OverviewSubCategoriesResponse>() {
                            @Override
                            public void onSuccess(OverviewSubCategoriesResponse overviewSubCategoriesResponse) {
                                onCategoriesResponse(overviewSubCategoriesResponse);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("aboutUsResponse", "onError - " + e.getMessage());
                            }
                        }));
    }

    private void onCategoriesResponse(OverviewSubCategoriesResponse response) {
        int status = response.getStatus();
        if (status == NETWORK_SUCCESS_CODE) {
            if (!response.getData().getSubCategories().isEmpty()) {
                getActions().onSuccess(response.getData().getSubCategories());
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
