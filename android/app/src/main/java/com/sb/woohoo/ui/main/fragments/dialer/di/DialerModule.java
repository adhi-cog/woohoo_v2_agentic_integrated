package com.sb.woohoo.ui.main.fragments.dialer.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.dialer.DialerFragment;
import com.sb.woohoo.ui.main.fragments.dialer.DialerPresenter;
import com.sb.woohoo.ui.main.fragments.dialer.DialerRepository;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

/**
 * Module for {@link DialerFragment}.
 *
 * @author Ritwik Jamuar.
 */
@Module
public class DialerModule {
    private DialerFragment mFragment;

    public DialerModule(DialerFragment fragment) {
        this.mFragment = fragment;
    }

    @Provides
    public DialerFragment getDialerFragment() {
        return mFragment;
    }

    @Provides
    public DialerPresenter getDialerPresenter(DialerRepository repository) {
        return new DialerPresenter(mFragment, repository);
    }
    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public WooHooApiCall getWooHooApiCall() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }
    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}