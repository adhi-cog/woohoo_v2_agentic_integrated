package com.sb.woohoo.ui.main.fragments.fnb.sub_categories;

import static com.sb.woohoo.ui.main.helpers.NetworkHelper.EMPTY_RESULT_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.NETWORK_SUCCESS_CODE;

import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.fnb.sub_categories.di.pojo.FnBSubCategoriesResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FnBSubCategoriesRepository extends BaseRepository<FnBSubCategoriesPresenter> {

    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    public FnBSubCategoriesRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    public void makeSubCategoriesRequest(String id) {
        compositeDisposable.add(
                wooHooApiCall.getFnBSubCategories(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<FnBSubCategoriesResponse>() {
                            @Override
                            public void onSuccess(FnBSubCategoriesResponse fnBCategoriesResponse) {
                                onSubCategoriesResponse(fnBCategoriesResponse);
                            }

                            @Override
                            public void onError(Throwable e) {
//                                getActions().covidMessage(e.getMessage());
                            }
                        }));
    }

    private void onSubCategoriesResponse(FnBSubCategoriesResponse fnBCategoriesResponse) {
        int status = fnBCategoriesResponse.getStatus();
        if (status == NETWORK_SUCCESS_CODE) {
            if (!fnBCategoriesResponse.getData().getCategories().isEmpty()) {
                getActions().onSuccess(fnBCategoriesResponse.getData().getCategories(), fnBCategoriesResponse.getData().getBanners());
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
