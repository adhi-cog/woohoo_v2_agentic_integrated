package com.sb.woohoo.ui.main.fragments.remote.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.remote.TVRemoteFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class TVRemoteModule {

    private TVRemoteFragment tvRemoteFragment;

    public TVRemoteModule(TVRemoteFragment tvRemoteFragment) {
        this.tvRemoteFragment = tvRemoteFragment;
    }

    @Provides
    TVRemoteFragment getTvRemoteFragment() {
        return tvRemoteFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(tvRemoteFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return tvRemoteFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(tvRemoteFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(tvRemoteFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
