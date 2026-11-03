package com.sb.woohoo.ui.main.fragments.buysim.buypackage.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.buysim.buypackage.BuySimFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class BuySimModule {

    private BuySimFragment buySimFragment;

    public BuySimModule(BuySimFragment buySimFragment) {
        this.buySimFragment = buySimFragment;
    }

    @Provides
    BuySimFragment getTvRemoteFragment() {
        return buySimFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(buySimFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return buySimFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(buySimFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(buySimFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
