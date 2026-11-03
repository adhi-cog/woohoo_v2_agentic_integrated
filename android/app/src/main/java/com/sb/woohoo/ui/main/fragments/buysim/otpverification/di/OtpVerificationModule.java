package com.sb.woohoo.ui.main.fragments.buysim.otpverification.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.buysim.otpverification.OtpVerificationFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class OtpVerificationModule {

    private OtpVerificationFragment otpVerificationFragment;

    public OtpVerificationModule(OtpVerificationFragment otpVerificationFragment) {
        this.otpVerificationFragment = otpVerificationFragment;
    }

    @Provides
    OtpVerificationFragment getOtpVerificationFragment() {
        return otpVerificationFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(otpVerificationFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return otpVerificationFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(otpVerificationFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(otpVerificationFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
