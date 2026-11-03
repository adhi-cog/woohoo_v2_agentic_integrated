package com.sb.woohoo.ui.main;

import static com.sb.woohoo.data.prefs.SharedPrefsKey.PREF_KEY_SURVEILLANCE_MODE;
import static com.sb.woohoo.ui.main.helpers.SocketHelper.SocketEvents.Lights.CHANGE_DEVICE_STATE;
import static com.sb.woohoo.utils.Constants.LIVE_FEED_AUDIO_TYPE;
import static com.sb.woohoo.utils.Constants.LIVE_FEED_VIDEO_TYPE;
import static com.sb.woohoo.utils.others.MethodUtils.isNetworkAvailable;
import static com.sb.woohoo.utils.voice.PollyTextToSpeech.CALLBACK_NEWS;
import static com.sb.woohoo.utils.voice.PollyTextToSpeech.CALLBACK_NEWS_DETAILS;
import static com.sb.woohoo.utils.voice.PollyTextToSpeech.CALLBACK_OTHER;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;
import com.sb.woohoo.BuildConfig;
import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Interactors.HelperInteractor;
import com.sb.woohoo.ui.main.Interactors.NavigationInteractor;
import com.sb.woohoo.ui.main.Interactors.SpeechManager;
import com.sb.woohoo.ui.main.Interactors.listener.HelperListener;
import com.sb.woohoo.ui.main.Interactors.listener.NavigationListener;
import com.sb.woohoo.ui.main.Interactors.listener.PresenterListener;
import com.sb.woohoo.ui.main.Interactors.listener.SpeechListener;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.Light;
import com.sb.woohoo.ui.main.fragments.diningrestaurants.DiningRestaurantsFragment;
import com.sb.woohoo.ui.main.fragments.feedback.pojo.Question;
import com.sb.woohoo.ui.main.fragments.feedback.FeedbackFragment;
import com.sb.woohoo.ui.main.fragments.amadeusServices.AmadeusServicesFragment;
import com.sb.woohoo.ui.main.fragments.buysim.buypackage.BuySimFragment;
import com.sb.woohoo.ui.main.fragments.faq.categories.FaqCategoriesFragment;
import com.sb.woohoo.ui.main.fragments.fnb.categories.FnBCategoriesFragment;
import com.sb.woohoo.ui.main.fragments.fnb.leftpane.FnBLeftPaneFragment;
import com.sb.woohoo.ui.main.fragments.fnb.products.FnBProductsFragment;
import com.sb.woohoo.ui.main.fragments.fnb.sub_categories.FnBSubCategoriesFragment;
import com.sb.woohoo.ui.main.fragments.livefeed.LiveFeedFragment;
import com.sb.woohoo.ui.main.fragments.livefeedaudio.LiveFeedAudioFragment;
import com.sb.woohoo.ui.main.fragments.music.MusicFragment;
import com.sb.woohoo.ui.main.fragments.overview.categories.OverviewCategoriesFragment;
import com.sb.woohoo.ui.main.fragments.reservation.booking.ReservationFragment;
import com.sb.woohoo.ui.main.fragments.relaxationmusic.RelaxationMusicFragment;
import com.sb.woohoo.ui.main.fragments.screensaver.pojo.ScreenSaverMulti;
import com.sb.woohoo.ui.main.fragments.settings.language.pojo.LanguageData;
import com.sb.woohoo.ui.main.fragments.settings.update.UpdateDataModel;
import com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceon.SurveillanceFragment;
import com.sb.woohoo.ui.main.fragments.translation.TranslationFragment;
import com.sb.woohoo.ui.main.fragments.weather.di.LatLngByIPModel;
import com.sb.woohoo.ui.main.helpers.SocketHelper;
import com.sb.woohoo.ui.main.pojo.EndSession;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.pojo.RemoteAlertModel;
import com.sb.woohoo.ui.main.pojo.RemoteSystemUpdateModel;
import com.sb.woohoo.ui.main.pojo.StartSession;
import com.sb.woohoo.ui.main.pojo.StatsDumpRequest;
import com.sb.woohoo.ui.main.pojo.nlp.SpeechData;
import com.sb.woohoo.ui.main.pojo.nlp.SpeechResponse;
import com.sb.woohoo.ui.main.pojo.socketevent.HotelCheckInResponse;
import com.sb.woohoo.ui.main.pojo.socketevent.HotelCheckOutResponse;
import com.sb.woohoo.ui.main.pojo.socketevent.LiveFeedParticipantModel;
import com.sb.woohoo.ui.main.pojo.socketevent.LiveFeedSocketData;
import com.sb.woohoo.ui.main.pojo.template.TemplateData;
import com.sb.woohoo.ui.main.secondarydisplay.SecondaryDisplayData;
import com.sb.woohoo.utils.LocaleHelper;
import com.sb.woohoo.utils.voice.PollyTextToSpeech;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import javax.inject.Inject;

