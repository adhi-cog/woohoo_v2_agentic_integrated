package com.sb.woohoo.ui.main.fragments.overview.saudi.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.overview.saudi.SaudiTourismFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class SaudiTourismModule {

    private SaudiTourismFragment saudiTourismFragment;

    public SaudiTourismModule(SaudiTourismFragment saudiTourismFragment) {
        this.saudiTourismFragment = saudiTourismFragment;
    }

    @Provides
    SaudiTourismFragment getSaudiTourismFragmentFragment() {
        return saudiTourismFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(saudiTourismFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return saudiTourismFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(saudiTourismFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(saudiTourismFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }
}
