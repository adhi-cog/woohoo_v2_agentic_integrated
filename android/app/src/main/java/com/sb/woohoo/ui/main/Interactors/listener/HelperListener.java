package com.sb.woohoo.ui.main.Interactors.listener;

import android.content.Intent;

import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.Light;
import com.sb.woohoo.ui.main.fragments.feedback.pojo.Question;
import com.sb.woohoo.ui.main.fragments.screensaver.pojo.ScreenSaverMulti;
import com.sb.woohoo.ui.main.pojo.RemoteAlertModel;
import com.sb.woohoo.ui.main.pojo.RemoteSystemUpdateModel;
import com.sb.woohoo.ui.main.pojo.socketevent.HotelCheckInResponse;
import com.sb.woohoo.ui.main.pojo.socketevent.HotelCheckOutResponse;
import com.sb.woohoo.ui.main.pojo.socketevent.LiveFeedParticipantModel;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;

import java.io.IOException;
import java.util.ArrayList;

public interface HelperListener {

    void onBackNavigation();

    void processQuery(String query);

    void processQuery(String query, String lang);

    void getTwilioToken(LiveFeedParticipantModel model);

    void dropInCallAccepted();

    void dropInCallRejected();

    void onSpotifyUserLoggedIn();

    void onSpotifyUserLoginFailed();

    void onSpotifyUserLoggedOut();

    void onSpotifyPlayerPlaybackError(Error error);

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

    void onTakeMetaData(Metadata metadata);

    void onSpotifyMusicActionCompleted();

    void onSpotifyMusicActionCancelled();

    void onSpeechNewsCallback(boolean isDetailedNews);

    void onLightSocketEvent(Light light);

    void onSurveillanceModeChanged(boolean state);

    void onAlert(Intent intent, String key) throws IOException, ClassNotFoundException;

    void onActivityIdle();

    void onActivityDetected();

    void checkAndUpdateStatus();

    void updateHomeScreen();

    void updateHomeScreenContacts();

    void onSystemAlert(RemoteAlertModel remoteAlertModel);

    void onSystemUpdate(RemoteSystemUpdateModel remoteSystemUpdateModel);

    void updateScreenSaverMulti(ScreenSaverMulti screenSaverMulti);

    void checkIn(HotelCheckInResponse hotelCheckInResponse);

    void checkOut(HotelCheckOutResponse hotelCheckOutResponse);

    void feedback(ArrayList<Question> aQuestions);

    void covid();
}

