package com.sb.woohoo.ui.main.fragments.livefeedhub.di;

import android.content.Context;
import android.media.AudioManager;

import com.google.gson.Gson;
import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.ui.main.fragments.livefeedhub.LiveFeedToHubFragment;
import com.sb.woohoo.utils.CameraCapturerCompat;
import com.sb.woohoo.utils.others.MediaPlayerUtil;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import retrofit2.Retrofit;

@Module
public class LiveFeedToHubModule {

    private LiveFeedToHubFragment liveFeedToHubFragment;

    public LiveFeedToHubModule(LiveFeedToHubFragment liveFeedToHubFragment) {
        this.liveFeedToHubFragment = liveFeedToHubFragment;
    }

    @Provides
    public AudioManager getAudioManager() {
        return (AudioManager) liveFeedToHubFragment.getContext().getSystemService(Context.AUDIO_SERVICE);
    }


    @Provides
    CameraCapturerCompat getCamera() {
        // FIXME : Should use same instance created in MainModule.
        return WooHooApp.get(liveFeedToHubFragment.getParentActivity()).getWooHooAppComponent().getCamerSource();
    }

    @Provides
    Gson getGson() {
        return WooHooApp.get(liveFeedToHubFragment.getActivity()).getWooHooAppComponent().getGson();
    }

    @Provides
    Retrofit getRetrofit() {
        return WooHooApp.get(liveFeedToHubFragment.getActivity()).getWooHooAppComponent().getRetrofit();
    }

    @Provides
    LiveFeedToHubFragment getLiveFeedToHubFragment() {
        return liveFeedToHubFragment;
    }

    @Provides
    MediaPlayerUtil getRingtone() {
        return new MediaPlayerUtil(liveFeedToHubFragment.getContext(), R.raw.short_notice);
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(liveFeedToHubFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(liveFeedToHubFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }

    @Provides
    public SharedPrefsHelper getSharedHelper() {
        return WooHooApp.get(liveFeedToHubFragment.getParentActivity()).getWooHooAppComponent().getSharedPrefsHelper();
    }
}
