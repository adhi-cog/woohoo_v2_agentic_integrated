package com.sb.woohoo.ui.main.di;

import com.sb.woohoo.ui.main.Interactors.HelperInteractor;
import com.sb.woohoo.ui.main.Interactors.NavigationInteractor;
import com.sb.woohoo.ui.main.Interactors.SpeechManager;
import com.sb.woohoo.ui.main.helpers.AnimationsHelper;
import com.sb.woohoo.ui.main.helpers.AudioHelper;
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
import com.sb.woohoo.ui.main.helpers.WakeWordHelper;
import com.sb.woohoo.ui.main.navigators.FragmentNavigator;
import com.sb.woohoo.ui.main.navigators.StateNavigator;

import dagger.Module;
import dagger.Provides;

@Module
public class InteractorModule {
    @Provides
    @MainScope
    HelperInteractor providesHelperInteractor(
            AnimationsHelper animationsHelper,
            AudioHelper audioHelper,
            BroadcastHelper broadcastHelper,
            ConnectivityHelper connectivityHelper,
            DeviceHelper deviceHelper,
            DrawerHelper drawerHelper,
            HandlerHelper handlerHelper,
            HardwareHelper hardwareHelper,
            MusicHelper musicHelper,
            TimerHelper timerHelper,
            PollyHelper pollyHelper,
            SocketHelper socketHelper,
            GSpeechRecognizerHelper gSpeechRecognizerHelper,
            LiveFeedHelper liveFeedHelper,
            WakeWordHelper wakeWordHelper) {
        return new HelperInteractor(
                animationsHelper,
                audioHelper,
                broadcastHelper,
                connectivityHelper,
                deviceHelper,
                drawerHelper,
                handlerHelper,
                hardwareHelper,
                musicHelper,
                timerHelper,
                pollyHelper,
                socketHelper,
                gSpeechRecognizerHelper,
                liveFeedHelper,
                wakeWordHelper
        );
    }

    @Provides
    @MainScope
    SpeechManager providesSpeechManager() {
        return new SpeechManager();
    }

    @Provides
    @MainScope
    NavigationInteractor providesNavigationInteractor(
            FragmentNavigator fragmentNavigator,
            StateNavigator stateNavigator) {
        return new NavigationInteractor(fragmentNavigator, stateNavigator);
    }
}
