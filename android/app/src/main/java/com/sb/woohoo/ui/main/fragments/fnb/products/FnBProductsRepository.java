package com.sb.woohoo.ui.main.fragments.fnb.products;

import static com.sb.woohoo.ui.main.helpers.NetworkHelper.NETWORK_SUCCESS_CODE;

import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.fnb.products.pojo.FnBProductsResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FnBProductsRepository extends BaseRepository<FnBProductsPresenter> {

    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    public FnBProductsRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    public void makeProductsRequest(String id, boolean isFromSubCategory) {

        if (isFromSubCategory) {
            compositeDisposable.add(
                    wooHooApiCall.getFnBProductsFromSubCategoryId(id)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<FnBProductsResponse>() {
                                @Override
                                public void onSuccess(FnBProductsResponse fnBProductsResponse) {
                                    onProductsResponse(fnBProductsResponse);
                                }

                                @Override
                                public void onError(Throwable e) {
//                                getActions().covidMessage(e.getMessage());
                                }
                            }));
        } else {
            compositeDisposable.add(
                    wooHooApiCall.getFnBProducts(id)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<FnBProductsResponse>() {
                                @Override
                                public void onSuccess(FnBProductsResponse fnBProductsResponse) {
                                    onProductsResponse(fnBProductsResponse);
                                }

                                @Override
                                public void onError(Throwable e) {
//                                getActions().covidMessage(e.getMessage());
                                }
                            }));
        }
    }

    private void onProductsResponse(FnBProductsResponse fnBProductsResponse) {
        int status = fnBProductsResponse.getStatus();
        if (status == NETWORK_SUCCESS_CODE) {
            if (!fnBProductsResponse.getData().getProducts().isEmpty()) {
                getActions().onSuccess(fnBProductsResponse.getData().getProducts(), fnBProductsResponse.getData().getBanners());
            } else {
                getActions().errorMessage(status);
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
