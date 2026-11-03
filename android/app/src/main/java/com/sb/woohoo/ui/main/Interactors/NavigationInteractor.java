package com.sb.woohoo.ui.main.Interactors;

import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Interactors.listener.NavigationListener;
import com.sb.woohoo.ui.main.Interactors.listener.PresenterListener;
import com.sb.woohoo.ui.main.MainPresenter;
import com.sb.woohoo.ui.main.fragments.devices.LightConstants;
import com.sb.woohoo.ui.main.fragments.devices.huecontrol.PhilipsHueFragment;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.Light;
import com.sb.woohoo.ui.main.fragments.home.HomeFragment;
import com.sb.woohoo.ui.main.fragments.livefeed.LiveFeedFragment;
import com.sb.woohoo.ui.main.fragments.livefeedhub.LiveFeedToHubFragment;
import com.sb.woohoo.ui.main.fragments.livefeedhubaudio.LiveFeedToHubAudioFragment;
import com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceon.SurveillanceFragment;
import com.sb.woohoo.ui.main.navigators.FragmentNavigator;
import com.sb.woohoo.ui.main.navigators.StateNavigator;
import com.sb.woohoo.ui.main.pojo.DrawerGridItems;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.pojo.template.TemplateData;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

public class NavigationInteractor implements PresenterListener.Navigator {
    // Constants.
    private static final String TAG = NavigationInteractor.class.getSimpleName();
    private FragmentNavigator mFragmentNavigator;
    private StateNavigator mStateNavigator;
    private NavigationListener navigationListener;
    private FragmentNavigator.FragmentNavigatorCallback mFragmentListener = new FragmentNavigator.FragmentNavigatorCallback() {
        @Override
        public void resetNlpContext() {
            navigationListener.resetNlpContext();
        }

        @Override
        public void onBackNavigation() {
            navigationListener.handleBackPressed();
        }

        @Override
        public void changeHintBar(int hintText, int backgroundColour, int visibility) {
            navigationListener.changeHintBar(hintText, backgroundColour, visibility);
        }
    };
    private StateNavigator.SwitchFragmentCallback mStateListener = new StateNavigator.SwitchFragmentCallback() {
        @Override
        public void changeHintBar(int hintText, int backgroundColour, int visibility) {
            navigationListener.changeHintBar(hintText, backgroundColour, visibility);
        }

        @Override
        public void changeFragmentStackUsingAdd(FeatureFragment featureFragment) {
            mFragmentNavigator.changeFragmentUsingAdd(featureFragment);
        }

        @Override
        public void changeFragmentStackUsingReplace(FeatureFragment featureFragment) {
            mFragmentNavigator.changeFragmentUsingReplace(featureFragment);
        }

        @Override
        public boolean musicIsPlaying() {
            return navigationListener.isMusicPlaying();
        }

        @Override
        public void pauseMusic() {
            navigationListener.pauseMusic();
        }

        @Override
        public void nextMusic() {
            navigationListener.nextMusic();
        }

        @Override
        public void previousMusic() {
            navigationListener.previousMusic();
        }

        @Override
        public void resumeMusic() {
            navigationListener.resumeMusic();
        }

        @Override
        public void stopMusic() {
            navigationListener.stopMusic();
        }

        @Override
        public void handleBackNavigation() {
            navigationListener.handleBackPressed();
        }

        @Override
        public void onResponseToUser(Object response) {
            navigationListener.onResponseToUser(response);
        }

        @Override
        public void controlVolume(IntentInfo intentInfo) {
            navigationListener.controlVolume(intentInfo);
        }

        @Override
        public Fragment getCurrentFragment() {
            return mFragmentNavigator.getCurrentFragment();
        }

        @Override
        public int getBackStackEntryCount() {
            return mFragmentNavigator.getBackStackEntryCount();
        }

        @Override
        public void popBackStackImmediate() {
            mFragmentNavigator.popBackStackImmediate();
        }

        @Override
        public void resetContext() {
            navigationListener.resetNlpContext();
        }

        @Override
        public void controlTimerStop() {
            navigationListener.stopTimer();
        }

    };

