package com.sb.woohoo.ui.main.navigators;


import static android.view.View.VISIBLE;
import static com.sb.woohoo.ui.base.IHintBarState.HINT_RESET;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;
import com.sb.woohoo.R;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.callbacks.VideoCalling;
import com.sb.woohoo.ui.main.fragments.alarm.alarm.AlarmFragment;
import com.sb.woohoo.ui.main.fragments.alarm.alarmalart.AlarmAlertFragment;
import com.sb.woohoo.ui.main.fragments.alarm.pojo.Alarm;
import com.sb.woohoo.ui.main.fragments.contacts.ContactsFragment;
import com.sb.woohoo.ui.main.fragments.contactsprofile.ContactsProfileFragment;
import com.sb.woohoo.ui.main.fragments.devices.discovery.DeviceDiscoveryFragment;
import com.sb.woohoo.ui.main.fragments.devices.huecontrol.PhilipsHueFragment;
import com.sb.woohoo.ui.main.fragments.devices.launcher.DeviceLauncherFragment;
import com.sb.woohoo.ui.main.fragments.home.HomeFragment;
import com.sb.woohoo.ui.main.fragments.music.MusicFragment;
import com.sb.woohoo.ui.main.fragments.newsaudio.NewsAudioFragment;
import com.sb.woohoo.ui.main.fragments.newstext.detail.NewsDetailFragment;
import com.sb.woohoo.ui.main.fragments.newstext.headline.NewsHeadlineFragment;
import com.sb.woohoo.ui.main.fragments.notification.Notification;
import com.sb.woohoo.ui.main.fragments.notification.NotificationFragment;
import com.sb.woohoo.ui.main.fragments.qna.QnaFragment;
import com.sb.woohoo.ui.main.fragments.reminders.ReminderFragment;
import com.sb.woohoo.ui.main.fragments.reminders.broadcastreceiver.SetReminder;
import com.sb.woohoo.ui.main.fragments.reminders.pojo.Reminder;
import com.sb.woohoo.ui.main.fragments.settings.SettingsFragment;
import com.sb.woohoo.ui.main.fragments.settings.devicesettings.DeviceSettingsFragment;
import com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceon.SurveillanceFragment;
import com.sb.woohoo.ui.main.fragments.templates.image.list.ImageListFragment;
import com.sb.woohoo.ui.main.fragments.templates.image.single.ImageSingleFragment;
import com.sb.woohoo.ui.main.fragments.templates.text.list.ListFragment;
import com.sb.woohoo.ui.main.fragments.templates.text.single.TextFragment;
import com.sb.woohoo.ui.main.fragments.timer.Timer;
import com.sb.woohoo.ui.main.fragments.timer.TimerFragment;
import com.sb.woohoo.ui.main.fragments.timer.TimerService;
import com.sb.woohoo.ui.main.fragments.todo.ToDoFragment;
import com.sb.woohoo.ui.main.fragments.translation.TranslationFragment;
import com.sb.woohoo.ui.main.fragments.video.VideoFragment;
import com.sb.woohoo.ui.main.fragments.video.VideoListFragment;
import com.sb.woohoo.ui.main.fragments.weather.WeatherFragment;
import com.sb.woohoo.ui.main.fragments.webview.WebViewFragment;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.pojo.template.ImageListTemplate;
import com.sb.woohoo.ui.main.pojo.template.ImageTemplate;
import com.sb.woohoo.ui.main.pojo.template.TemplateData;
import com.sb.woohoo.ui.main.pojo.template.TextListTemplate;
import com.sb.woohoo.ui.main.pojo.template.TextTemplate;
import com.sb.woohoo.ui.main.pojo.template.VideoTemplate;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import javax.inject.Inject;

public class StateNavigator {

    private static final String TAG = StateNavigator.class.getSimpleName();
    private SwitchFragmentCallback callback;
    private FragmentManager fragmentManager;
    private ArrayList<Notification> notificationList = new ArrayList<>(0);

    @Inject
    public StateNavigator(Context context, FragmentManager fragmentManager) {
        LoggerWrapper.i(StateNavigator.class.getSimpleName(), "Constructor");
        this.fragmentManager = fragmentManager;
    }

    /**
     * Sets the listener for the callbacks
     *
     * @param callBack Implement the callbacks
     */
    public void setStateListener(SwitchFragmentCallback callBack) {
        this.callback = callBack;
    }

