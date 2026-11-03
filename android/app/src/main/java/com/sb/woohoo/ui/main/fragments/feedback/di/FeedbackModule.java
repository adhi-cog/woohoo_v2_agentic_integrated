package com.sb.woohoo.ui.main.fragments.feedback.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.feedback.FeedbackFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class FeedbackModule {

    private FeedbackFragment feedbackFragment;

    public FeedbackModule(FeedbackFragment feedbackFragment) {
        this.feedbackFragment = feedbackFragment;
    }

    @Provides
    FeedbackFragment getFeedbackFragment() {
        return feedbackFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(feedbackFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return feedbackFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(feedbackFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(feedbackFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
