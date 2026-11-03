package com.sb.woohoo.ui.main.fragments.overview.item;

import android.util.Log;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.overview.item.pojo.OverviewItemResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OverviewItemRepository extends BaseRepository<OverviewItemPresenter> {
    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    public OverviewItemRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    public void makeOverviewProducts(String id) {

        compositeDisposable.add(
                wooHooApiCall.getOverviewProducts(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<OverviewItemResponse>() {
                            @Override
                            public void onSuccess(OverviewItemResponse overviewItemResponse) {
                                onCategoriesResponse(overviewItemResponse);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("aboutUsResponse", "onError - " + e.getMessage());
                            }
                        }));
    }

    private void onCategoriesResponse(OverviewItemResponse response) {
        int status = response.getStatus();
        if (status == 200) {
            if (response.getData() != null && response.getData().getItems() != null) {
                getActions().onSuccess(response.getData().getItems());
            } else {
                getActions().errorMessage(WooHooApp.getAppContext().getString(R.string.no_result_fetched));
            }
        } else if (status == 502) {
            getActions().errorMessage(WooHooApp.getAppContext().getString(R.string.bad_gateway));
        }
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
    }
}
