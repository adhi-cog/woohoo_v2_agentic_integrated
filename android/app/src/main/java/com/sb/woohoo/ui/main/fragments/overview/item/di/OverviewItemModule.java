package com.sb.woohoo.ui.main.fragments.overview.item.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.overview.item.OverviewItemFragment;
import com.sb.woohoo.ui.main.fragments.overview.subcategories.OverviewSubCategoriesFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class OverviewItemModule {

    private OverviewItemFragment overviewItemFragment;

    public OverviewItemModule(OverviewItemFragment overviewItemFragment) {
        this.overviewItemFragment = overviewItemFragment;
    }

    @Provides
    OverviewItemFragment getFnBCategoriesFragment() {
        return overviewItemFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(overviewItemFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return overviewItemFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(overviewItemFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(overviewItemFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }
}
