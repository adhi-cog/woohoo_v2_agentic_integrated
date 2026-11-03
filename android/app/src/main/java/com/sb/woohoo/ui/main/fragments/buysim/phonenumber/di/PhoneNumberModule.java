package com.sb.woohoo.ui.main.fragments.buysim.phonenumber.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.buysim.buypackage.BuySimFragment;
import com.sb.woohoo.ui.main.fragments.buysim.phonenumber.PhoneNumberFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class PhoneNumberModule {

    private PhoneNumberFragment phoneNumberFragment;

    public PhoneNumberModule(PhoneNumberFragment phoneNumberFragment) {
        this.phoneNumberFragment = phoneNumberFragment;
    }

    @Provides
    PhoneNumberFragment getPhoneNumberFragment() {
        return phoneNumberFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(phoneNumberFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return phoneNumberFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(phoneNumberFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(phoneNumberFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
