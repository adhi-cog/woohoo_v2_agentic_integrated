package com.sb.woohoo.ui.main.fragments.overview.saudidestinationsdetails.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.overview.saudidestinationsdetails.DestinationsDetailFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class DestinationsDetailModule {

    private DestinationsDetailFragment destinationsDetailFragment;

    public DestinationsDetailModule(DestinationsDetailFragment destinationsDetailFragment) {
        this.destinationsDetailFragment = destinationsDetailFragment;
    }

    @Provides
    DestinationsDetailFragment getDestinationsDetailFragment() {
        return destinationsDetailFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(destinationsDetailFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return destinationsDetailFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(destinationsDetailFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(destinationsDetailFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }
}
