package com.sb.woohoo.ui.main.fragments.fnb.categories.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.covid.CovidFragment;
import com.sb.woohoo.ui.main.fragments.fnb.categories.FnBCategoriesFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class FnBCategoriesModule {

    private FnBCategoriesFragment fnBCategoriesFragment;

    public FnBCategoriesModule(FnBCategoriesFragment fnBCategoriesFragment) {
        this.fnBCategoriesFragment = fnBCategoriesFragment;
    }

    @Provides
    FnBCategoriesFragment getFnBCategoriesFragment() {
        return fnBCategoriesFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(fnBCategoriesFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return fnBCategoriesFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(fnBCategoriesFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(fnBCategoriesFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }
}
