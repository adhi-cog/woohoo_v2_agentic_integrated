package com.sb.woohoo.ui.main.fragments.newstext.headline.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.newstext.headline.NewsHeadlineFragment;
import com.squareup.picasso.Picasso;

import androidx.fragment.app.Fragment;
import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class NewsHeadlineModule {

    private NewsHeadlineFragment newsHeadlineFragment;

    public NewsHeadlineModule(NewsHeadlineFragment newsHeadlineFragment) {
        this.newsHeadlineFragment = newsHeadlineFragment;
    }

    @Provides
    NewsHeadlineFragment getNewsHeadlineFragment() {
        return newsHeadlineFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(newsHeadlineFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return newsHeadlineFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(newsHeadlineFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(newsHeadlineFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
