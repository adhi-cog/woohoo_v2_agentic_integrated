package com.sb.woohoo.ui.main.fragments.overview.saudidestinations.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.overview.saudidestinations.SaudiTourismDestinationsFragment;
import com.sb.woohoo.ui.main.fragments.overview.saudipackages.SaudiTourismPackagesFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class SaudiTourismDestinationsModule {

    private SaudiTourismDestinationsFragment saudiTourismDestinationsFragment;

    public SaudiTourismDestinationsModule(SaudiTourismDestinationsFragment saudiTourismFragment) {
        this.saudiTourismDestinationsFragment = saudiTourismFragment;
    }

    @Provides
    SaudiTourismDestinationsFragment getSaudiTourismDestinationsFragmentFragment() {
        return saudiTourismDestinationsFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(saudiTourismDestinationsFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return saudiTourismDestinationsFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(saudiTourismDestinationsFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(saudiTourismDestinationsFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }
}