    /**
     * Handles the fragment state
     *
     * @param templateData Tells what to do next
     */
    public void handleTemplateState(TemplateData templateData) {
        HashMap<String, Object> templateDataHasMap = templateData.getDisplayTemplate();

        String type = (String) templateDataHasMap.get("type");

        Object body = templateDataHasMap.get("body");

        Gson gson = new Gson();

        String bodyJsonString = gson.toJson(body);

        Object template;

        Fragment currentFragment = callback.getCurrentFragment();

        if (type != null) {
            switch (type) {
                case TemplateData.TemplateType.TEXT:
                    template = gson.fromJson(bodyJsonString, TextTemplate.class);
                    templateData.setTemplate(template);

                    if (currentFragment instanceof TextFragment) {
                        ((TextFragment) currentFragment).processTemplates(templateData);
                    } else {
                        handleFragmentNavigationUsingAdd(TextFragment.newInstance(templateData));
                    }

                    break;
                case TemplateData.TemplateType.TEXT_LIST:
                    template = gson.fromJson(bodyJsonString, TextListTemplate.class);
                    templateData.setTemplate(template);

                    if (currentFragment instanceof ListFragment) {
                        ((ListFragment) currentFragment).processTemplates(templateData);
                    } else {
                        handleFragmentNavigationUsingAdd(ListFragment.newInstance(templateData));
                    }

                    break;
                case TemplateData.TemplateType.IMAGE:
                    template = gson.fromJson(bodyJsonString, ImageTemplate.class);
                    templateData.setTemplate(template);

                    if (currentFragment instanceof ImageSingleFragment) {
                        ((ImageSingleFragment) currentFragment).processTemplates(templateData);
                    } else {
                        handleFragmentNavigationUsingAdd(ImageSingleFragment.newInstance(templateData));
                    }

                    break;
                case TemplateData.TemplateType.IMAGE_LIST:
                    template = gson.fromJson(bodyJsonString, ImageListTemplate.class);
                    templateData.setTemplate(template);

                    if (currentFragment instanceof ImageListFragment) {
                        ((ImageListFragment) currentFragment).processTemplates(templateData);
                    } else {
                        handleFragmentNavigationUsingAdd(ImageListFragment.newInstance(templateData));
                    }

                    break;
                case TemplateData.TemplateType.VIDEO:
                    template = gson.fromJson(bodyJsonString, VideoTemplate.class);
                    templateData.setTemplate(template);

                    if (currentFragment instanceof VideoFragment) {
                        ((VideoFragment) currentFragment).processTemplates(templateData);
                    } else {
                        handleFragmentNavigationUsingAdd(VideoFragment.newInstance(templateData));
                    }

                    break;

                case TemplateData.TemplateType.VIDEO_LIST:
                    LoggerWrapper.i(TAG, "Video List template is not handled");
                    break;

                default:
                    LoggerWrapper.e(TAG, type + " : this type of template is not parsed");
            }
        }
    }

    /**
     * Handles the fragment state
     */

    public void handleWebView(String url) {
        if (callback != null) {
            Fragment currentFragment = callback.getCurrentFragment();
            if (currentFragment instanceof WebViewFragment) {
                //((WebViewFragment) currentFragment).updateInstance(url);
            } else {
                //handleFragmentNavigationUsingAdd(WebViewFragment.newInstance(url, WebViewFragment.WebviewLaunchMode.Default));
            }
        }

    }

