package com.sb.woohoo.ui.main.fragments.fnb.history.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.fnb.history.FnBHistoryFragment;
import com.sb.woohoo.ui.main.fragments.fnb.order.FnBOrderFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class FnBHistoryModule {

    private FnBHistoryFragment fnBHistoryFragment;

    public FnBHistoryModule(FnBHistoryFragment fnBHistoryFragment) {
        this.fnBHistoryFragment = fnBHistoryFragment;
    }

    @Provides
    FnBHistoryFragment getFnBHistoryFragment() {
        return fnBHistoryFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(fnBHistoryFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return fnBHistoryFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(fnBHistoryFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(fnBHistoryFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
