package com.sb.woohoo.ui.main.fragments.overview.categories.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.fnb.categories.FnBCategoriesFragment;
import com.sb.woohoo.ui.main.fragments.overview.categories.OverviewCategoriesFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class OverviewCategoriesModule {

    private OverviewCategoriesFragment overviewCategoriesFragment;

    public OverviewCategoriesModule(OverviewCategoriesFragment overviewCategoriesFragment) {
        this.overviewCategoriesFragment = overviewCategoriesFragment;
    }

    @Provides
    OverviewCategoriesFragment getFnBCategoriesFragment() {
        return overviewCategoriesFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(overviewCategoriesFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return overviewCategoriesFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(overviewCategoriesFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(overviewCategoriesFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }
}
