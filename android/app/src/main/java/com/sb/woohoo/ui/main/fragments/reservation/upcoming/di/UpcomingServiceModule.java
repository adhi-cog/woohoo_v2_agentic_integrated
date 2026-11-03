package com.sb.woohoo.ui.main.fragments.reservation.upcoming.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.reservation.upcoming.UpcomingServiceFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class UpcomingServiceModule {

    private UpcomingServiceFragment upcomingServiceFragment;

    public UpcomingServiceModule(UpcomingServiceFragment upcomingServiceFragment) {
        this.upcomingServiceFragment = upcomingServiceFragment;
    }

    @Provides
    UpcomingServiceFragment getUpcomingServiceFragment() {
        return upcomingServiceFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(upcomingServiceFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return upcomingServiceFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(upcomingServiceFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(upcomingServiceFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }
}
