package com.sb.woohoo.ui.main.fragments.faq.categories;

import static com.sb.woohoo.ui.main.helpers.NetworkHelper.API_FAIL_ERROR_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.NETWORK_SUCCESS_CODE;

import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.faq.categories.pojo.FaqCategoriesResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FaqCategoriesRepository extends BaseRepository<FaqCategoriesPresenter> {
    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    public FaqCategoriesRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    public void makeFaqCategoriesRequest() {

        compositeDisposable.add(
                wooHooApiCall.getFAQCategories()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<FaqCategoriesResponse>() {
                            @Override
                            public void onSuccess(FaqCategoriesResponse response) {
                                onCategoriesResponse(response);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onErrorResponse(API_FAIL_ERROR_CODE);
                            }
                        }));
    }

    private void onCategoriesResponse(FaqCategoriesResponse response) {
        int status = response.getStatus();
        if (status == NETWORK_SUCCESS_CODE) {
            if (!response.getData().isEmpty()) {
                getActions().onCategoriesResponse(response.getData());
            } else {
                getActions().onErrorResponse(status);
            }
        } else {
            getActions().onErrorResponse(status);
        }
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
    }
}