    public void handleFragmentState(IntentInfo intentInfo) {
        if (callback != null) {
            Fragment currentFragment = callback.getCurrentFragment();

            switch (intentInfo.getSubCategory()) {
                case Categories.Skills.COMMAND_SKILL_REMINDER:
                    callback.changeHintBar(HINT_RESET, HINT_RESET, VISIBLE);
                    stopPlayingVideo();
                    if (currentFragment instanceof ReminderFragment) {
                        ((ReminderFragment) currentFragment).onNewIntent(intentInfo);
                    } else {
                        handleFragmentNavigationUsingAdd(ReminderFragment.newInstance(intentInfo));
                    }
                    break;
                case Categories.Skills.COMMAND_SKILL_ALARM:
                    callback.changeHintBar(HINT_RESET, HINT_RESET, VISIBLE);
                    stopPlayingVideo();
                    if (currentFragment instanceof AlarmFragment) {
                        ((AlarmFragment) currentFragment).onNewIntent(intentInfo);
                    } else {
                        handleFragmentNavigationUsingAdd(AlarmFragment.newInstance(intentInfo));
                    }
                    break;
                case Categories.Skills.COMMAND_SKILL_TODO:
                    callback.changeHintBar(HINT_RESET, HINT_RESET, VISIBLE);
                    stopPlayingVideo();
                    if (currentFragment instanceof ToDoFragment) {
                        ((ToDoFragment) currentFragment).onNewIntent(intentInfo);
                    } else {
                        handleFragmentNavigationUsingAdd(ToDoFragment.newInstance(intentInfo));
                    }
                    break;
                case Categories.Skills.COMMAND_SKILL_TIMER:
                    callback.changeHintBar(HINT_RESET, HINT_RESET, VISIBLE);
                    stopPlayingVideo();
                    handleTimer(currentFragment, intentInfo);
                    break;
                case Categories.Skills.COMMAND_SKILL_QNA:
                    callback.changeHintBar(HINT_RESET, HINT_RESET, VISIBLE);
                    stopPlayingVideo();
                    if (currentFragment instanceof QnaFragment) {
                        ((QnaFragment) currentFragment).onNewIntent(intentInfo);
                    } else {
                        handleFragmentNavigationUsingAdd(QnaFragment.newInstance(intentInfo));
                    }
                    break;
               /* case Categories.Skills.COMMAND_SKILL_MUSIC:
                    callback.changeHintBar(HINT_RESET, HINT_RESET, VISIBLE);
                    stopPlayingVideo();
                    if (currentFragment instanceof MusicFragment) {
                        ((MusicFragment) currentFragment).onNewIntent(intentInfo);
                    } else {
                        handleFragmentNavigationUsingAdd(MusicFragment.newInstance(intentInfo));
                    }
                    break;*/
                case Categories.Controls.COMMAND_CONTROLS_NAVIGATION:
                    if (!(currentFragment instanceof HomeFragment)) {
                        callback.handleBackNavigation();
                    }
                    // Navigation to home
                    if (intentInfo.getIntentName().contentEquals(Categories.Controls.Navigation.COMMAND_NAVIGATION_HOME)) {
                        while (!(currentFragment instanceof HomeFragment)) {
                            callback.handleBackNavigation();
                            currentFragment = callback.getCurrentFragment();
                        }
                    }
                    break;
                case Categories.Controls.COMMAND_CONTROLS_PLAYER:
                    handlePlayerCommands(intentInfo, currentFragment);
                    break;
                case Categories.Communication.COMMAND_COMMUNICATION_CONTACT:
                    callback.changeHintBar(HINT_RESET, HINT_RESET, VISIBLE);
                    stopPlayingVideo();

                    if (currentFragment instanceof ContactsFragment) {
                        ((ContactsFragment) currentFragment).onNewIntent(intentInfo);
                    } else {
                        handleFragmentNavigationUsingAdd(ContactsFragment.newInstance(intentInfo));
                    }


                    break;
                case Categories.Communication.COMMAND_COMMUNICATION_CALLS:
                    callback.changeHintBar(HINT_RESET, HINT_RESET, VISIBLE);
                    stopPlayingVideo();

                    if (currentFragment instanceof ContactsProfileFragment) {
                        ((ContactsProfileFragment) currentFragment).onNewIntent(intentInfo);
                    } else {
                        handleFragmentNavigationUsingAdd(ContactsProfileFragment.newInstance(intentInfo.getParameters()));
                    }


                    break;
                case Categories.Skills.COMMAND_SKILL_MUSIC:
                case Categories.Skills.COMMAND_SKILL_VIDEO:
                    callback.stopMusic();
                    currentFragment = handleFragmentStacking(intentInfo);
                    if (currentFragment instanceof VideoListFragment || currentFragment instanceof VideoFragment) {
                        Fragment fragment = fragmentManager.findFragmentByTag("VideoListFragment");
                        ((VideoListFragment) fragment).onNewIntent(intentInfo);
                    } else {
                        // Handling cases when Video is not being played or listed.
                        callback.resetContext();
                        String intentName = intentInfo.getIntentName();
                        switch (intentName) {
                            case Categories.Skills.Video.COMMAND_ACTION_VIDEO_PLAY_ORDINAL_ALTERNATIVE:
                                callback.onResponseToUser(R.string.error_no_videos_listed);
                                break;
                            default:
                                callback.changeHintBar(HINT_RESET, HINT_RESET, VISIBLE);
                                handleFragmentNavigationUsingAdd(VideoListFragment.newInstance(intentInfo));
                                break;
                        }
                    }
                    break;
                case Categories.Skills.COMMAND_SKILL_NEWS:
                    callback.changeHintBar(HINT_RESET, HINT_RESET, VISIBLE);
                    callback.stopMusic();
                    stopPlayingVideo();
                    handleNewsSwitch(intentInfo, currentFragment);
                    break;

                case Categories.Skills.COMMAND_SKILL_WEATHER:
                    stopPlayingVideo();
                    if (currentFragment instanceof WeatherFragment) {
                        ((WeatherFragment) currentFragment).onNewIntent(intentInfo);
                    } else {
                        handleFragmentNavigationUsingAdd(WeatherFragment.newInstance(intentInfo));
                    }
                    break;

                case Categories.Devices.COMMAND_DEVICES_LIGHTS:
                    callback.changeHintBar(HINT_RESET, HINT_RESET, VISIBLE);
                    stopPlayingVideo();
                    if (intentInfo.getAction() == Categories.Devices.Light.LIGHTS_SHOW) {
                        if (currentFragment instanceof DeviceLauncherFragment) {
                            ((DeviceLauncherFragment) currentFragment).onNewIntent(intentInfo);
                        } else {
                            handleFragmentNavigationUsingAdd(DeviceLauncherFragment.newInstance(intentInfo));
                        }
                    } else if (intentInfo.getAction() == Categories.Devices.Light.LIGHTS_SEARCH ||
                            intentInfo.getAction() == Categories.Devices.Light.LIGHTS_REGISTER) {
                        if (currentFragment instanceof DeviceDiscoveryFragment) {
                            ((DeviceDiscoveryFragment) currentFragment).onNewIntent(intentInfo);
                        } else {
                            handleFragmentNavigationUsingAdd(DeviceDiscoveryFragment.newInstance(intentInfo));
                        }
                    } else if (intentInfo.getAction() == Categories.Devices.Light.LIGHTS_REGISTER_AUTO) {
                        if (currentFragment instanceof DeviceDiscoveryFragment) {
                            ((DeviceDiscoveryFragment) currentFragment).onNewIntent(intentInfo);
                        }
                    } else if (intentInfo.getAction() == Categories.Devices.Light.LIGHTS_ALL_TOGGLE_ON ||
                            intentInfo.getAction() == Categories.Devices.Light.LIGHTS_ALL_TOGGLE_OFF ||
                            intentInfo.getAction() == Categories.Devices.Light.LIGHTS_ALL_BRIGHTNESS ||
                            intentInfo.getAction() == Categories.Devices.Light.LIGHTS_ALL_HUE ||
                            intentInfo.getAction() == Categories.Devices.Light.LIGHTS_SCENE ||
                            intentInfo.getAction() == Categories.Devices.Light.LIGHTS_INC_BRIGHTNESS ||
                            intentInfo.getAction() == Categories.Devices.Light.LIGHTS_DEC_BRIGHTNESS) {
                        if (currentFragment instanceof DeviceLauncherFragment) {
                            ((DeviceLauncherFragment) currentFragment).onNewIntent(intentInfo);
                        } else {
                            handleFragmentNavigationUsingAdd(DeviceLauncherFragment.newInstance(intentInfo));
                        }
                    }
                    break;
                case Categories.Controls.COMMAND_CONTROLS_SYSTEM:
                    callback.controlVolume(intentInfo);
                    break;

                case Categories.Controls.COMMAND_CONTROLS_ALERT:
                    if (currentFragment instanceof NotificationFragment) {
                        ((NotificationFragment) currentFragment).dismissNotification();
                    }
                    break;

                case Categories.Skills.COMMAND_SKILL_TRANSLATE:

                    if (currentFragment instanceof TranslationFragment) {
                        ((TranslationFragment) currentFragment).onNewIntent(intentInfo);
                    } else {
                        handleFragmentNavigationUsingAdd(TranslationFragment.newInstance(intentInfo));
                    }
                    break;

            }
        }
    }