    @Inject
    public NavigationInteractor(FragmentNavigator fragmentNavigator, StateNavigator stateNavigator) {
        LoggerWrapper.i(NavigationInteractor.class.getSimpleName(), "Constructor");
        this.mFragmentNavigator = fragmentNavigator;
        this.mStateNavigator = stateNavigator;
    }

    @Override
    public void registerCallbacks() {
        mStateNavigator.setStateListener(mStateListener);
        mFragmentNavigator.setFragmentListener(mFragmentListener);
    }

    @Override
    public void setNavigationListener(MainPresenter listener) {
        this.navigationListener = listener;
    }

    @Override
    public void registerForFragmentChanges() {
        mFragmentNavigator.setOnBackStackChangeListener();
    }

//    @Override
//    public void unRegisterFragmentChanges() {
//        mFragmentNavigator.stopCloseDelay();
//    }

    @Override
    public void handleIntents(IntentInfo intentInfo) {
        mStateNavigator.handleFragmentState(intentInfo);
    }

    @Override
    public void handleWebview(String url) {
        mStateNavigator.handleWebView(url);
    }

    @Override
    public void handleTemplate(TemplateData templateData) {
        mStateNavigator.handleTemplateState(templateData);
    }

    @Override
    public void openHomeFragment() {
        mStateNavigator.handleFragmentNavigationUsingAdd(HomeFragment.newInstance());
    }

    @Override
    public void switchFragmentUsingAdd(FeatureFragment fragment) {
        mStateNavigator.handleFragmentNavigationUsingAdd(fragment);
    }

    @Override
    public void switchFragmentUsingAdd(FeatureFragment leftFragment, FeatureFragment rightFragment) {
        //mStateNavigator.handleFragmentNavigationUsingAdd(leftFragment, rightFragment);
        mFragmentNavigator.changeFragmentUsingAdd(leftFragment, rightFragment);
    }

    @Override
    public void switchFragmentUsingReplace(FeatureFragment fragment) {
        mStateNavigator.handleFragmentNavigationUsingReplace(fragment);
    }

    @Override
    public void resumePlayingVideo() {
        mStateNavigator.resumePlayingVideo();
    }

    @Override
    public void stopPlayingVideo() {
        mStateNavigator.stopPlayingVideo();
    }

    @Override
    public void stopRingingAlarm() {
        mStateNavigator.stopRingingAlarm();
    }

    @Override
    public boolean isFragmentStacked() {
        return mFragmentNavigator.isFragmentStacked();
    }

    @Override
    public void onHomePressed() {
        mStateNavigator.onHomePressed();
    }


    @Override
    public void startSurveillance(String twilioToken) {
        Fragment fragment = mFragmentNavigator.getCurrentFragment();
        if (fragment instanceof SurveillanceFragment) {
            ((SurveillanceFragment) fragment).initSurveillanceFeed(twilioToken);
        }
    }

    @Override
    public void onSpotifyMusicLoggedIn() {
        mStateNavigator.onSpotifyMusicLoggedIn();
    }

    @Override
    public void onSpotifyMusicLoginFailed() {
        mStateNavigator.onSpotifyMusicLoginFailed();
    }

    @Override
    public void onSpotifyMusicLoggedOut() {
        mStateNavigator.onSpotifyMusicLoggedOut();
    }

    @Override
    public void onSpotifyMusicPlaybackError(Error error) {
        mStateNavigator.onSpotifyMusicPlaybackError(error);
    }

    @Override
    public void onSpotifyMusicPlaying() {
        mStateNavigator.onSpotifyMusicPlaying();
    }

    @Override
    public void onSpotifyMusicPaused() {
        mStateNavigator.onSpotifyMusicPaused();
    }

    @Override
    public void onSpotifyMusicNextTrack() {
        mStateNavigator.onSpotifyMusicNextTrack();
    }

