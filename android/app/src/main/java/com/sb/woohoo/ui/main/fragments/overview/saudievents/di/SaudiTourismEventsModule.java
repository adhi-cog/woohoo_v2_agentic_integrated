package com.sb.woohoo.ui.main.fragments.overview.saudievents.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.overview.saudievents.SaudiTourismEventsFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class SaudiTourismEventsModule {

    private SaudiTourismEventsFragment saudiTourismEventsFragment;

    public SaudiTourismEventsModule(SaudiTourismEventsFragment saudiTourismFragment) {
        this.saudiTourismEventsFragment = saudiTourismFragment;
    }

    @Provides
    SaudiTourismEventsFragment getSaudiTourismEventsFragmentFragment() {
        return saudiTourismEventsFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(saudiTourismEventsFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return saudiTourismEventsFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(saudiTourismEventsFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(saudiTourismEventsFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }
}