    private void handleTimer(Fragment currentFragment, IntentInfo intentInfo) {
        if (intentInfo.getAction() == Categories.Skills.Timer.TIMER_SHOW ||
                intentInfo.getAction() == Categories.Skills.Timer.TIMER_SET) {
            if (currentFragment instanceof TimerFragment) {
                ((TimerFragment) currentFragment).onNewIntent(intentInfo);
            } else {
                handleFragmentNavigationUsingAdd(TimerFragment.newInstance(intentInfo));
            }
        }

        if (intentInfo.getAction() == Categories.Skills.Timer.TIMER_STOP) {
            if (currentFragment instanceof TimerFragment) {
                ((TimerFragment) currentFragment).onNewIntent(intentInfo);
            } else {
                callback.controlTimerStop();
            }
        }
    }

    /**
     * Handles the Fragment Stacking according to the IntentInfo
     *
     * @param intentInfo Instance of IntentInfo, action to be performed.
     * @return Fragment
     */
    private Fragment handleFragmentStacking(IntentInfo intentInfo) {
        if (callback != null) {
            Fragment currentFragment = callback.getCurrentFragment();
            if (currentFragment instanceof FeatureFragment && !(currentFragment instanceof HomeFragment) && intentInfo != null) {
                String subCategory = intentInfo.getSubCategory();
                if (subCategory != null) {
                    if (((FeatureFragment) currentFragment).getCategory() != null &&
                            subCategory.contentEquals(((FeatureFragment) currentFragment).getCategory())) {
                        return currentFragment;
                    }
                }
            }
            while (callback.getBackStackEntryCount() >= 2 && shouldPopBackstack(currentFragment, intentInfo)) {
                callback.handleBackNavigation();
                currentFragment = callback.getCurrentFragment();
            }
            return currentFragment;
        } else return null;
    }

