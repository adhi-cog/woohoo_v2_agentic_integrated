package com.sb.woohoo.ui.main.fragments.newstext.detail.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.ui.main.fragments.newstext.detail.NewsDetailFragment;

import dagger.Module;
import dagger.Provides;

@Module
public class NewsDetailModule {

    NewsDetailFragment newsDetailFragment;

    public NewsDetailModule(NewsDetailFragment newsDetailFragment) {
        this.newsDetailFragment = newsDetailFragment;
    }

    @Provides
    NewsDetailFragment getNewsDetailFragment() {
        return newsDetailFragment;
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(newsDetailFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
