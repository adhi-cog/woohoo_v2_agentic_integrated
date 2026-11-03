package com.sb.woohoo.ui.main.fragments.livefeed.di;

import android.content.Context;
import android.media.AudioManager;

import com.google.gson.Gson;
import com.sb.woohoo.ContextModule;
import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsModule;
import com.sb.woohoo.ui.main.fragments.livefeed.LiveFeedFragment;
import com.sb.woohoo.utils.CameraCapturerCompat;
import com.sb.woohoo.utils.others.MediaPlayerUtil;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import retrofit2.Retrofit;

/**
 * Created by Satyaraj on 3/17/2018.
 */

@Module
public class LiveFeedModule {

    LiveFeedFragment liveFeedFragment;

    public LiveFeedModule(LiveFeedFragment liveFeedFragment) {
        this.liveFeedFragment = liveFeedFragment;
    }

    @Provides
    public AudioManager getAudioManager() {
        return (AudioManager) Objects.requireNonNull(liveFeedFragment.getContext()).getSystemService(Context.AUDIO_SERVICE);
    }

    @Provides
    CameraCapturerCompat getCamera() {
        return WooHooApp.get(liveFeedFragment.getParentActivity()).getWooHooAppComponent().getCamerSource();
    }

    @Provides
    Gson getGson() {
        return WooHooApp.get(liveFeedFragment.getParentActivity()).getWooHooAppComponent().getGson();
    }

    @Provides
    Retrofit getRetrofit() {
        return WooHooApp.get(liveFeedFragment.getParentActivity()).getWooHooAppComponent().getRetrofit();
    }

    @Provides
    LiveFeedFragment getLiveFeedFragment() {
        return liveFeedFragment;
    }

    @Provides
    MediaPlayerUtil getRingtone() {
        return new MediaPlayerUtil(liveFeedFragment.getContext(), R.raw.short_notice);
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(liveFeedFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(liveFeedFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }

    @Provides
    public SharedPrefsHelper getSharedHelper() {
        return WooHooApp.get(liveFeedFragment.getParentActivity()).getWooHooAppComponent().getSharedPrefsHelper();
    }
}