    /**
     * Handles the fragment navigation using ADD before changing the fragment
     *
     * @param featureFragment Instance of FeatureFragment
     */
    public void handleFragmentNavigationUsingAdd(FeatureFragment featureFragment) {
        if (callback != null) {
            Fragment currentFragment = callback.getCurrentFragment();
            if (featureFragment != null) {
                if (currentFragment == null || (!featureFragment.getClass().equals(currentFragment.getClass()))) {
                    handleFragmentStacking(featureFragment.getIntentInfo());
                    callback.getBackStackEntryCount();
                    callback.changeFragmentStackUsingAdd(featureFragment);
                }
            }
        }
    }

    /**
     * Handles the fragment navigation using REPLACE before changing the fragment
     *
     * @param featureFragment Instance of FeatureFragment
     */
    public void handleFragmentNavigationUsingReplace(FeatureFragment featureFragment) {
        if (callback != null) {
            Fragment currentFragment = callback.getCurrentFragment();
            if (currentFragment == null || (!featureFragment.getClass().equals(currentFragment.getClass()))) {
                callback.changeFragmentStackUsingReplace(featureFragment);
            }
        }
    }

    private boolean shouldPopBackstack(Fragment currentFragment, IntentInfo intentInfo) {
        boolean popBackstack = true;
        if (intentInfo != null) {
            // FIXME Hard coded intent name
            if (intentInfo.getIntentName() != null && intentInfo.getIntentName().toLowerCase().contentEquals("livefeed")) {
                popBackstack = false;
            }
            if (currentFragment instanceof VideoListFragment || currentFragment instanceof VideoFragment) {
                popBackstack = false;
            }

            if (currentFragment instanceof DeviceDiscoveryFragment || currentFragment instanceof DeviceLauncherFragment
                    || currentFragment instanceof PhilipsHueFragment) {
                popBackstack = false;
            }

            if (intentInfo.getSubCategory() != null && (intentInfo.getSubCategory().contains(Categories.Controls.COMMAND_CONTROLS_SYSTEM) || intentInfo.getSubCategory().contains("hub.skills.qna"))) {
                popBackstack = currentFragment instanceof NewsDetailFragment || currentFragment instanceof NewsAudioFragment;
            }
        } else {
            if (currentFragment instanceof SettingsFragment || currentFragment instanceof DeviceSettingsFragment) {
                popBackstack = false;
            }
            if (currentFragment instanceof NewsHeadlineFragment) {
                popBackstack = false;
            }

            if (currentFragment instanceof NewsDetailFragment) {
                popBackstack = false;
            }

            if (currentFragment instanceof VideoListFragment) {
                popBackstack = false;
            }

            if (currentFragment instanceof SurveillanceFragment) {
                popBackstack = false;
            }
        }
        return popBackstack;
    }

