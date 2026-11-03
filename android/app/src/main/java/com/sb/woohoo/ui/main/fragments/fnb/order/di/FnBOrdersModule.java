package com.sb.woohoo.ui.main.fragments.fnb.order.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.fnb.categories.FnBCategoriesFragment;
import com.sb.woohoo.ui.main.fragments.fnb.order.FnBOrderFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class FnBOrdersModule {

    private FnBOrderFragment fnBOrderFragment;

    public FnBOrdersModule(FnBOrderFragment fnBOrderFragment) {
        this.fnBOrderFragment = fnBOrderFragment;
    }

    @Provides
    FnBOrderFragment getFnBOrdersFragment() {
        return fnBOrderFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(fnBOrderFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return fnBOrderFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(fnBOrderFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(fnBOrderFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
