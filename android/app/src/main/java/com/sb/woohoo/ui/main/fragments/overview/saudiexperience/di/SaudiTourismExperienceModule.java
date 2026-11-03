package com.sb.woohoo.ui.main.fragments.overview.saudiexperience.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.overview.saudiexperience.SaudiTourismExperienceFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class SaudiTourismExperienceModule {

    private SaudiTourismExperienceFragment saudiTourismExperienceFragment;

    public SaudiTourismExperienceModule(SaudiTourismExperienceFragment saudiTourismExperienceFragment) {
        this.saudiTourismExperienceFragment = saudiTourismExperienceFragment;
    }

    @Provides
    SaudiTourismExperienceFragment getSaudiTourismExperienceFragmentFragment() {
        return saudiTourismExperienceFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(saudiTourismExperienceFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return saudiTourismExperienceFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(saudiTourismExperienceFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(saudiTourismExperienceFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }
}
