package com.sb.woohoo.ui.main.fragments.livefeedaudio.di;

import android.content.Context;
import android.media.AudioManager;

import com.google.gson.Gson;
import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.ui.main.fragments.livefeedaudio.LiveFeedAudioFragment;
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
public class LiveFeedAudioModule {

    LiveFeedAudioFragment liveFeedAudioFragment;

    public LiveFeedAudioModule(LiveFeedAudioFragment liveFeedAudioFragment) {
        this.liveFeedAudioFragment = liveFeedAudioFragment;
    }

    @Provides
    public AudioManager getAudioManager() {
        return (AudioManager) Objects.requireNonNull(liveFeedAudioFragment.getContext()).getSystemService(Context.AUDIO_SERVICE);
    }

    @Provides
    CameraCapturerCompat getCamera() {
        return WooHooApp.get(liveFeedAudioFragment.getParentActivity()).getWooHooAppComponent().getCamerSource();
    }

    @Provides
    Gson getGson() {
        return WooHooApp.get(liveFeedAudioFragment.getParentActivity()).getWooHooAppComponent().getGson();
    }

    @Provides
    Retrofit getRetrofit() {
        return WooHooApp.get(liveFeedAudioFragment.getParentActivity()).getWooHooAppComponent().getRetrofit();
    }

    @Provides
    LiveFeedAudioFragment getLiveFeedAudioFragment() {
        return liveFeedAudioFragment;
    }

    @Provides
    MediaPlayerUtil getRingtone() {
        return new MediaPlayerUtil(liveFeedAudioFragment.getContext(), R.raw.short_notice);
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(liveFeedAudioFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(liveFeedAudioFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }

    @Provides
    public SharedPrefsHelper getSharedHelper() {
        return WooHooApp.get(liveFeedAudioFragment.getParentActivity()).getWooHooAppComponent().getSharedPrefsHelper();
    }
}