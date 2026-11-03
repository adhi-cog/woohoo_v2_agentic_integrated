package com.sb.woohoo.ui.main.fragments.home.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.home.HomeFragment;
import com.sb.woohoo.ui.main.fragments.home.HomePresenter;
import com.sb.woohoo.ui.main.fragments.home.HomeRepository;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

/**
 * Created by Shrey on 18/03/18.
 */

@Module
public class HomeModule {
    private HomeFragment mFragment;

    public HomeModule(HomeFragment fragment) {
        this.mFragment = fragment;
    }

    @Provides
    public HomeFragment getContactsFragment() {
        return mFragment;
    }

    @Provides
    public HomePresenter getHomePresenter(HomeRepository repository) {
        return new HomePresenter(mFragment, repository);
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }

    @Provides
    public WooHooApiCall getWooHooApiCall() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }
}