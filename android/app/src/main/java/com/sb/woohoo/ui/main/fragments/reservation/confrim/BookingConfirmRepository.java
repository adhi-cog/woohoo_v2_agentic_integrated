package com.sb.woohoo.ui.main.fragments.reservation.confrim;

import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.reservation.booking.pojo.DiningRestaurantRequest;
import com.sb.woohoo.ui.main.fragments.reservation.upcoming.pojo.CancelBookingResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class BookingConfirmRepository extends BaseRepository<BookingConfirmPresenter> {

    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;


    @Inject
    public BookingConfirmRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
    }

    public void makeCancelBookingRequest(int id) {
        compositeDisposable.add(wooHooApiCall.makeCancelBookingRequest(String.valueOf(id)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<CancelBookingResponse>() {
            @Override
            public void onSuccess(CancelBookingResponse response) {
                onCancelBookingResponse(response);
            }

            @Override
            public void onError(Throwable e) {
                getActions().errorMessage(e.getMessage());
            }
        }));
    }

    private void onCancelBookingResponse(CancelBookingResponse response) {
        int status = response.getStatus();
        if (status == 200) {
            if (response.getData() != null) {
                getActions().onCancelBookingConfirm();
            } else {
                getActions().onCancelBookingConfirm();
            }
        }
    }

    public void makeDiningRestaurant(DiningRestaurantRequest obj) {
        compositeDisposable.add(wooHooApiCall.makeDiningRestaurantBooking(obj).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody response) {
                try {
                    getActions().onBookingResponse(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e) {
                getActions().errorMessage(e.getMessage());
            }
        }));
    }
}
