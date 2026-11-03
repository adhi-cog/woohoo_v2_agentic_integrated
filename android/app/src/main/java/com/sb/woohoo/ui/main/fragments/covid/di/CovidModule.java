package com.sb.woohoo.ui.main.fragments.covid.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.covid.CovidFragment;
import com.sb.woohoo.ui.main.fragments.feedback.FeedbackFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class CovidModule {

    private CovidFragment covidFragment;

    public CovidModule(CovidFragment feedbackFragment) {
        this.covidFragment = feedbackFragment;
    }

    @Provides
    CovidFragment getCovidFragment() {
        return covidFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(covidFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return covidFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(covidFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(covidFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
