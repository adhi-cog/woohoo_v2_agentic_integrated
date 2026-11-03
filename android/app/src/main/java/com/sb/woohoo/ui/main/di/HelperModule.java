package com.sb.woohoo.ui.main.di;

import android.media.SoundPool;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.helpers.AnimationsHelper;
import com.sb.woohoo.ui.main.helpers.AudioHelper;
import com.sb.woohoo.ui.main.helpers.BrightnessHelper;
import com.sb.woohoo.ui.main.helpers.BroadcastHelper;
import com.sb.woohoo.ui.main.helpers.ConnectivityHelper;
import com.sb.woohoo.ui.main.helpers.DeviceHelper;
import com.sb.woohoo.ui.main.helpers.DrawerHelper;
import com.sb.woohoo.ui.main.helpers.GSpeechRecognizerHelper;
import com.sb.woohoo.ui.main.helpers.HandlerHelper;
import com.sb.woohoo.ui.main.helpers.HardwareHelper;
import com.sb.woohoo.ui.main.helpers.LiveFeedHelper;
import com.sb.woohoo.ui.main.helpers.MusicHelper;
import com.sb.woohoo.ui.main.helpers.PollyHelper;
import com.sb.woohoo.ui.main.helpers.SocketHelper;
import com.sb.woohoo.ui.main.helpers.TimerHelper;
import com.sb.woohoo.ui.main.helpers.UpdateHelper;
import com.sb.woohoo.ui.main.helpers.WakeWordHelper;
import com.sb.woohoo.utils.CameraCapturerCompat;
import com.sb.woohoo.utils.VolumeControl;
import com.sb.woohoo.utils.others.CameraUtils;
import com.sb.woohoo.utils.voice.PollyTextToSpeech;

import dagger.Module;
import dagger.Provides;
import io.socket.client.Socket;

@Module
public class HelperModule {
    @Provides
    @MainScope
    AnimationsHelper providesAnimationsHelper(MainActivity context) {
        return new AnimationsHelper(context);
    }

    @Provides
    @MainScope
    AudioHelper providesAudioHelper(SoundPool pool, MainActivity context) {
        return new AudioHelper(pool, context);
    }

    @Provides
    @MainScope
    BroadcastHelper providesBroadcastHelper() {
        return new BroadcastHelper();
    }

    @Provides
    @MainScope
    ConnectivityHelper providesConnectivityHelper() {
        return new ConnectivityHelper();
    }

    @Provides
    @MainScope
    DeviceHelper providesDeviceHelper(Gson gson, JsonParser parser) {
        return new DeviceHelper(gson, parser);
    }

    @Provides
    @MainScope
    DrawerHelper providesDrawerHelper(MainActivity context) {
        return new DrawerHelper(context);
    }


    @Provides
    @MainScope
    MusicHelper providesMusicHelper(MainActivity mainActivity) {
        return new MusicHelper(mainActivity);
    }

    @Provides
    @MainScope
    TimerHelper providesTimerHelper(MainActivity mainActivity) {
        return new TimerHelper(mainActivity);
    }

    @Provides
    @MainScope
    UpdateHelper providesUpdateHelper(MainActivity mainActivity) {
        return new UpdateHelper(mainActivity);
    }

    @Provides
    @MainScope
    public PollyHelper providesPollyHelper(PollyTextToSpeech pollyTextToSpeech) {
        return new PollyHelper(pollyTextToSpeech);
    }

    @Provides
    @MainScope
    public SocketHelper providesSocketHelper(Socket socket, Gson gson, JsonParser jsonParser, AnalyticsHelper analyticsHelper) {
        return new SocketHelper(socket, gson, jsonParser, analyticsHelper);
    }

    @Provides
    @MainScope
    public GSpeechRecognizerHelper providesGSpeechRecognizerHelper(MainActivity context) {
        return new GSpeechRecognizerHelper(context);
    }

    @Provides
    @MainScope
    public LiveFeedHelper providesLiveFeedHelper() {
        return new LiveFeedHelper();
    }

    @Provides
    @MainScope
    public WakeWordHelper providesWakeWordHelper(MainActivity context) {
        return new WakeWordHelper(context);
    }

    @Provides
    @MainScope
    BrightnessHelper providesBrightnessHandler(HandlerHelper handlerHelper, MainActivity context) {
        return new BrightnessHelper(context.getContentResolver(), handlerHelper.getForegroundHandler());
    }
}
