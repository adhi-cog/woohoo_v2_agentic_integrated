package com.sb.woohoo.ui.main.fragments.qrcode.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.qrcode.QRCodeContract;
import com.sb.woohoo.ui.main.fragments.qrcode.QRCodeFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class QRCodeModule {

    private QRCodeFragment qrCodeFragment;

    public QRCodeModule(QRCodeFragment qrCodeFragment) {
        this.qrCodeFragment = qrCodeFragment;
    }

    @Provides
    QRCodeContract.Views getQRCodeFragment() {
        return qrCodeFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(qrCodeFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return qrCodeFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(qrCodeFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(qrCodeFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