    /**
     * Handles the News switch fragment
     *
     * @param intentInfo      IntentInfo to handle the next action
     * @param currentFragment Current fragment visible on the device
     */
    private void handleNewsSwitch(IntentInfo intentInfo, Fragment currentFragment) {
        String type = "";
        if (intentInfo.getParameters().containsKey("type")) {
            type = (String) intentInfo.getParameters().get("type");
        }
        if (type != null) {
            if (intentInfo.getAction() == Categories.Skills.News.NEWS_CONTROL_PLAY
                    || intentInfo.getAction() == Categories.Skills.News.NEWS_CONTROL_STOP
                    || intentInfo.getAction() == Categories.Skills.News.NEWS_CONTROL_PAUSE
                    || (intentInfo.getAction() == Categories.Skills.News.NEWS_CONTROL_LAUNCH
                    && type.contains("brief"))) {
                navigateNewsAudio(intentInfo, currentFragment);
            } else if (intentInfo.getAction() == Categories.Skills.News.NEWS_CONTROL_LAUNCH
                    || intentInfo.getAction() == Categories.Skills.News.NEWS_CONTROL_LOCATION
                    || intentInfo.getAction() == Categories.Skills.News.NEWS_CONTROL_SEARCH
                    || intentInfo.getAction() == Categories.Skills.News.NEWS_CONTROL_NEXT
                    || intentInfo.getAction() == Categories.Skills.News.NEWS_CONTROL_PREVIOUS) {
                navigateNewsHeadline(intentInfo, currentFragment);
            }
        }
    }

    /**
     * Handles the navigation of News Audio
     *
     * @param intentInfo      IntentInfo to handle the next action
     * @param currentFragment Current fragment visible on the device
     */
    private void navigateNewsAudio(IntentInfo intentInfo, Fragment currentFragment) {
        if (currentFragment instanceof NewsAudioFragment) {
            ((NewsAudioFragment) currentFragment).onNewIntent(intentInfo);
        } else {
            handleFragmentNavigationUsingAdd(NewsAudioFragment.newInstance(intentInfo));
        }
    }

    /**
     * Handles the navigation of News Headline
     *
     * @param intentInfo      IntentInfo to handle the next action
     * @param currentFragment Current fragment visible on the device
     */
    private void navigateNewsHeadline(IntentInfo intentInfo, Fragment currentFragment) {
        if (currentFragment instanceof NewsAudioFragment) {
            currentFragment.onDestroy();
        }
        if (currentFragment instanceof NewsHeadlineFragment) {
            ((NewsHeadlineFragment) currentFragment).onNewIntent(intentInfo);
        } else {
            handleFragmentNavigationUsingAdd(NewsHeadlineFragment.newInstance(intentInfo));
        }
    }

    /**
     * Handles the player commands for News, youtube or music whichever is visible on the device
     *
     * @param intentInfo      IntentInfo to handle the next action like play, pause, resume
     * @param currentFragment Current fragment visible on the device
     */
    private void handlePlayerCommands(IntentInfo intentInfo, Fragment currentFragment) {
        // Handle in individual fragment
        if (currentFragment instanceof VideoFragment) {
            ((VideoFragment) currentFragment).processActions(intentInfo);
            return;
        }
        if (currentFragment instanceof NewsAudioFragment) {
            ((NewsAudioFragment) currentFragment).onNewIntent(intentInfo);
            return;
        }
        if (currentFragment instanceof NewsHeadlineFragment) {
            ((NewsHeadlineFragment) currentFragment).onNewIntent(intentInfo);
            return;
        }
        if (currentFragment instanceof NewsDetailFragment) {
            ((NewsDetailFragment) currentFragment).onNewIntent(intentInfo);
            return;
        }

        if (intentInfo.getAction() == Categories.Controls.Player.PLAYER_PAUSE) {
            callback.pauseMusic();
            return;
        }

        if (intentInfo.getAction() == Categories.Controls.Player.PLAYER_RESUME) {
            callback.resumeMusic();
            return;
        }

        if (intentInfo.getAction() == Categories.Controls.Player.PLAYER_STOP) {
            if (callback.musicIsPlaying()) {
                if (currentFragment instanceof MusicFragment) {
                    ((MusicFragment) currentFragment).controlMusicStop();
                } else {
                    callback.stopMusic();
                }
            }
            return;
        }

        if (intentInfo.getAction() == Categories.Controls.Player.PLAYER_NEXT) {
            callback.nextMusic();
            return;
        }

        if (intentInfo.getAction() == Categories.Controls.Player.PLAYER_PREVIOUS) {
            callback.previousMusic();
            return;
        }

        if (intentInfo.getAction() == Categories.Controls.Player.PLAYER_PLAY) {
            if (!callback.musicIsPlaying()) {
                callback.resumeMusic();
                handleFragmentNavigationUsingAdd(MusicFragment.newInstance(null));
            }
        }
    }

