package com.sb.woohoo.ui.main.fragments.livefeedhubaudio.di;

import android.content.Context;
import android.media.AudioManager;

import com.google.gson.Gson;
import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.ui.main.fragments.livefeedhubaudio.LiveFeedToHubAudioFragment;
import com.sb.woohoo.utils.CameraCapturerCompat;
import com.sb.woohoo.utils.others.MediaPlayerUtil;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import retrofit2.Retrofit;

@Module
public class LiveFeedToHubAudioModule {

    private LiveFeedToHubAudioFragment liveFeedToHubAudioFragment;

    public LiveFeedToHubAudioModule(LiveFeedToHubAudioFragment liveFeedToHubAudioFragment) {
        this.liveFeedToHubAudioFragment = liveFeedToHubAudioFragment;
    }

    @Provides
    public AudioManager getAudioManager() {
        return (AudioManager) liveFeedToHubAudioFragment.getContext().getSystemService(Context.AUDIO_SERVICE);
    }


    @Provides
    CameraCapturerCompat getCamera() {
        // FIXME : Should use same instance created in MainModule.
        return WooHooApp.get(liveFeedToHubAudioFragment.getParentActivity()).getWooHooAppComponent().getCamerSource();
    }

    @Provides
    Gson getGson() {
        return WooHooApp.get(liveFeedToHubAudioFragment.getActivity()).getWooHooAppComponent().getGson();
    }

    @Provides
    Retrofit getRetrofit() {
        return WooHooApp.get(liveFeedToHubAudioFragment.getActivity()).getWooHooAppComponent().getRetrofit();
    }

    @Provides
    LiveFeedToHubAudioFragment getLiveFeedToHubAudioFragment() {
        return liveFeedToHubAudioFragment;
    }

    @Provides
    MediaPlayerUtil getRingtone() {
        return new MediaPlayerUtil(liveFeedToHubAudioFragment.getContext(), R.raw.short_notice);
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(liveFeedToHubAudioFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(liveFeedToHubAudioFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }

    @Provides
    public SharedPrefsHelper getSharedHelper() {
        return WooHooApp.get(liveFeedToHubAudioFragment.getParentActivity()).getWooHooAppComponent().getSharedPrefsHelper();
    }
}
