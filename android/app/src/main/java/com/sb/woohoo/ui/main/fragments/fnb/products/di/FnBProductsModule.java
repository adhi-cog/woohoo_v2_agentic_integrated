package com.sb.woohoo.ui.main.fragments.fnb.products.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.fnb.categories.FnBCategoriesFragment;
import com.sb.woohoo.ui.main.fragments.fnb.products.FnBProductsFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class FnBProductsModule {

    private FnBProductsFragment fnBProductsFragment;

    public FnBProductsModule(FnBProductsFragment fnBProductsFragment) {
        this.fnBProductsFragment = fnBProductsFragment;
    }

    @Provides
    FnBProductsFragment getFnBProductsFragment() {
        return fnBProductsFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(fnBProductsFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return fnBProductsFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(fnBProductsFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(fnBProductsFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
