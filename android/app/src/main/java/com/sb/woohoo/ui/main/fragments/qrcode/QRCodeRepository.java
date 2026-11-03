package com.sb.woohoo.ui.main.fragments.qrcode;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.qrcode.pojo.QRCodeResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class QRCodeRepository extends BaseRepository<QRCodePresenter> {

    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;


    @Inject
    public QRCodeRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    public void makeQRCodeAPIRequest() {

        compositeDisposable.add(
                wooHooApiCall.getQRCode()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<QRCodeResponse>() {
                            @Override
                            public void onSuccess(QRCodeResponse qrCodeResponse) {
                                onQRCodeResponse(qrCodeResponse);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().qrCodeMessage(e.getMessage());
                            }
                        }));
    }

    private void onQRCodeResponse(QRCodeResponse qrCodeResponse) {
        int status = qrCodeResponse.getStatus();
        if (status == 200) {
            getActions().qrCodeSuccess(qrCodeResponse);
        } else{
            getActions().qrCodeMessage(WooHooApp.getAppContext().getString(R.string.bad_gateway));
        }
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
    }

}
