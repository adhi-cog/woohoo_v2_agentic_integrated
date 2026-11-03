package com.sb.woohoo.ui.main.fragments.diningrestaurants;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.diningrestaurants.pojo.DiningRestaurantResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DiningRestaurantsRepository extends BaseRepository<DiningRestaurantsPresenter> {

    private final String DEVICE_ID = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");
    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;


    @Inject
    public DiningRestaurantsRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    public void makeRestaurantsRequest() {

        compositeDisposable.add(
                wooHooApiCall.getRestaurants()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DiningRestaurantResponse>() {
                            @Override
                            public void onSuccess(DiningRestaurantResponse response) {
                                onDiningRestaurantResponse(response);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onRestaurantsMessage(e.getMessage());
                            }
                        }));
    }

    private void onDiningRestaurantResponse(DiningRestaurantResponse response) {
        int status = response.getStatus();
        if (status == 200) {
            if (!response.getData().isEmpty()) {
                getActions().onRestaurantsFetched(response.getData());
            } else {
                getActions().onRestaurantsMessage(WooHooApp.getAppContext().getString(R.string.no_result_fetched));
            }
        } else if (status == 502) {
            getActions().onRestaurantsMessage(WooHooApp.getAppContext().getString(R.string.bad_gateway));
        }
    }
}