    @Override
    public void onSpotifyMusicPreviousTrack() {
        mStateNavigator.onSpotifyMusicPreviousTrack();
    }

    @Override
    public void onSpotifyMusicShuffleOn() {
        mStateNavigator.onSpotifyMusicShuffleOn();
    }

    @Override
    public void onSpotifyMusicShuffleOff() {
        mStateNavigator.onSpotifyMusicShuffleOff();
    }

    @Override
    public void onSpotifyMusicRepeatOn() {
        mStateNavigator.onSpotifyMusicRepeatOn();
    }

    @Override
    public void onSpotifyMusicRepeatOff() {
        mStateNavigator.onSpotifyMusicRepeatOff();
    }

    @Override
    public void onSpotifyMusicStopped() {
        mStateNavigator.onSpotifyMusicStopped();
    }

    @Override
    public void onSpotifyMusicTrackChanged() {
        mStateNavigator.onSpotifyMusicTrackChanged();
    }

    @Override
    public void onSpotifyMusicTrackDelivered() {
        mStateNavigator.onSpotifyMusicTrackDelivered();
    }

    @Override
    public void onSpotifyMusicMetaDataChanged() {
        mStateNavigator.onSpotifyMusicMetaDataChanged();
    }

    @Override
    public void onSpotifyMusicTakeData(Metadata metadata) {
        mStateNavigator.onSpotifyMusicTakeData(metadata);
    }

    @Override
    public void onSpotifyMusicActionCompleted() {
        mStateNavigator.onSpotifyMusicActionCompleted();
    }

    @Override
    public void onSpotifyMusicActionCancelled() {
        mStateNavigator.onSpotifyMusicActionCancelled();
    }

    @Override
    public void handleSpeechNewsCallback(boolean isDetailedNews) {
        mStateNavigator.handleNewsSpeechCallback(isDetailedNews);
    }

    @Override
    public void onDropInCallAccepted() {
        Fragment fragment = mFragmentNavigator.getCurrentFragment();
        if (fragment instanceof LiveFeedToHubFragment) {
            ((LiveFeedToHubFragment) fragment).onCallAccepted();
        } else if (fragment instanceof LiveFeedToHubAudioFragment) {
            ((LiveFeedToHubAudioFragment) fragment).onCallAccepted();
        }
    }

    @Override
    public void onDropInCallRejected() {
        Fragment fragment = mFragmentNavigator.getCurrentFragment();
        if (fragment instanceof LiveFeedFragment) {
            ((LiveFeedFragment) fragment).onBackPressed();
        } else if (fragment instanceof SurveillanceFragment) {
            ((SurveillanceFragment) fragment).endSurveillanceFeed();
        } else if (fragment instanceof LiveFeedToHubFragment) {
            ((LiveFeedToHubFragment) fragment).onBackPressed();
        }
    }

    @Override
    public void alertAlarm(Intent intent, String key) throws IOException, ClassNotFoundException {
        mStateNavigator.alertAlarm(intent, key);
    }

    @Override
    public void snoozePop() {
        mFragmentNavigator.snoozePop();
    }

    @Override
    public void updateScreenSaverTime(int time) {
        mFragmentNavigator.updateScreenSaverTime(time);
    }

    @Override
    public void pausePop() {
        mFragmentNavigator.pausePop();
    }

    @Override
    public void onLightStateChanged(Light light) {
        Fragment currentFragment = mFragmentNavigator.getCurrentFragment();
        IntentInfo<Light> intentInfo = new IntentInfo<>(light, LightConstants.LightState.FROM_SOCKET);
        if (currentFragment instanceof PhilipsHueFragment) {
            ((PhilipsHueFragment) currentFragment).getPresenter().processActions(intentInfo);
        } else {
            navigationListener.changeLightState(light);
        }
    }

    @Override
    public void changeFragmentOnError(FeatureFragment featureFragment) {
        mFragmentNavigator.changeFragmentUsingAdd(featureFragment);
    }

    @Override
    public void onDeviceNormal() {
        mStateNavigator.showNotificationsIfAny();
    }
}
