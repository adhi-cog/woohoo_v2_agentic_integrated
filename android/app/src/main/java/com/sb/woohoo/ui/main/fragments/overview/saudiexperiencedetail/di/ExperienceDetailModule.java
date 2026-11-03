package com.sb.woohoo.ui.main.fragments.overview.saudiexperiencedetail.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.overview.saudiexperiencedetail.ExperienceDetailFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class ExperienceDetailModule {

    private ExperienceDetailFragment experienceDetailFragment;

    public ExperienceDetailModule(ExperienceDetailFragment itemDetailFragment) {
        this.experienceDetailFragment = itemDetailFragment;
    }

    @Provides
    ExperienceDetailFragment getExperienceDetailFragment() {
        return experienceDetailFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(experienceDetailFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return experienceDetailFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(experienceDetailFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(experienceDetailFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }
}
