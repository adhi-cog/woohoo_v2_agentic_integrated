package com.sb.woohoo.ui.main;

import android.content.Context;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.fragments.feedback.pojo.Question;
import com.sb.woohoo.ui.main.fragments.music.MusicFragment;
import com.sb.woohoo.ui.main.fragments.screensaver.pojo.ScreenSaverMulti;
import com.sb.woohoo.ui.main.fragments.settings.language.pojo.LanguageData;
import com.sb.woohoo.ui.main.fragments.settings.update.UpdateDataModel;
import com.sb.woohoo.ui.main.fragments.weather.di.LatLngByIPModel;
import com.sb.woohoo.ui.main.pojo.EndSession;
import com.sb.woohoo.ui.main.pojo.RemoteAlertModel;
import com.sb.woohoo.ui.main.pojo.RemoteSystemUpdateModel;
import com.sb.woohoo.ui.main.pojo.StartSession;
import com.sb.woohoo.ui.main.pojo.StatsDumpRequest;
import com.sb.woohoo.ui.main.pojo.socketevent.HotelCheckInResponse;
import com.sb.woohoo.ui.main.pojo.socketevent.HotelCheckOutResponse;
import com.sb.woohoo.ui.main.secondarydisplay.SecondaryDisplayData;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.player.Metadata;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Shrey on 26/02/18.
 */

public interface MainContract {

    interface Views {

        void onError(int msg);

        void onServerError(String msg);

        void onBackPressed();

        void changeHintBarState(int hintText, int backgroundColour, int visibility);

        void setHintBarClickListener(View.OnClickListener hintBarClickListener);

        void resetHintBarClickListener();

        void changeView(boolean state);

        void setVolumeControlStream();

        void onServerSetup();

        void orderPlaced();

        void onNoInternet();

        void updateLatLng(LatLngByIPModel latLngByIPModel);

        void updateHomeScreenFromServer();

        void updateHomeScreenContactsFromServer();

        void systemAlert(RemoteAlertModel remoteAlertModel);

        void systemUpdate(RemoteSystemUpdateModel remoteSystemUpdateModel);

        void updateAvail(UpdateDataModel updateDataModel);

        void installUpdate(String apkFilePath);

        void updateScreenSaverFromServerMulti(ScreenSaverMulti screenSaverMulti);

        void sessionStarted(StartSession appstoreRes);

        void sessionEndSucess(EndSession appstoreRes);

        void onAllLocaleFetch(ArrayList<LanguageData> data);

        void updateCheckIn(HotelCheckInResponse hotelCheckInResponse);

        void updateCheckOut(HotelCheckOutResponse hotelCheckOutResponse);

        Fragment getCurrentFragment();

        void switchFragmentUsingAdd(String fragmentName);

        String getLang();

        void setLeftPane(FeatureFragment featureFragment);

        void loadDataInSecondDisplay(SecondaryDisplayData data);

        void onSettingsMode(boolean settingsMode);

        void updateFeedbackList(ArrayList<Question> aQuestions);

        void showSpeechResponse(String message);

        void setNightModeFromVoice(boolean enabled);

        void setBluetoothFromVoice(boolean enabled);

        void openBluetoothFromVoice();
    }

    interface Actions {

        void callUpdateAppVersion();
        void getLanguageRequest();

        void onCreate(Context context);

        void secondDisplayCreated();

        void resetNlpContext();

        void volumeUpPressed();

        void volumeDownPressed();

        void onDestroy(Context context);

        void endLiveFeed();

        void disableOtherSkills();

        void enableSurveillanceMode(boolean b);

        void respondToUser(String message);

        void resumePlayingVideo();

        void stopTextToSpeech(boolean callbackNeeded);

        void readNewsHeadline(String newsHeadline, boolean isDetailed);

        void askFollowUpQuery(String query);

        void closeDrawer();

        void orderPlaced();

        boolean isFragmentStacked();

        boolean isMusicPlayerInitialized();

        boolean isMusicPlaying();

        int getMusicCurrentPosition();

        void seekMusic(int position);

        void stopPlayingMusic();

        void placeOrder(JSONObject body);

        boolean isMusicRepeating();

        void setMusicRepeat(boolean repeat);

        void pausePlayingMusic();

        void resumePlayingMusic();

        void nextSong();

        void stopWakeWord();

        void previousSong();

        boolean isMusicPlayerTerminated();

        Metadata getMusicMetaData();

        Track getMusicTrack();

        void terminateMusicPlayer();

        void playMusic(String spotifyURI);

        boolean isSpotifyUserLoggedIn();

        void switchFragmentUsingAdd(FeatureFragment featureFragment);

        void switchFragmentUsingAdd(FeatureFragment featureFragmentLeft, FeatureFragment featureFragmentRight);

        void switchFragmentUsingAdd(MusicFragment musicFragment);

        void switchFragmentUsingReplace(FeatureFragment featureFragment);

        void switchFragmentOnError(FeatureFragment featureFragment);

        void setSpotifyPlayer(Context context, String token);

        void rotateCamera(boolean rotate, String hubId, String receverUserId, int angle, int isRotating);

        void emitEventToServer(String socketEvent, JsonObject jsonObject);

        void onLightStateEmitSocket(JsonObject jsonObject);

        void stopWakeWordDetection();

        void onWakeWordDetected();

        void onHomePressed();

        void stopPlayingVideo();

        void getUserDetails();

        void snoozePop();

        void updateScreenSaverTime(int time);

        void disableSkillsForLogOut();

        void disableWakeWord();

        void enableSkillsForLogOut();

        void emitMusicStatus(boolean isPlaying);

        void onTrackChanged();

        void checkForHubMode();

        void runWakeWord();

        void stopWakeWordAnimation();

        void onWakeWordChanged();

        void callForLatLngbyIP();

        void IPRetriveSucess(LatLngByIPModel latLngByIPModel);

        void doCheckUpdate();

        void onCheckUpdateSuccess(UpdateDataModel updateDataModel);

        void onCheckUpdateFailed();

        void installApk(String apkFilePath);

        void getScreenSaverTime();

        void callSessionEnd(StartSession value);

        void calltoVoiceCommand(String toString);

        void setLangChange(String lang);

        void openTranlationFragment();

        void openFnBFragment();

        void openFnBFragment(HashMap<String, Object> slots);

        void openRelaxationMusicFragment(HashMap<String, Object> slots);

        void openBuySimFragment(HashMap<String, Object> slots);

        void openFaqFragment();

        void openAmenitiesFragment();

        void openHotelInfoFragment();

        void openBookServiceFragment();

        void openDiningRestaurantsFragment();

        void openFeedbackFragment();

        void onSettingsMode(boolean settingsMode);

        void showMuteBar();

        void hideMuteBar();

        void hideTopDrawerItem();

        void showAllSoundBar();

        void callStatsDumpAPI(StatsDumpRequest statsDumpRequest);
    }
}