    /**
     * Resumes the Video if it was playing
     */
    public void resumePlayingVideo() {
        if (callback != null) {
            Fragment currentFragment = callback.getCurrentFragment();
            if (currentFragment instanceof VideoFragment) {
                currentFragment.onResume();
            }
        }
    }

    /**
     * Stops the video if video is playing
     */
    public void stopPlayingVideo() {
        if (callback != null) {
            Fragment currentFragment = callback.getCurrentFragment();
            while (currentFragment instanceof VideoListFragment || currentFragment instanceof VideoFragment) {
                callback.popBackStackImmediate();
                currentFragment = callback.getCurrentFragment();
            }
        }
    }

    /**
     * Stops the alarm if its ringing
     */
    public void stopRingingAlarm() {
        if (callback != null) {
            Fragment currentFragment = callback.getCurrentFragment();
            if (currentFragment instanceof AlarmAlertFragment) {
                (currentFragment).onDestroy();
            }
        }
    }

    public void onHomePressed() {
        if (callback != null) {
            Fragment currentFragment = callback.getCurrentFragment();
            while (!(currentFragment instanceof HomeFragment)) {
                callback.handleBackNavigation();
                currentFragment = callback.getCurrentFragment();
            }
        }
    }

    private MusicFragment getMusicFragment() {
        if (callback != null) {
            Fragment currentFragment = callback.getCurrentFragment();
            if (currentFragment instanceof MusicFragment) {
                return (MusicFragment) currentFragment;
            } else return null;
        } else return null;
    }

    public void onSpotifyMusicLoggedIn() {
        if (getMusicFragment() != null) {
            getMusicFragment().onLoggedIn();
        }
    }

    public void onSpotifyMusicLoginFailed() {
        if (getMusicFragment() != null) {
            getMusicFragment().onLoginFailed();
        }
    }

    public void onSpotifyMusicLoggedOut() {
        if (getMusicFragment() != null) {
            getMusicFragment().onLoggedOut();
        }
    }

    public void onSpotifyMusicPlaybackError(Error error) {
        if (getMusicFragment() != null) {
            getMusicFragment().onPlaybackError(error);
        }
    }

    public void onSpotifyMusicPlaying() {
        if (getMusicFragment() != null) {
            getMusicFragment().onPlaying();
        }
    }

    public void onSpotifyMusicPaused() {
        if (getMusicFragment() != null) {
            getMusicFragment().onPaused();
        }
    }

    public void onSpotifyMusicNextTrack() {
        if (getMusicFragment() != null) {
            getMusicFragment().onNextTrack();
        }
    }

    public void onSpotifyMusicPreviousTrack() {
        if (getMusicFragment() != null) {
            getMusicFragment().onPreviousTrack();
        }
    }

    public void onSpotifyMusicShuffleOn() {
        if (getMusicFragment() != null) {
            getMusicFragment().onShuffleOn();
        }
    }

    public void onSpotifyMusicShuffleOff() {
        if (getMusicFragment() != null) {
            getMusicFragment().onShuffleOff();
        }
    }

    public void onSpotifyMusicRepeatOn() {
        if (getMusicFragment() != null) {
            getMusicFragment().onRepeatOn();
        }
    }

    public void onSpotifyMusicRepeatOff() {
        if (getMusicFragment() != null) {
            getMusicFragment().onRepeatOff();
        }
    }

    public void onSpotifyMusicStopped() {
        if (getMusicFragment() != null) {
            getMusicFragment().onStopped();
        }
    }

    public void onSpotifyMusicTrackChanged() {
        if (getMusicFragment() != null) {
            getMusicFragment().onTrackChanged();
        }
    }

    public void onSpotifyMusicTrackDelivered() {
        if (getMusicFragment() != null) {
            getMusicFragment().onTrackDelivered();
        }
    }

    public void onSpotifyMusicMetaDataChanged() {
        if (getMusicFragment() != null) {
            getMusicFragment().onMetadataChanged();
        }
    }

    public void onSpotifyMusicTakeData(Metadata metadata) {
        if (getMusicFragment() != null && metadata != null) {
            getMusicFragment().takeDataToRespondUser(metadata);
        }
    }

    public void onSpotifyMusicActionCompleted() {
        if (getMusicFragment() != null) {
            getMusicFragment().onActionComplete();
        }
    }