public class MainPresenter
        extends BasePresenter
        implements MainContract.Actions,
        IntentProcessor.IntentProcessCallback,
        HelperListener, NavigationListener, SpeechListener {

    static final String APK_FOLDER = Environment.getExternalStorageDirectory() + "/sb/";
    public static int REQUEST_BEGINNING = 100;
    public static int REQUEST_INTERNET = 101;
    // Constants.
    private final String TAG = MainPresenter.class.getSimpleName();
    // Other Components.
    private MainContract.Views mView;
    private MainRepository mainRepository;
    private PresenterListener.Helper mHelperInteractor;
    private PresenterListener.Speech mSpeechManager;
    private PresenterListener.Navigator mNavigationInteractor;
    // Variables.
    private IntentProcessor intentProcessor;
    private String twilioToken;
    private LiveFeedSocketData participantData;
    private Context mContext;

    @Inject
    public MainPresenter(
            MainContract.Views mView,
            MainRepository mainRepository,
            IntentProcessor intentProcessor,
            HelperInteractor helperInteractor,
            SpeechManager speechManager,
            NavigationInteractor navigationInteractor) {
        this.mView = mView;
        this.intentProcessor = intentProcessor;
        this.mainRepository = mainRepository;
        this.mHelperInteractor = helperInteractor;
        this.mSpeechManager = speechManager;
        this.mNavigationInteractor = navigationInteractor;
        mainRepository.onAttach(this);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {
    }

    /****************************************** MAINACTIVITY CALLBACK *****************************************/

    @Override
    public void onCreate(Context context) {
        this.mContext = context;
        LoggerWrapper.i(MainPresenter.class.getSimpleName(), "Constructor");
        mHelperInteractor.setListener(this);
        mHelperInteractor.registerCallbacks(LocaleHelper.getLanguage(context));
        mHelperInteractor.initializeComponents(context);

        mSpeechManager.setListener(this);

        WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_IS_FACTORY_RESET_DONE, false);

        mNavigationInteractor.registerCallbacks();
        mNavigationInteractor.registerForFragmentChanges();
        mNavigationInteractor.setNavigationListener(this);
        mNavigationInteractor.openHomeFragment();
    }

    @Override
    public void onDestroy(Context context) {
        //TODO mNavigationInteractor.unRegisterFragmentChanges();
        mHelperInteractor.cleanUp(context);
    }

    /*********************************************************************************************************/

    @Override
    public void endLiveFeed() {
        //TODO RESUME MUSIC
        mView.setVolumeControlStream();
        mHelperInteractor.runWakeWordRecognition();
        mHelperInteractor.toggleSlidingTrayVisibility(true);
        mHelperInteractor.toggleSlidingTrayBottomVisibility(true);

        mNavigationInteractor.onDeviceNormal();
    }

    @Override
    public void enableSurveillanceMode(boolean enable) {
        if (enable) {
            disableOtherSkills();
        } else {
            endLiveFeed();
        }
    }

    @Override
    public void onLightStateEmitSocket(JsonObject jsonObject) {
        mHelperInteractor.emitEventToServer(CHANGE_DEVICE_STATE, jsonObject);
    }

    @Override
    public void onIntentProcessed(IntentInfo intentInfo) {
        if (intentInfo != null) {
            boolean isExplicitFollowUpNeeded = intentInfo.needsExplicitFollowUp();
            String response = getSuccessSpeechText(intentInfo);
            boolean isUserResponseNullOrEmpty = response == null || response.trim().isEmpty();

            handleVoiceCommand(intentInfo);

            if (intentInfo.getSubCategory().equals(Categories.Skills.Translate.COMMAND_ACTION_TRANSLATE)) {
//                askFollowUpQuery(response);

            } else if (isExplicitFollowUpNeeded && !isUserResponseNullOrEmpty) {
                askFollowUpQuery(response);
            } else if (!isExplicitFollowUpNeeded) {
                if (response != null && !response.isEmpty()) {
                    respondToUser(response);
                } else {
                    if (!Objects.equals(intentInfo.getSubCategory(), Categories.Skills.COMMAND_SKILL_WEATHER) &&
                            !Objects.equals(intentInfo.getSubCategory(), Categories.Skills.COMMAND_SKILL_QNA)) {
                        mHelperInteractor.restoreVolume(AudioManager.STREAM_MUSIC);
                    }
                }
            }
        } else {
            printLog("Intent processing error : onIntentProcessed() ");
            mHelperInteractor.stopWakeWordAnimation(true);
//            mHelperInteractor.runWakeWordRecognition();
        }
    }

    private String getSuccessSpeechText(IntentInfo intentInfo) {
        if (intentInfo == null || intentInfo.getUserResponse() == null
                || intentInfo.getUserResponse().getOnSuccessSpeech() == null) {
            return null;
        }

        SpeechData speech = intentInfo.getUserResponse().getOnSuccessSpeech();
        if (speech.getSpeechSsml() != null && !speech.getSpeechSsml().trim().isEmpty()) {
            return speech.getSpeechSsml();
        }
        return speech.getSpeechText();
    }

    @Override
    public void callSessionEnd(StartSession value) {
        mainRepository.endSessionApi(value);
    }

    @Override
    public void calltoVoiceCommand(String toString) {
        mainRepository.processQuery(toString, 0, mView.getLang());
    }

    @Override
    public void setLangChange(String lang) {
        mHelperInteractor.registerCallbacks(lang);

    }



    /********************************************* ERROR METHODS *********************************************/

    @Override
    public void onIntentProcessError(String errorMessage) {
        // respondToUser(errorMessage);
        respondToUser(((MainActivity) mView).errorLayout.getContext().getString(R.string.did_not_get_that));
    }

    public void onAPICallError(Object error) {
        String msgString;
        if (error instanceof Integer)
            msgString = ((MainActivity) mView).errorLayout.getContext().getString((Integer) error);
        else
            msgString = (String) error;
        printLog(msgString);
        mHelperInteractor.stopWakeWordAnimation(false);
    }

    public void onError(int msgResId) {
        printLog("" + msgResId);
        mView.onError(msgResId);
    }

    public void onServerError(String msg) {
        printLog(msg);
        mView.onServerError(msg);
    }

    public void onNoInternet(String message) {
        printLog(message);
        mView.onNoInternet();
    }


    void onLiveFeedTokenError(String message) {
        printLog(message);
    }

    public void onHubStatusUpdateError(String message) {
        printLog(message);
    }

    /********************************************* Repository callbacks ******************************************/

    void unprocessableIntent() {
        respondToUser(((MainActivity) mView).errorLayout.getContext().getString(R.string.cant_do_that));
    }

    @Override
    public void stopWakeWordAnimation() {
        mHelperInteractor.stopWakeWordAnimation(true);
//        mHelperInteractor.runWakeWordRecognition();
    }

    void followUpForFulfillment(String followUpQuestion) {
        if (followUpQuestion != null && !followUpQuestion.isEmpty()) {
            LoggerWrapper.d(TAG, "followUpForFulfillment: " + followUpQuestion);
            askFollowUpQuery(followUpQuestion);
        } else {
            LoggerWrapper.e(TAG, ((MainActivity) mView).errorLayout.getContext().getString(R.string.empty_user_response));
        }
    }

    void processTemplates(TemplateData templateData) {
        mNavigationInteractor.handleTemplate(templateData);
        mHelperInteractor.setFollowUp(false);
        stopWakeWordAnimation();
    }

    void switchToWebView(String url) {
        stopWakeWordAnimation();
        mNavigationInteractor.handleWebview(url);
        runWakeWord();
    }

    @Override
    public void orderPlaced() {
        mView.orderPlaced();
    }

    @Override
    public void placeOrder(JSONObject body) {
        mainRepository.placeOrder(body);
    }


    void processSpeech(SpeechData speechData) {
        if (speechData != null) {
            String textToSpeak = speechData.getSpeechSsml();
            if (textToSpeak == null) {
                textToSpeak = speechData.getSpeechText();
            }
            respondToUser(textToSpeak);
        } else {
            LoggerWrapper.e(TAG, ((MainActivity) mView).errorLayout.getContext().getString(R.string.empty_string_to_speak));
        }
    }

    void processIntent(String intent, HashMap<String, Object> parameters, SpeechResponse userResponse, String userQuery, String appUrl, boolean explicitFollowUp) {
        mHelperInteractor.setFollowUp(false);
        mHelperInteractor.stopWakeWordAnimation(false);
        intentProcessor.getIntentInfo(intent, parameters, this, userResponse, userQuery, appUrl, explicitFollowUp);
    }


    public void onWakeWordChanged() {
        mHelperInteractor.onWakeWordChanged();
    }


    public void onHubStatusUpdateSuccess() {
    }

    void onLiveFeedTokenSuccess(String twilioToken) {
        //Set the hub status as busy
        this.twilioToken = twilioToken;
        mainRepository.updateHubStatus(true);
        onStartLiveFeed();
    }

    @Override
    public void getUserDetails() {
        mainRepository.getUserDetails();
    }

    void serverWorking() {
        mView.onServerSetup();
    }

    /******************************************** Reset DialogFlow Context ***************************************/

    @Override
    public void resetNlpContext() {
        mainRepository.resetNlpContext();
    }

    /****************************************** Private Methods ****************************************************/

    public void handleVoiceCommand(IntentInfo intentInfo) {
        mHelperInteractor.stopWakeWordAnimation(false);
        mNavigationInteractor.handleIntents(intentInfo);
    }

    private void onStartLiveFeed() {
        if (WooHooApp.getSharedPrefsHelper().getHubMode(PREF_KEY_SURVEILLANCE_MODE)) {
            mNavigationInteractor.startSurveillance(twilioToken);
        } else {
            disableOtherSkills();
            if (participantData.getFeedType() == null) {
                mNavigationInteractor.switchFragmentUsingAdd(LiveFeedFragment.newInstance(twilioToken, participantData));
            } else {
                if (participantData.getFeedType().equalsIgnoreCase(LIVE_FEED_AUDIO_TYPE)) {
                    mNavigationInteractor.switchFragmentUsingAdd(LiveFeedAudioFragment.newInstance(twilioToken, participantData));
                } else if (participantData.getFeedType().equalsIgnoreCase(LIVE_FEED_VIDEO_TYPE)) {
                    mNavigationInteractor.switchFragmentUsingAdd(LiveFeedFragment.newInstance(twilioToken, participantData));
                }
            }

        }
    }

    private void printLog(@NonNull String message) {
        LoggerWrapper.i(TAG, message);
    }

    /******************************************* Public Methods ****************************************************/

    @Override
    public void disableOtherSkills() {
        mHelperInteractor.closeDrawer();
        mHelperInteractor.toggleSlidingTrayVisibility(false);
        mHelperInteractor.toggleSlidingTrayBottomVisibility(false);

        disableSkillsForLogOut();
    }

    @Override
    public void disableSkillsForLogOut() {
        mSpeechManager.stopPolly(false);
        mHelperInteractor.pausePlayingMusic();
        mNavigationInteractor.stopPlayingVideo();
        mNavigationInteractor.stopRingingAlarm();
        mHelperInteractor.stopWakeWordRecognition();
        mHelperInteractor.removeWakeWordHandler();
        mHelperInteractor.stopWakeWordAnimation(false);
        mHelperInteractor.stopAsr();
    }

    @Override
    public void disableWakeWord() {
        mSpeechManager.stopPolly(false);
        mHelperInteractor.stopWakeWordRecognition();
        mHelperInteractor.removeWakeWordHandler();
        mHelperInteractor.stopWakeWordAnimation(false);
        mHelperInteractor.stopAsr();
    }

    @Override
    public void stopWakeWord() {
        mSpeechManager.stopPolly(false);
        mHelperInteractor.stopWakeWordRecognition();
        mHelperInteractor.stopWakeWordAnimation(false);
        mHelperInteractor.stopAsr();
    }

    @Override
    public void enableSkillsForLogOut() {
        mHelperInteractor.runWakeWordRecognition();
    }

    @Override
    public void resumePlayingVideo() {
        mNavigationInteractor.resumePlayingVideo();
    }


    @Override
    public void respondToUser(String response) {
        boolean isResponseEmptyOrNull = response == null || response.isEmpty();
        if (!isResponseEmptyOrNull) {
            LoggerWrapper.d(TAG, "respondToUser speaking via Polly: " + response);
            mView.showSpeechResponse(response);
            mHelperInteractor.startSpeaking(response, PollyTextToSpeech.STREAM_MUSIC, CALLBACK_OTHER, mView.getLang());
        } else {
            LoggerWrapper.e(MainPresenter.class.getSimpleName(), "respondToUser is empty or null");
        }
    }

    @Override
    public void stopTextToSpeech(boolean callbackNeeded) {
        mSpeechManager.stopPolly(callbackNeeded);
    }

    @Override
    public void readNewsHeadline(String newsHeadline, boolean isDetailedNews) {
        if (newsHeadline != null && !newsHeadline.isEmpty()) {
            if (!isDetailedNews) {
                mSpeechManager.speakUsingPolly(newsHeadline, PollyTextToSpeech.STREAM_MUSIC, CALLBACK_NEWS);
            } else {
                mSpeechManager.speakUsingPolly(newsHeadline, PollyTextToSpeech.STREAM_MUSIC, CALLBACK_NEWS_DETAILS);
            }
        }
    }

    @Override
    public void switchFragmentUsingAdd(FeatureFragment fragment) {
        mNavigationInteractor.switchFragmentUsingAdd(fragment);
    }

    @Override
    public void switchFragmentUsingAdd(FeatureFragment featureFragmentLeft, FeatureFragment featureFragmentRight) {
        mNavigationInteractor.switchFragmentUsingAdd(featureFragmentLeft, featureFragmentRight);
    }

    @Override
    public void switchFragmentUsingAdd(MusicFragment musicFragment) {
        mNavigationInteractor.switchFragmentUsingAdd(musicFragment);
    }

    @Override
    public void switchFragmentUsingReplace(FeatureFragment fragment) {
        mNavigationInteractor.switchFragmentUsingReplace(fragment);
    }

    @Override
    public void switchFragmentOnError(FeatureFragment featureFragment) {
        mHelperInteractor.closeDrawer();
        mHelperInteractor.toggleSlidingTrayVisibility(false);
        mHelperInteractor.toggleSlidingTrayBottomVisibility(false);
        mHelperInteractor.overlayAnimation(false);
        mNavigationInteractor.changeFragmentOnError(featureFragment);
    }

    @Override
    public void stopPlayingVideo() {
        mNavigationInteractor.stopPlayingVideo();
    }

    @Override
    public void stopWakeWordDetection() {
        mHelperInteractor.stopWakeWordRecognition();
    }

    @Override
    public void closeDrawer() {
        mHelperInteractor.closeDrawer();
    }


    @Override
    public void volumeUpPressed() {
        mHelperInteractor.onVolumeUpPressed();
    }

    @Override
    public void volumeDownPressed() {
        mHelperInteractor.onVolumeDownPressed();
    }

    @Override
    public void askFollowUpQuery(String query) {
        LoggerWrapper.d(TAG, "askFollowUpQuery speaking via Polly: " + query);
        LoggerWrapper.d(MainRepository.class.getSimpleName(), "MainPresenter.askFollowUpQuery entered for speech_data");
        mView.showSpeechResponse(query);
        mHelperInteractor.stopWakeWordRecognition();
        mHelperInteractor.setFollowUp(true);
        LoggerWrapper.d(MainRepository.class.getSimpleName(), "MainPresenter.askFollowUpQuery invoking Polly for speech_data");
        mHelperInteractor.startSpeaking(query, PollyTextToSpeech.STREAM_MUSIC, PollyTextToSpeech.CALLBACK_FOLLOWUP, mView.getLang());
        LoggerWrapper.d(MainRepository.class.getSimpleName(), "MainPresenter.askFollowUpQuery invoked Polly for speech_data");
    }

    @Override
    public void onHomePressed() {
        mNavigationInteractor.onHomePressed();
    }

    @Override
    public void onWakeWordDetected() {
        mHelperInteractor.startAsrRecording();
    }

    @Override
    public void seekMusic(int position) {
        mHelperInteractor.seekMusic(position);
    }

    @Override
    public void stopPlayingMusic() {
        mHelperInteractor.stopPlayingMusic();
    }

    @Override
    public void setMusicRepeat(boolean repeat) {
        mHelperInteractor.setMusicRepeat(repeat);
    }

    @Override
    public void pausePlayingMusic() {
        mHelperInteractor.pausePlayingMusic();
    }

    @Override
    public void resumePlayingMusic() {
        mHelperInteractor.resumePlayingMusic();
    }

    @Override
    public void nextSong() {
        mHelperInteractor.nextSong();
    }

    @Override
    public void previousSong() {
        mHelperInteractor.previousSong();
    }

    @Override
    public void terminateMusicPlayer() {
        mHelperInteractor.terminateMusicPlayer();
    }

    @Override
    public void playMusic(@NonNull String spotifyURI) {
        mHelperInteractor.playMusic(spotifyURI);
    }

    @Override
    public void setSpotifyPlayer(Context context, String token) {
        mHelperInteractor.setSpotifyPlayer(context, token);
    }

    @Override
    public void rotateCamera(boolean rotate, String hubId, String receverUserId, int angle, int isRotating) {
        mHelperInteractor.rotateCamera(rotate, hubId, receverUserId, angle, isRotating);
    }

    @Override
    public void emitEventToServer(String socketEvent, JsonObject jsonObject) {
        mHelperInteractor.emitEventToServer(socketEvent, jsonObject);
    }

    @Override
    public boolean isFragmentStacked() {
        return mNavigationInteractor.isFragmentStacked();
    }

    @Override
    public boolean isMusicPlayerInitialized() {
        return mHelperInteractor.isMusicPlayerInitialized();
    }

    @Override
    public boolean isMusicPlayerTerminated() {
        return mHelperInteractor.isMusicPlayerTerminated();
    }

    @Override
    public boolean isSpotifyUserLoggedIn() {
        return mHelperInteractor.isSpotifyUserLoggedIn();
    }

    @Override
    public boolean isMusicRepeating() {
        return mHelperInteractor.isMusicRepeating();
    }

    @Override
    public int getMusicCurrentPosition() {
        return mHelperInteractor.getMusicCurrentPosition();
    }

    @Override
    public Metadata getMusicMetaData() {
        return mHelperInteractor.getMusicMetaData();
    }

    @Override
    public Track getMusicTrack() {
        return null;
    }

    @Override
    public void emitMusicStatus(boolean isPlaying) {
        mHelperInteractor.emitEventToServer(SocketHelper.SocketEvents.Music.MUSIC_PLAY_PAUSE, mainRepository.getPlayStatusJSONObject(isPlaying));
    }

    @Override
    public void onTrackChanged() {
        mHelperInteractor.emitEventToServer(SocketHelper.SocketEvents.Music.MUSIC_SETTER, mainRepository.setMusicMetaData(mHelperInteractor.getMusicMetaData()));
    }

    @Override
    public void checkForHubMode() {
        mainRepository.updateHubStatus(false);
        mainRepository.checkForHubStatus(REQUEST_BEGINNING);
    }


    @Override
    public void getScreenSaverTime() {
        mainRepository.getScreenSaverTime(0);
    }

    @Override
    public void runWakeWord() {
        mHelperInteractor.runWakeWordRecognition();
    }

    /************************************** Helper Callback methods ********************************************************/

    @Override
    public void onBackNavigation() {
        mView.onBackPressed();
    }

    @Override
    public void processQuery(String query) {
        processQuery(query, mView.getLang());
    }

    @Override
    public void processQuery(String query, String lang) {

        Fragment currentFragment = mView.getCurrentFragment();

        if (currentFragment instanceof TranslationFragment) {
            ((TranslationFragment) currentFragment).translate(query);
        } else {
            mainRepository.processQuery(query, 0, lang);
        }
    }

    @Override
    public void openTranlationFragment() {
        switchFragmentUsingAdd(TranslationFragment.newInstance(null));
    }

    @Override
    public void openFnBFragment() {
        openFnBFragment(null);
    }

    @Override
    public void openFnBFragment(HashMap<String, Object> slots) {
        Fragment currentFragment = mView.getCurrentFragment();

        if (currentFragment instanceof FnBCategoriesFragment) {
            ((FnBCategoriesFragment) currentFragment).getPresenter().processActions(null);
        } else {
            switchFragmentUsingAdd(FnBCategoriesFragment.newInstance());
        }

    }

    private String getSlotValue(HashMap<String, Object> slots, String key) {
        if (slots == null || !slots.containsKey(key) || slots.get(key) == null) {
            return null;
        }

        String value = String.valueOf(slots.get(key)).trim();
        return value.isEmpty() || "null".equalsIgnoreCase(value) ? null : value;
    }

    private void openFnBSubCategoriesForCategory(String categoryId) {
        Bundle bundle = new Bundle();
        bundle.putString("category_id", categoryId);
        bundle.putBoolean("category_sub_type", true);

        FnBSubCategoriesFragment fragment = FnBSubCategoriesFragment.newInstance();
        fragment.setArguments(bundle);
        openFnBFeatureFragment(FnBSubCategoriesFragment.class.getSimpleName(), fragment);
    }

    private void openFnBProductsForSubCategory(String subCategoryId) {
        Bundle bundle = new Bundle();
        bundle.putString("category_id", "");
        bundle.putBoolean("category_sub_type", true);
        bundle.putString("sub_category_id", subCategoryId);

        FnBProductsFragment fragment = FnBProductsFragment.newInstance();
        fragment.setArguments(bundle);
        openFnBFeatureFragment(FnBProductsFragment.class.getSimpleName(), fragment);
    }

    private void openFnBFeatureFragment(String fragmentName, FeatureFragment fragment) {
        if (mContext != null && mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            switchFragmentUsingAdd(FnBLeftPaneFragment.getInstance(fragmentName), fragment);
        } else {
            switchFragmentUsingReplace(fragment);
        }
    }

    @Override
    public void openRelaxationMusicFragment(HashMap<String, Object> slots) {
        Fragment currentFragment = mView.getCurrentFragment();

        if (currentFragment instanceof RelaxationMusicFragment) {
            ((RelaxationMusicFragment) currentFragment).handleVoiceSlots(slots);
        } else {
            switchFragmentUsingAdd(RelaxationMusicFragment.newInstance(slots));
        }
    }

    @Override
    public void openBuySimFragment(HashMap<String, Object> slots) {
        Fragment currentFragment = mView.getCurrentFragment();

        if (currentFragment instanceof BuySimFragment) {
            ((BuySimFragment) currentFragment).handleVoiceSlots(slots);
        } else {
            switchFragmentUsingAdd(BuySimFragment.newInstance(slots));
        }
    }

    @Override
    public void openFaqFragment() {
        switchFragmentUsingAdd(FaqCategoriesFragment.newInstance());
    }

    @Override
    public void openAmenitiesFragment() {
        switchFragmentUsingAdd(AmadeusServicesFragment.newInstance());
    }

    @Override
    public void openHotelInfoFragment() {
        switchFragmentUsingAdd(OverviewCategoriesFragment.newInstance());
    }

    @Override
    public void openBookServiceFragment() {
        switchFragmentUsingAdd(ReservationFragment.newInstance(null));
    }

    @Override
    public void openDiningRestaurantsFragment() {
        switchFragmentUsingAdd(DiningRestaurantsFragment.newInstance());
    }

    @Override
    public void openFeedbackFragment() {
        switchFragmentUsingAdd(FeedbackFragment.newInstance());
    }
    @Override
    public void onSettingsMode(boolean settingsMode) {
        mView.onSettingsMode(settingsMode);
    }

    @Override
    public void showMuteBar() {
        mHelperInteractor.showMuteBar(isNetworkAvailable(mContext));
    }

    @Override
    public void hideMuteBar() {
        mHelperInteractor.hideMuteBar(isNetworkAvailable(mContext));
    }

    @Override
    public void hideTopDrawerItem() {
        mHelperInteractor.hideTopDrawerItem();
    }

    @Override
    public void showAllSoundBar() {
        mHelperInteractor.showAllSoundBar();
    }

    public void callStatsDumpAPI(StatsDumpRequest statsDumpRequest){
        mainRepository.callStatsDumpAPI(statsDumpRequest);
    }

    @Override
    public void secondDisplayCreated() {
        mainRepository.getSecondDisplayData();
    }

    @Override
    public void getTwilioToken(LiveFeedParticipantModel liveFeedParticipantModel) {
        participantData = liveFeedParticipantModel.getData();
        String hubID = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, null);
        if (mHelperInteractor.getCameraStatus()) {
            mainRepository.requestLiveFeedToken(hubID);
        } else {
            mView.onError(R.string.camera_not_working);
            mHelperInteractor.emitEventToServer("dropInReject", sendRejectionAction(participantData));
        }
    }

    private JsonObject sendRejectionAction(LiveFeedSocketData participantData) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, null));
        jsonObject.addProperty("senderClientId", participantData.getSenderHubId());
        return jsonObject;
    }

    @Override
    public void onSpotifyUserLoggedIn() {
        mNavigationInteractor.onSpotifyMusicLoggedIn();
    }

    @Override
    public void onSpotifyUserLoginFailed() {
        mNavigationInteractor.onSpotifyMusicLoginFailed();
    }

    @Override
    public void onSpotifyUserLoggedOut() {
        mNavigationInteractor.onSpotifyMusicLoggedOut();
    }

    @Override
    public void onSpotifyPlayerPlaybackError(Error error) {
        mNavigationInteractor.onSpotifyMusicPlaybackError(error);
    }

    @Override
    public void onSpotifyMusicPlaying() {
        mNavigationInteractor.onSpotifyMusicPlaying();

        //TODO socket
        mHelperInteractor.emitEventToServer(SocketHelper.SocketEvents.Music.MUSIC_PLAY_PAUSE,
                mainRepository.getPlayStatusJSONObject(true));
    }

    @Override
    public void onSpotifyMusicPaused() {
        mNavigationInteractor.onSpotifyMusicPaused();

        //TODO socket
        mHelperInteractor.emitEventToServer(SocketHelper.SocketEvents.Music.MUSIC_PLAY_PAUSE,
                mainRepository.getPlayStatusJSONObject(true));
    }

    @Override
    public void onSpotifyMusicNextTrack() {
        mNavigationInteractor.onSpotifyMusicNextTrack();
    }

    @Override
    public void onSpotifyMusicPreviousTrack() {
        mNavigationInteractor.onSpotifyMusicPreviousTrack();
    }

    @Override
    public void onSpotifyMusicShuffleOn() {
        mNavigationInteractor.onSpotifyMusicShuffleOn();
    }

    @Override
    public void onSpotifyMusicShuffleOff() {
        mNavigationInteractor.onSpotifyMusicShuffleOff();
    }

    @Override
    public void onSpotifyMusicRepeatOn() {
        mNavigationInteractor.onSpotifyMusicRepeatOn();
    }

    @Override
    public void onSpotifyMusicRepeatOff() {
        mNavigationInteractor.onSpotifyMusicRepeatOff();
    }

    @Override
    public void onSpotifyMusicStopped() {
        mNavigationInteractor.onSpotifyMusicStopped();
    }

    @Override
    public void onSpotifyMusicTrackChanged() {
        mNavigationInteractor.onSpotifyMusicTrackChanged();

        //TODO socket
        mHelperInteractor.emitEventToServer(SocketHelper.SocketEvents.Music.MUSIC_SETTER,
                mainRepository.setMusicMetaData(mHelperInteractor.getMusicMetaData()));
    }

    @Override
    public void onSpotifyMusicTrackDelivered() {
        mNavigationInteractor.onSpotifyMusicTrackDelivered();
    }

    @Override
    public void onSpotifyMusicMetaDataChanged() {
        mNavigationInteractor.onSpotifyMusicMetaDataChanged();
    }

    @Override
    public void onTakeMetaData(Metadata metadata) {
        mNavigationInteractor.onSpotifyMusicTakeData(metadata);
    }

    @Override
    public void onSpotifyMusicActionCompleted() {
        mNavigationInteractor.onSpotifyMusicActionCompleted();
    }

    @Override
    public void onSpotifyMusicActionCancelled() {
        mNavigationInteractor.onSpotifyMusicActionCancelled();
    }

    @Override
    public void onSpeechNewsCallback(boolean isDetailedNews) {
        mNavigationInteractor.handleSpeechNewsCallback(isDetailedNews);
    }

    @Override
    public void onLightSocketEvent(Light light) {
        mNavigationInteractor.onLightStateChanged(light);
    }

    @Override
    public void dropInCallAccepted() {
        mNavigationInteractor.onDropInCallAccepted();
    }

    @Override
    public void dropInCallRejected() {
        mNavigationInteractor.onDropInCallRejected();
    }

    @Override
    public void onAlert(Intent intent, String key) throws IOException, ClassNotFoundException {
        mNavigationInteractor.alertAlarm(intent, key);
    }

    @Override
    public void onActivityIdle() {
        mNavigationInteractor.snoozePop();
    }

    @Override
    public void onActivityDetected() {
        mNavigationInteractor.pausePop();
    }

    @Override
    public void checkAndUpdateStatus() {
        mainRepository.updateHubStatus(false);
        mainRepository.checkForHubStatus(REQUEST_INTERNET);
    }

    @Override
    public void updateHomeScreen() {
        mView.updateHomeScreenFromServer();
    }


    @Override
    public void updateScreenSaverMulti(ScreenSaverMulti screenSaverMulti) {
        mView.updateScreenSaverFromServerMulti(screenSaverMulti);
    }

    @Override
    public void checkIn(HotelCheckInResponse hotelCheckInResponse) {
        mView.updateCheckIn(hotelCheckInResponse);
    }

    @Override
    public void checkOut(HotelCheckOutResponse hotelCheckOutResponse) {
        mView.updateCheckOut(hotelCheckOutResponse);

    }

    @Override
    public void feedback(ArrayList<Question> aQuestions) {
        mView.updateFeedbackList(aQuestions);
    }

    @Override
    public void covid() {
        mView.switchFragmentUsingAdd("covid-19");
    }

    @Override
    public void updateHomeScreenContacts() {
        mView.updateHomeScreenContactsFromServer();
    }

    public void onSystemAlert(RemoteAlertModel remoteAlertModel) {
        mView.systemAlert(remoteAlertModel);
    }

    @Override
    public void onSystemUpdate(RemoteSystemUpdateModel remoteSystemUpdateModel) {
        mView.systemUpdate(remoteSystemUpdateModel);
    }

    @Override
    public void snoozePop() {
        mNavigationInteractor.snoozePop();
    }

    @Override
    public void updateScreenSaverTime(int time) {
        mNavigationInteractor.updateScreenSaverTime(time);
    }

    /************************************** Navigator Callback methods ********************************************************/

    @Override
    public void changeHintBar(int hintText, int backgroundColour, int visibility) {
        mView.changeHintBarState(hintText, backgroundColour, visibility);
    }

    @Override
    public void handleBackPressed() {
        mView.onBackPressed();
    }

    @Override
    public boolean isMusicPlaying() {
        return mHelperInteractor.isMusicPlaying();
    }

    @Override
    public void pauseMusic() {
        mHelperInteractor.pausePlayingMusic();
    }

    @Override
    public void nextMusic() {
        mHelperInteractor.nextSong();
    }

    @Override
    public void previousMusic() {
        mHelperInteractor.previousSong();
    }

    @Override
    public void resumeMusic() {
        mHelperInteractor.resumePlayingMusic();
    }

    @Override
    public void stopMusic() {
        mHelperInteractor.stopPlayingMusic();
    }

    @Override
    public void onResponseToUser(Object response) {
        String msgString;
        if (response instanceof Integer)
            msgString = ((MainActivity) mView).errorLayout.getContext().getString((Integer) response);
        else
            msgString = (String) response;
        respondToUser(msgString);
    }

    @Override
    public void controlVolume(IntentInfo intentInfo) {
        if (controlSystemSetting(intentInfo)) {
            return;
        }
        mHelperInteractor.controlSystemVolume(intentInfo);
    }

    private boolean controlSystemSetting(IntentInfo intentInfo) {
        int action = intentInfo.getAction();
        if (action == Categories.Controls.SystemSetting.NIGHT_MODE_ON) {
            mView.setNightModeFromVoice(true);
            return true;
        } else if (action == Categories.Controls.SystemSetting.NIGHT_MODE_OFF) {
            mView.setNightModeFromVoice(false);
            return true;
        } else if (action == Categories.Controls.SystemSetting.BLUETOOTH_ON) {
            mView.setBluetoothFromVoice(true);
            return true;
        } else if (action == Categories.Controls.SystemSetting.BLUETOOTH_OFF) {
            mView.setBluetoothFromVoice(false);
            return true;
        } else if (action == Categories.Controls.SystemSetting.BLUETOOTH_OPEN) {
            mView.openBluetoothFromVoice();
            return true;
        } else if (action == Categories.Controls.SystemSetting.BRIGHTNESS_UP) {
            adjustBrightness(25);
            return true;
        } else if (action == Categories.Controls.SystemSetting.BRIGHTNESS_DOWN) {
            adjustBrightness(-25);
            return true;
        } else if (action == Categories.Controls.SystemSetting.BRIGHTNESS_SET) {
            setBrightnessFromIntent(intentInfo);
            return true;
        }
        return false;
    }

    private void adjustBrightness(int delta) {
        int brightness = mHelperInteractor.getBrightness();
        mHelperInteractor.setBrightness(Math.max(6, Math.min(255, brightness + delta)));
    }

    private void setBrightnessFromIntent(IntentInfo intentInfo) {
        Object rawValue = intentInfo.getParameters().get("percentage");
        if (rawValue == null || rawValue.toString().isEmpty()) {
            rawValue = intentInfo.getParameters().get("value");
        }
        if (rawValue == null || rawValue.toString().isEmpty()) {
            return;
        }
        String cleanValue = rawValue.toString().replace("%", "");
        int percentage = Integer.parseInt(cleanValue);
        percentage = Math.max(1, Math.min(100, percentage));
        int brightness = Math.max(6, Math.min(255, Math.round(percentage * 255f / 100f)));
        mHelperInteractor.setBrightness(brightness);
    }

    @Override
    public void onSurveillanceModeChanged(boolean state) {
        mView.changeView(state);
        enableSurveillanceMode(state);
        WooHooApp.getSharedPrefsHelper().setHubMode(PREF_KEY_SURVEILLANCE_MODE, state);
        if (state) {
            switchFragmentUsingAdd(SurveillanceFragment.newInstance());
        } else {
            mNavigationInteractor.onDeviceNormal();
        }
    }

    public void surveillanceMode(boolean state) {
        enableSurveillanceMode(state);
        WooHooApp.getSharedPrefsHelper().setHubMode(PREF_KEY_SURVEILLANCE_MODE, state);
        if (state) {
            switchFragmentUsingAdd(SurveillanceFragment.newInstance());
        }
    }

    @Override
    public void changeLightState(Light light) {
        mainRepository.makeAPICall(light);
    }

    @Override
    public void callForLatLngbyIP() {
        mainRepository.getLatlngWithIP();
    }

    @Override
    public void stopTimer() {
        mHelperInteractor.stopTimer();
    }

    /************************************** Speech Callback methods ********************************************************/

    @Override
    public void startSpeaking(String text, int streamType, int callbackType) {
        mHelperInteractor.startSpeaking(text, streamType, callbackType,mView.getLang());
    }

    @Override
    public void stopSpeaking(boolean needCallback) {
        mHelperInteractor.stopSpeaking(needCallback);
    }

    @Override
    public void IPRetriveSucess(LatLngByIPModel latLngByIPModel) {
        mView.updateLatLng(latLngByIPModel);
    }

    @Override
    public void doCheckUpdate() {
        mainRepository.preformCheckUpdate();
    }

    @Override
    public void onCheckUpdateSuccess(UpdateDataModel updateDataModel) {
        int currentVersionCode = BuildConfig.VERSION_CODE;
        int versionCodeReceived = updateDataModel.getVersionCode();

        if (versionCodeReceived > currentVersionCode) {
            LoggerWrapper.i("TAG", "**** server code received is greater than current version " + versionCodeReceived + "  " + currentVersionCode);
            mView.updateAvail(updateDataModel);
        } else {
            LoggerWrapper.i("TAG", "**** server code received and current version " + versionCodeReceived + "  " + currentVersionCode);

            mView.onError(R.string.no_update_available);

        }
    }

    @Override
    public void onCheckUpdateFailed() {
        mView.onError(R.string.error_while_updating);
    }

    @Override
    public void installApk(String apkFilePath) {
        mView.installUpdate(apkFilePath);

    }

    public BluetoothProfile.ServiceListener getBluetoothProfileListener(BluetoothAdapter bluetoothAdapter) {
        return new BluetoothProfile.ServiceListener() {
            BluetoothA2dp bluetoothA2dp;

            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                if (profile == BluetoothProfile.A2DP) {
                    bluetoothA2dp = (BluetoothA2dp) proxy;
                }
                bluetoothAdapter.closeProfileProxy(BluetoothProfile.STATE_CONNECTED, proxy);
                bluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP, bluetoothA2dp);
            }

            public void onServiceDisconnected(int profile) {

            }
        };
    }

    public void onScreenSaverFetched(ScreenSaverMulti data) {
        LoggerWrapper.i("TAG", "**** screensaver data " + data);
        mView.updateScreenSaverFromServerMulti(data);
    }

    public void onSecondDisplayDataFetched(SecondaryDisplayData data) {
        LoggerWrapper.i("TAG", "**** second display data " + data);
        mView.loadDataInSecondDisplay(data);
    }

    public void onStartSessionSucess(StartSession appstoreRes) {
        mView.sessionStarted(appstoreRes);
    }

    public void onSessionEnd(EndSession appstoreRes) {

        mView.sessionEndSucess(appstoreRes);

    }

    @Override
    public void callUpdateAppVersion() {
        mainRepository.updateAppVersion();
    }

    public void onAllLocaleFetchSucess(ArrayList<LanguageData> data) {
        mView.onAllLocaleFetch(data);
    }

    @Override
    public void getLanguageRequest() {
        mainRepository.getLanguageRequest();
    }

    /*****************************************************************************************************************/
}
