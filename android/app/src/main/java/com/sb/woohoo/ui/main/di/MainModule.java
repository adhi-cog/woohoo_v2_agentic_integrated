package com.sb.woohoo.ui.main.di;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.services.polly.AmazonPollyPresigningClient;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.sb.woohoo.BuildConfig;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.ui.main.IntentProcessor;
import com.sb.woohoo.ui.main.Interactors.HelperInteractor;
import com.sb.woohoo.ui.main.Interactors.NavigationInteractor;
import com.sb.woohoo.ui.main.Interactors.SpeechManager;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.MainPresenter;
import com.sb.woohoo.ui.main.MainRepository;
import com.sb.woohoo.utils.Constants;
import com.sb.woohoo.utils.gps.GPSLocation;
import com.sb.woohoo.utils.voice.PollyTextToSpeech;
import com.squareup.picasso.Picasso;

import java.net.URISyntaxException;

import androidx.fragment.app.FragmentManager;
import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;

/**
 * Created by Shrey on 10/03/18.
 */

@Module(includes = {HelperModule.class, NavigatorModule.class, InteractorModule.class, HardwareModule.class})
public class MainModule {
    private MainActivity mainActivity;

    public MainModule(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Provides
    @MainScope
    MainActivity providesMainActivity() {
        return mainActivity;
    }

    @Provides
    @MainScope
    Context getContext() {
        return mainActivity;
    }

    /*************************************** {@link MainActivity} Dependencies ****************************************/

    @Provides
    @MainScope
    GPSLocation providesGPSLocation() {
        return new GPSLocation(mainActivity);
    }

    @Provides
    @MainScope
    MainPresenter providesMainPresenter(
            MainRepository mainRepository,
            IntentProcessor intentProcessor,
            HelperInteractor helperInteractor,
            SpeechManager speechManager,
            NavigationInteractor navigationInteractor) {
        return new MainPresenter(
                mainActivity,
                mainRepository,
                intentProcessor,
                helperInteractor,
                speechManager,
                navigationInteractor);
    }

    /*************************************** {@link MainPresenter} Dependencies ***************************************/

    @Provides
    @MainScope
    MainRepository providesMainRepository(WooHooApiCall wooHooApiCall,
                                          SharedPrefsHelper prefsHelper,
                                          CompositeDisposable compositeDisposable,
                                          Gson gson) {
        return new MainRepository(wooHooApiCall, prefsHelper, compositeDisposable, gson);
    }

    @Provides
    @MainScope
    IntentProcessor providesIntentProcessor() {
        return new IntentProcessor();
    }

    /********************************************** Common Dependencies ***********************************************/

    @Provides
    @MainScope
    FragmentManager providesFragmentManager() {
        return mainActivity.getSupportFragmentManager();
    }

    @Provides
    @MainScope
    SoundPool providesSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes =
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build();

            return (new SoundPool.Builder().setAudioAttributes(attributes).build());
        } else {
            return (new SoundPool(10, AudioManager.STREAM_NOTIFICATION, 0));
        }
    }

    @Provides
    @MainScope
    AudioManager getAudioManager() {
        return (AudioManager) mainActivity.getSystemService(Context.AUDIO_SERVICE);
    }

    @Provides
    @MainScope
    AmazonPollyPresigningClient providesAmazonPollyPresigningClient() {
        CognitoCachingCredentialsProvider credentialsProvider =
                new CognitoCachingCredentialsProvider(
                        mainActivity,
                        PollyTextToSpeech.COGNITO_POOL_ID,
                        PollyTextToSpeech.MY_REGION
                );
        return new AmazonPollyPresigningClient(credentialsProvider);
    }

    @Provides
    @MainScope
    MediaPlayer providesMediaPlayer() {
        return new MediaPlayer();
    }

    @Provides
    @MainScope
    PollyTextToSpeech providesPollyTextToSpeech(
            AmazonPollyPresigningClient amazonPollyPresigningClient,
            MediaPlayer mediaPlayer) {
        return PollyTextToSpeech.getInstance(amazonPollyPresigningClient, mediaPlayer, mainActivity);
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }
}