package com.sb.woohoo.ui.main.fragments.fnb.order;

import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.fnb.order.pojo.PlaceOrderRequest;
import com.sb.woohoo.ui.main.fragments.fnb.order.pojo.PlaceOrderResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FnBOrdersRepository extends BaseRepository<FnBOrdersPresenter> {
    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    public FnBOrdersRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    public void makePlaceOrderRequest(PlaceOrderRequest placeOrderRequest) {

        compositeDisposable.add(
                wooHooApiCall.placeOrder(placeOrderRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<PlaceOrderResponse>() {
                            @Override
                            public void onSuccess(PlaceOrderResponse placeOrderResponse) {
                                try {
                                    getActions().onSuccess(placeOrderResponse.getMessage(), placeOrderResponse.getOrderData());

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    getActions().errorMessage();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().errorMessage();
                            }
                        }));
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
    }
}
