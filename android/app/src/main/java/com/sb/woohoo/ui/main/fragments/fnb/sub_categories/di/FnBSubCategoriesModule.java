package com.sb.woohoo.ui.main.fragments.fnb.sub_categories.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.fnb.categories.FnBCategoriesFragment;
import com.sb.woohoo.ui.main.fragments.fnb.sub_categories.FnBSubCategoriesFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class FnBSubCategoriesModule {

    private FnBSubCategoriesFragment fnBCategoriesFragment;

    public FnBSubCategoriesModule(FnBSubCategoriesFragment fnBCategoriesFragment) {
        this.fnBCategoriesFragment = fnBCategoriesFragment;
    }

    @Provides
    FnBSubCategoriesFragment fnBSubCategoriesFragment() {
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
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(fnBCategoriesFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
