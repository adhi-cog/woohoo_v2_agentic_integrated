package com.sb.woohoo.ui.main.fragments.overview.saudipackages.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.overview.saudipackages.SaudiTourismPackagesFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class SaudiTourismPackagesModule {

    private SaudiTourismPackagesFragment saudiTourismPackagesFragment;

    public SaudiTourismPackagesModule(SaudiTourismPackagesFragment saudiTourismFragment) {
        this.saudiTourismPackagesFragment = saudiTourismFragment;
    }

    @Provides
    SaudiTourismPackagesFragment getSaudiTourismPackagesFragmentFragment() {
        return saudiTourismPackagesFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(saudiTourismPackagesFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return saudiTourismPackagesFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(saudiTourismPackagesFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(saudiTourismPackagesFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }
}