    public void onSpotifyMusicActionCancelled() {
        if (getMusicFragment() != null) {
            getMusicFragment().onActionCancelled();
        }
    }

    public void handleNewsSpeechCallback(boolean isDetailedNews) {
        if (callback != null) {
            Fragment currentFragment = callback.getCurrentFragment();
            if (currentFragment instanceof NewsHeadlineFragment) {
                if (!((NewsHeadlineFragment) currentFragment).isLastPage()) {
                    ((NewsHeadlineFragment) currentFragment).proceedToNextPage();
                } else {
                    callback.handleBackNavigation();
                }
            }
        }
    }

    public void alertAlarm(Intent intent, String key) throws IOException, ClassNotFoundException {
        if (callback != null) {
            Fragment currentFragment = callback.getCurrentFragment();
            if (key.equals(TimerFragment.TIMER_KEY)) {
                if (Objects.requireNonNull(intent.getExtras()).getSerializable(TimerService.TIMER_DATA) != null) {
                    Timer timer = (Timer) Objects.requireNonNull(intent.getExtras()).getSerializable(TimerService.TIMER_DATA);
                    if (shouldShowAlert()) {
                        if (currentFragment instanceof NotificationFragment) {
                            ((NotificationFragment) currentFragment).updateNotification(timer);
                        } else {
                            handleFragmentNavigationUsingAdd(NotificationFragment.newInstance(timer));
                        }
                    } else {
                        Notification notification = NotificationFragment.getNotification(timer);
                        notificationList.add(0, notification);
                    }
                }
            } else {
                if (intent != null) {
                    Bundle alertBundle = intent.getExtras();
                    byte[] byteArrayInputStream;
                    if (alertBundle != null) {
                        byteArrayInputStream = alertBundle.getByteArray(key);
                        if (byteArrayInputStream != null) {
                            ByteArrayInputStream byteArrayInput = new ByteArrayInputStream(byteArrayInputStream);
                            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInput);
                            if (key.equals(SetReminder.REMINDER_KEY)) {
                                Reminder reminder = (Reminder) objectInputStream.readObject();
                                if (reminder != null) {
                                    if (shouldShowAlert()) {
                                        if (currentFragment instanceof NotificationFragment) {
                                            ((NotificationFragment) currentFragment).updateNotification(reminder);
                                        } else {
                                            handleFragmentNavigationUsingAdd(NotificationFragment.newInstance(reminder));
                                        }
                                    } else {
                                        Notification notification = NotificationFragment.getNotification(reminder);
                                        notificationList.add(0, notification);
                                    }
                                }
                            } else {
                                Alarm alarm = (Alarm) objectInputStream.readObject();
                                if (alarm != null) {
                                    if (shouldShowAlert()) {
                                        if (currentFragment instanceof NotificationFragment) {
                                            ((NotificationFragment) currentFragment).updateNotification(alarm);
                                        } else {
                                            handleFragmentNavigationUsingAdd(NotificationFragment.newInstance(alarm));
                                        }
                                    } else {
                                        Notification notification = NotificationFragment.getNotification(alarm);
                                        notificationList.add(0, notification);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void showNotificationsIfAny() {
        if (callback != null) {
            Fragment currentFragment = callback.getCurrentFragment();
            if (notificationList.size() != 0) {
                if (currentFragment instanceof NotificationFragment) {
                    ((NotificationFragment) currentFragment).showAllNotifications(notificationList);
                } else {
                    callback.changeFragmentStackUsingAdd(NotificationFragment.newInstance(notificationList));
                }
                notificationList.clear();
            }
        }
    }

    private boolean shouldShowAlert() {
        if (callback != null) {
            Fragment currentFragment = callback.getCurrentFragment();
            return !(currentFragment instanceof VideoCalling);
        } else return false;
    }

    public interface SwitchFragmentCallback {
        void changeHintBar(int hintText, int backgroundColour, int visibility);

        void changeFragmentStackUsingAdd(FeatureFragment featureFragment);

        void changeFragmentStackUsingReplace(FeatureFragment featureFragment);

        boolean musicIsPlaying();

        void pauseMusic();

        void nextMusic();

        void previousMusic();

        void resumeMusic();

        void stopMusic();

        void handleBackNavigation();

        void onResponseToUser(Object response);

        void controlVolume(IntentInfo intentInfo);

        Fragment getCurrentFragment();

        int getBackStackEntryCount();

        void popBackStackImmediate();

        void resetContext();

        void controlTimerStop();
    }
}
