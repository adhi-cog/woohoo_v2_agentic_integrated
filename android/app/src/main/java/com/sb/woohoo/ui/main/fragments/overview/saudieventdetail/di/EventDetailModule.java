package com.sb.woohoo.ui.main.fragments.overview.saudieventdetail.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.overview.saudieventdetail.EventDetailFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class EventDetailModule {

    private EventDetailFragment eventDetailFragment;

    public EventDetailModule(EventDetailFragment itemDetailFragment) {
        this.eventDetailFragment = itemDetailFragment;
    }

    @Provides
    EventDetailFragment getEventDetailFragment() {
        return eventDetailFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(eventDetailFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return eventDetailFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(eventDetailFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(eventDetailFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }
}
