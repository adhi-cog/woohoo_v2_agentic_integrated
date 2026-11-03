package com.sb.woohoo.ui.main.fragments.music.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.music.MusicFragment;
import com.sb.woohoo.ui.main.fragments.music.MusicPresenter;
import com.sb.woohoo.ui.main.fragments.music.MusicRepository;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

/**
 * Module for {@link MusicFragment}.
 *
 * @author Ritwik Jamuar.
 */
@Module
public class MusicModule {
    private MusicFragment mFragment;

    public MusicModule(MusicFragment fragment) {
        this.mFragment = fragment;
    }

    @Provides
    public MusicPresenter providesMusicPresenter(MusicRepository repository) {
        return new MusicPresenter(mFragment, repository);
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    public MusicFragment providesMusicFragment() {
        return mFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
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