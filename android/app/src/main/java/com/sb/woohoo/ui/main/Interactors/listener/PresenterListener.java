package com.sb.woohoo.ui.main.Interactors.listener;

import android.content.Context;
import android.content.Intent;

import com.google.gson.JsonObject;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainPresenter;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.Light;
import com.sb.woohoo.ui.main.pojo.DrawerGridItems;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.pojo.template.TemplateData;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;

import java.io.IOException;
import java.util.List;

public interface PresenterListener {

    interface Helper {

        void setListener(MainPresenter mainPresenter);

        void registerCallbacks(String lang);

        void initializeComponents(Context context);

        void cleanUp(Context context);

        void runWakeWordRecognition();

        void toggleSlidingTrayVisibility(boolean b);

        void toggleSlidingTrayBottomVisibility(boolean b);

        void emitEventToServer(String changeDeviceState, JsonObject jsonObject);

        void stopWakeWordAnimation(boolean needTorunWakeWordRecobnition);

        void onWakeWordChanged();

        void setFollowUp(boolean b);

        boolean getCameraStatus();

        void pausePlayingMusic();

        void stopWakeWordRecognition();

        void removeWakeWordHandler();

        boolean isMusicPlaying();

        void closeDrawer();

        void onVolumeUpPressed();

        void onVolumeDownPressed();

        void startAsrRecording();

        void seekMusic(int position);

        void stopPlayingMusic();

        void setMusicRepeat(boolean repeat);

        void resumePlayingMusic();

        void nextSong();

        void previousSong();

        void terminateMusicPlayer();

        void playMusic(String spotifyURI);

        void setSpotifyPlayer(Context context, String token);

        void rotateCamera(boolean rotate, String hubId,String receverUserId, int angle, int isRotating);

        boolean isMusicPlayerInitialized();

        boolean isMusicPlayerTerminated();

        boolean isSpotifyUserLoggedIn();

        boolean isMusicRepeating();

        int getMusicCurrentPosition();

        Metadata getMusicMetaData();

        void controlSystemVolume(IntentInfo intentInfo);

        int getBrightness();

        void setBrightness(int brightness);

        void startSpeaking(String text, int streamType, int callbackType, String lang);

        void stopSpeaking(boolean needCallback);

        void restoreVolume(int audioStream);

        void stopAsr();

        void overlayAnimation(boolean b);

        void stopTimer();

        void showMuteBar(boolean aIsNetworkPresent);

        void hideMuteBar(boolean aIsNetworkPresent);

        void showAllSoundBar();

        void hideTopDrawerItem();
    }

    interface Navigator {

        void registerCallbacks();

        void registerForFragmentChanges();

        void setNavigationListener(MainPresenter mainPresenter);

        void openHomeFragment();

        void switchFragmentUsingAdd(FeatureFragment featureFragment);

        void switchFragmentUsingAdd(FeatureFragment featureFragmentLeft, FeatureFragment featureFragmentRight);

        void switchFragmentUsingReplace(FeatureFragment featureFragment);

        void handleIntents(IntentInfo intentInfo);

        void handleWebview(String url);

        void handleTemplate(TemplateData templateData);

        void startSurveillance(String twilioToken);

        void stopPlayingVideo();

        void stopRingingAlarm();

        void resumePlayingVideo();

        void onHomePressed();

        boolean isFragmentStacked();

        void onSpotifyMusicLoggedIn();

        void onSpotifyMusicLoginFailed();

        void onSpotifyMusicLoggedOut();

        void onSpotifyMusicPlaybackError(Error error);

        void onSpotifyMusicPlaying();

        void onSpotifyMusicPaused();

        void onSpotifyMusicNextTrack();

        void onSpotifyMusicPreviousTrack();

        void onSpotifyMusicShuffleOn();

        void onSpotifyMusicShuffleOff();

        void onSpotifyMusicRepeatOn();

        void onSpotifyMusicRepeatOff();

        void onSpotifyMusicStopped();

        void onSpotifyMusicTrackChanged();

        void onSpotifyMusicTrackDelivered();

        void onSpotifyMusicMetaDataChanged();

        void onSpotifyMusicTakeData(Metadata metadata);

        void onSpotifyMusicActionCompleted();

        void onSpotifyMusicActionCancelled();

        void handleSpeechNewsCallback(boolean isDetailedNews);

        void onDropInCallAccepted();

        void alertAlarm(Intent intent, String key) throws IOException, ClassNotFoundException;

        void snoozePop();

        void updateScreenSaverTime(int time);

        void pausePop();

        void onLightStateChanged(Light light);

        void changeFragmentOnError(FeatureFragment featureFragment);

        void onDeviceNormal();

        void onDropInCallRejected();

    }

    interface Speech {

        void setListener(MainPresenter mainPresenter);

        void stopPolly(boolean b);

        void speakUsingPolly(String response, int streamMusic, int callbackAutoClose);
    }
}
