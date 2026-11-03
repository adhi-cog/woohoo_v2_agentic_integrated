package com.sb.woohoo.ui.main.fragments.overview.saudipackagesdetail.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.overview.saudipackagesdetail.PackagesDetailFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class PackagesDetailModule {

    private PackagesDetailFragment packagesDetailFragment;

    public PackagesDetailModule(PackagesDetailFragment packagesDetailFragment) {
        this.packagesDetailFragment = packagesDetailFragment;
    }

    @Provides
    PackagesDetailFragment getEventDetailFragment() {
        return packagesDetailFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(packagesDetailFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return packagesDetailFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(packagesDetailFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(packagesDetailFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }
}
