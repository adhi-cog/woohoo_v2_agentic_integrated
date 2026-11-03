package com.sb.woohoo.ui.main.fragments.newsaudio.di;

import com.google.gson.Gson;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.newsaudio.NewsAudioFragment;
import com.sb.woohoo.ui.main.fragments.newsaudio.NewsAudioPresenter;
import com.sb.woohoo.ui.main.fragments.newsaudio.NewsAudioRepository;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class NewsAudioModule {
    private NewsAudioFragment mFragment;

    public NewsAudioModule(NewsAudioFragment fragment) {
        this.mFragment = fragment;
    }

    @Provides
    NewsAudioFragment providesNewsAudioFragment() {
        return mFragment;
    }

    /*********************************** Presenter Dependencies ***********************************/

    @Provides
    NewsAudioPresenter providesNewsAudioPresenter(NewsAudioRepository repository) {
        return new NewsAudioPresenter(mFragment, repository);
    }

    /*********************************** Repository Dependencies **********************************/

    @Provides
    WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Gson providesGson() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getGson();
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