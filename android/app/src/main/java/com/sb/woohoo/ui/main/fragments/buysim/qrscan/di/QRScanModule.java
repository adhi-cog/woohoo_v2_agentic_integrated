package com.sb.woohoo.ui.main.fragments.buysim.qrscan.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.buysim.cart.CartVerificationFragment;
import com.sb.woohoo.ui.main.fragments.buysim.qrscan.QRScanFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class QRScanModule {

    private QRScanFragment qRScanFragment;

    public QRScanModule(QRScanFragment qRScanFragment) {
        this.qRScanFragment = qRScanFragment;
    }

    @Provides
    QRScanFragment getQRScanFragment() {
        return qRScanFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(qRScanFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return qRScanFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(qRScanFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(qRScanFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
