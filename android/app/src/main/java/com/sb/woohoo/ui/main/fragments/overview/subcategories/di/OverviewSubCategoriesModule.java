package com.sb.woohoo.ui.main.fragments.overview.subcategories.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.overview.subcategories.OverviewSubCategoriesFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class OverviewSubCategoriesModule {

    private OverviewSubCategoriesFragment overviewSubCategoriesFragment;

    public OverviewSubCategoriesModule(OverviewSubCategoriesFragment overviewSubCategoriesFragment) {
        this.overviewSubCategoriesFragment = overviewSubCategoriesFragment;
    }

    @Provides
    OverviewSubCategoriesFragment getFnBCategoriesFragment() {
        return overviewSubCategoriesFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(overviewSubCategoriesFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return overviewSubCategoriesFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(overviewSubCategoriesFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(overviewSubCategoriesFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }
}
