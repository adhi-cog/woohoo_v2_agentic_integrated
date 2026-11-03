package com.sb.woohoo.ui.main;

import android.util.Log;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.main.fragments.alarm.pojo.Alarm;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.ContactsData;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.StaffMember;
import com.sb.woohoo.ui.main.fragments.qna.pojo.WolframData;
import com.sb.woohoo.ui.main.fragments.reminders.pojo.Reminder;
import com.sb.woohoo.ui.main.fragments.todo.pojo.ToDoItem;
import com.sb.woohoo.ui.main.fragments.video.pojo.Video;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.pojo.nlp.SpeechResponse;

import java.util.HashMap;

import javax.inject.Inject;

/**
 * Created by Shrey on 17/03/18.
 */

@SuppressWarnings("unchecked")
public class IntentProcessor {
    private final String TAG = IntentProcessor.class.getSimpleName();
    private HashMap<String, HashMap<String, HashMap<String, IntentInfo>>> intentRegistry = new HashMap<>();

    @SuppressWarnings("WeakerAccess")
    @Inject
    public IntentProcessor() {
        LoggerWrapper.i(IntentProcessor.class.getSimpleName(), "Constructor");
        registerIntents();
    }

    /**
     * Register Intents to {@code intentRegistry}.
     */
    @SuppressWarnings("WeakerAccess")
    public void registerIntents() {
        intentRegistry.put(Categories.COMMAND_CATEGORY_SKILLS, registerSubCategoriesSkill());
        intentRegistry.put(Categories.COMMAND_CATEGORY_CONTROLS, registerSubCategoriesControls());
        intentRegistry.put(Categories.COMMAND_CATEGORY_COMMUNICATION, registerSubCategoriesCommunications());
        intentRegistry.put(Categories.COMMAND_CATEGORY_DEVICE, registerSubCategoriesDevices());
    }

    /**
     * Registers Skill Sub-Category Intents.
     *
     * @return {@link HashMap} of Skill Sub-Category.
     */
    private HashMap<String, HashMap<String, IntentInfo>> registerSubCategoriesSkill() {
        HashMap<String, HashMap<String, IntentInfo>> subCategorySkills = new HashMap<>();
        subCategorySkills.put(Categories.Skills.COMMAND_SKILL_REMINDER, registerReminderActions());
        subCategorySkills.put(Categories.Skills.COMMAND_SKILL_ALARM, registerAlarmActions());
        subCategorySkills.put(Categories.Skills.COMMAND_SKILL_TODO, registerToDoActions());
        subCategorySkills.put(Categories.Skills.COMMAND_SKILL_TIMER, registerTimerActions());
        subCategorySkills.put(Categories.Skills.COMMAND_SKILL_QNA, registerQnaActions());
        subCategorySkills.put(Categories.Skills.COMMAND_SKILL_MUSIC, registerMusicActions());
        subCategorySkills.put(Categories.Skills.COMMAND_SKILL_VIDEO, registerVideoActions());
        subCategorySkills.put(Categories.Skills.COMMAND_SKILL_NEWS, registerNewsAudioActions());
        subCategorySkills.put(Categories.Skills.COMMAND_SKILL_WEATHER, registerWeatherActions());
        subCategorySkills.put(Categories.Skills.COMMAND_SKILL_CAMERA, registerCameraActions());
        subCategorySkills.put(Categories.Skills.COMMAND_SKILL_TRANSLATE, registerTranslateActions());

        return subCategorySkills;
    }

    private HashMap<String, HashMap<String, IntentInfo>> registerSubCategoriesDevices() {
        HashMap<String, HashMap<String, IntentInfo>> subCategoryDevices = new HashMap<>();
        subCategoryDevices.put(Categories.Devices.COMMAND_DEVICES_LIGHTS, registerLightActions());
        return subCategoryDevices;
    }

    private HashMap<String, IntentInfo> registerLightActions() {
        HashMap<String, IntentInfo> lightActions = new HashMap<>();
        lightActions.put(Categories.Devices.Light.COMMAND_ACTION_SHOW, new IntentInfo(Categories.Devices.Light.LIGHTS_SHOW));
        lightActions.put(Categories.Devices.Light.COMMAND_ACTION_LIGHTS_SEARCH, new IntentInfo(Categories.Devices.Light.LIGHTS_SEARCH));
        lightActions.put(Categories.Devices.Light.COMMAND_ACTION_LIGHTS_REGISTER, new IntentInfo(Categories.Devices.Light.LIGHTS_REGISTER));
        lightActions.put(Categories.Devices.Light.COMMAND_ACTION_LIGHTS_ALL_TOGGLE_OFF, new IntentInfo(Categories.Devices.Light.LIGHTS_ALL_TOGGLE_OFF));
        lightActions.put(Categories.Devices.Light.COMMAND_ACTION_LIGHTS_ALL_TOGGLE_ON, new IntentInfo(Categories.Devices.Light.LIGHTS_ALL_TOGGLE_ON));
        lightActions.put(Categories.Devices.Light.COMMAND_ACTION_LIGHTS_ALL_BRIGHTNESS, new IntentInfo(Categories.Devices.Light.LIGHTS_ALL_BRIGHTNESS));
        lightActions.put(Categories.Devices.Light.COMMAND_ACTION_LIGHTS_ALL_HUE, new IntentInfo(Categories.Devices.Light.LIGHTS_ALL_HUE));
        lightActions.put(Categories.Devices.Light.COMMAND_ACTION_LIGHTS_REGISTER_AUTO, new IntentInfo(Categories.Devices.Light.LIGHTS_REGISTER_AUTO));
        lightActions.put(Categories.Devices.Light.COMMAND_ACTION_LIGHTS_SCENE, new IntentInfo(Categories.Devices.Light.LIGHTS_SCENE));
        lightActions.put(Categories.Devices.Light.COMMAND_ACTION_LIGHTS_INC_BRIGHTNESS, new IntentInfo(Categories.Devices.Light.LIGHTS_INC_BRIGHTNESS));
        lightActions.put(Categories.Devices.Light.COMMAND_ACTION_LIGHTS_DEC_BRIGHTNESS, new IntentInfo(Categories.Devices.Light.LIGHTS_DEC_BRIGHTNESS));

        return lightActions;
    }

    private HashMap<String, IntentInfo> registerVideoActions() {
        HashMap<String, IntentInfo> videoActions = new HashMap<>();
        videoActions.put(Categories.Skills.Video.COMMAND_ACTION_VIDEO_SEARCH, new IntentInfo(new Video(), Categories.Skills.Video.VIDEO_SEARCH));
        videoActions.put(Categories.Skills.Video.COMMAND_ACTION_VIDEO_AUTOPLAY, new IntentInfo(new Video(), Categories.Skills.Video.VIDEO_AUTOPLAY));
        videoActions.put(Categories.Skills.Video.COMMAND_ACTION_VIDEO_PLAY_ORDINAL, new IntentInfo(new Video(), Categories.Skills.Video.VIDEO_PLAY_ORDINAL));
        videoActions.put(Categories.Skills.Video.COMMAND_ACTION_VIDEO_PLAY_ORDINAL_ALTERNATIVE, new IntentInfo(new Video(), Categories.Skills.Video.VIDEO_PLAY_ORDINAL_ALTERNATIVE));
        videoActions.put(Categories.Skills.Video.COMMAND_ACTION_VIDEO_LAUNCH, new IntentInfo(new Video(), Categories.Skills.Video.VIDEO_LAUNCH));
        videoActions.put(Categories.Skills.Video.COMMAND_ACTION_VIDEO_LAUNCH_AUTOPLAY, new IntentInfo(new Video(), Categories.Skills.Video.VIDEO_LAUNCH_AUTOPLAY));
        videoActions.put(Categories.Skills.Video.COMMAND_ACTION_VIDEO_CONTROLS_PLAY, new IntentInfo(new Video(), Categories.Skills.Video.VIDEO_CONTROLS_PLAY));
        videoActions.put(Categories.Skills.Video.COMMAND_ACTION_VIDEO_CONTROLS_PAUSE, new IntentInfo(new Video(), Categories.Skills.Video.VIDEO_CONTROLS_PAUSE));
        videoActions.put(Categories.Skills.Video.COMMAND_ACTION_VIDEO_CONTROLS_BACK, new IntentInfo(new Video(), Categories.Skills.Video.VIDEO_CONTROLS_BACK));
        videoActions.put(Categories.Skills.Video.COMMAND_ACTION_VIDEO_CONTROLS_BACK_ALTERNATIVE, new IntentInfo(new Video(), Categories.Skills.Video.VIDEO_CONTROLS_BACK_ALTERNATIVE));
        return videoActions;
    }

    private HashMap<String, IntentInfo> registerWeatherActions() {
        HashMap<String, IntentInfo> weatherActions = new HashMap<>();
        weatherActions.put(Categories.Skills.Weather.COMMAND_ACTION_WEATHER_SEARCH, new IntentInfo(Categories.Skills.Weather.WEATHER_SEARCH));
        weatherActions.put(Categories.Skills.Weather.COMMAND_ACTION_WEATHER_OUTFIT, new IntentInfo(Categories.Skills.Weather.WEATHER_OUTFIT));
        weatherActions.put(Categories.Skills.Weather.COMMAND_ACTION_WEATHER_CONDITION, new IntentInfo(Categories.Skills.Weather.WEATHER_CONDITION));
        weatherActions.put(Categories.Skills.Weather.COMMAND_ACTION_WEATHER_SHOW, new IntentInfo(Categories.Skills.Weather.WEATHER_SHOW));
        return weatherActions;
    }

    private HashMap<String, IntentInfo> registerCameraActions() {
        HashMap<String, IntentInfo> cameraActions = new HashMap<>();
        cameraActions.put(Categories.Skills.Camera.COMMAND_ACTION_SELFIE, new IntentInfo(Categories.Skills.Camera.CAMERA_SELFIE));
        return cameraActions;
    }

    private HashMap<String, IntentInfo> registerTranslateActions() {
        HashMap<String, IntentInfo> translateActions = new HashMap<>();
        translateActions.put(Categories.Skills.Translate.COMMAND_ACTION_TRANSLATE, new IntentInfo(Categories.Skills.Translate.TRANSLATE_LANG));
        return translateActions;
    }


    /**
     * Register Reminder Actions to Skill Sub-Category Intents.
     *
     * @return {@link HashMap} of Reminder Actions.
     */
    private HashMap<String, IntentInfo> registerReminderActions() {
        HashMap<String, IntentInfo> reminderActions = new HashMap<>();
        reminderActions.put(Categories.Skills.Reminder.COMMAND_ACTION_SHOW, new IntentInfo(new Reminder(), Categories.Skills.Reminder.REMINDER_SHOW));
        reminderActions.put(Categories.Skills.Reminder.COMMAND_ACTION_ADD, new IntentInfo(new Reminder(), Categories.Skills.Reminder.REMINDER_ADD));
        reminderActions.put(Categories.Skills.Reminder.COMMAND_ACTION_DELETE_ALL, new IntentInfo(new Reminder(), Categories.Skills.Reminder.REMINDER_DELETE_ALL));
        reminderActions.put(Categories.Skills.Reminder.COMMAND_ACTION_DELETE_TITLE, new IntentInfo(new Reminder(), Categories.Skills.Reminder.REMINDER_DELETE_TITLE));
        reminderActions.put(Categories.Skills.Reminder.COMMAND_ACTION_EDIT, new IntentInfo(new Reminder(), Categories.Skills.Reminder.REMINDER_EDIT));
        reminderActions.put(Categories.Skills.Reminder.COMMAND_ACTION_DELETE_TITLE_ALTERNATIVE, new IntentInfo(new Reminder(), Categories.Skills.Reminder.REMINDER_DELETE_TITLE_ALTERNATIVE));
        reminderActions.put(Categories.Skills.Reminder.COMMAND_ACTION_EDIT_ALTERNATIVE, new IntentInfo(new Reminder(), Categories.Skills.Reminder.REMINDER_EDIT_ALTERNATIVE));
        return reminderActions;
    }

    /**
     * Register AlarmFragment Actions to Skill Sub-Category Intents.
     *
     * @return {@link HashMap} of AlarmFragment Actions.
     */
    private HashMap<String, IntentInfo> registerAlarmActions() {
        HashMap<String, IntentInfo> alarmActions = new HashMap<>();
        alarmActions.put(Categories.Skills.Alarm.COMMAND_ACTION_SHOW, new IntentInfo(new Alarm(), Categories.Skills.Alarm.ALARM_SHOW));
        alarmActions.put(Categories.Skills.Alarm.COMMAND_ACTION_ADD, new IntentInfo(new Alarm(), Categories.Skills.Alarm.ALARM_ADD));
        alarmActions.put(Categories.Skills.Alarm.COMMAND_ACTION_DELETE_ALL, new IntentInfo(new Alarm(), Categories.Skills.Alarm.ALARM_DELETE_ALL));
        alarmActions.put(Categories.Skills.Alarm.COMMAND_ACTION_DELETE_ORDINAL, new IntentInfo(new Alarm(), Categories.Skills.Alarm.ALARM_DELETE_ORDINAL));
        alarmActions.put(Categories.Skills.Alarm.COMMAND_ACTION_SNOOZE, new IntentInfo(new Alarm(), Categories.Skills.Alarm.ALARM_SNOOZE));

        return alarmActions;
    }

    /**
     * Register To Do Actions to Skill Sub-Category Intents.
     *
     * @return {@link HashMap} of To Do Actions.
     */
    private HashMap<String, IntentInfo> registerToDoActions() {
        HashMap<String, IntentInfo> todoActions = new HashMap<>();
        todoActions.put(Categories.Skills.ToDo.COMMAND_ACTION_SHOW, new IntentInfo(new ToDoItem(), Categories.Skills.ToDo.TODO_SHOW));
        todoActions.put(Categories.Skills.ToDo.COMMAND_ACTION_ADD, new IntentInfo(new ToDoItem(), Categories.Skills.ToDo.TODO_ADD));
        todoActions.put(Categories.Skills.ToDo.COMMAND_ACTION_DELETE_ORDINAL, new IntentInfo(new ToDoItem(), Categories.Skills.ToDo.TODO_DELETE_ORDINAL));
        todoActions.put(Categories.Skills.ToDo.COMMAND_ACTION_DELETE_ALL, new IntentInfo(new ToDoItem(), Categories.Skills.ToDo.TODO_DELETE_ALL));
        return todoActions;
    }

    /**
     * Register Timer Actions to Skill Sub-Category Intents.
     *
     * @return {@link HashMap} of Timer Actions.
     */
    private HashMap<String, IntentInfo> registerTimerActions() {
        HashMap<String, IntentInfo> timerActions = new HashMap<>();
        timerActions.put(Categories.Skills.Timer.COMMAND_ACTION_SHOW, new IntentInfo(Categories.Skills.Timer.TIMER_SHOW));
        timerActions.put(Categories.Skills.Timer.COMMAND_ACTION_SET, new IntentInfo(Categories.Skills.Timer.TIMER_SET));
        timerActions.put(Categories.Skills.Timer.COMMAND_ACTION_STOP, new IntentInfo(Categories.Skills.Timer.TIMER_STOP));
        return timerActions;
    }

    /**
     * Register QnA Actions to Skill Sub-Category Intents.
     *
     * @return {@link HashMap} of QnA Actions.
     */
    private HashMap<String, IntentInfo> registerQnaActions() {
        HashMap<String, IntentInfo> qnaAction = new HashMap<>();
        qnaAction.put(Categories.Skills.QnA.COMMAND_ACTION_QUERY, new IntentInfo(new WolframData(), Categories.Skills.QnA.QNA_QUERY));
        qnaAction.put(Categories.Skills.QnA.COMMAND_ACTION_STOP, new IntentInfo(new WolframData(), Categories.Skills.QnA.QNA_STOP));

        qnaAction.put(Categories.Skills.QnA.COMMAND_ACTION_CURRENT_TIME, new IntentInfo(new WolframData(), Categories.Skills.QnA.QNA_CURRENT_TIME));
        qnaAction.put(Categories.Skills.QnA.COMMAND_ACTION_CURRENT_DATE, new IntentInfo(new WolframData(), Categories.Skills.QnA.QNA_CURRENT_DATE));
        return qnaAction;
    }

    /**
     * Register Music Actions to Skill Sub-Category Intents.
     *
     * @return {@link HashMap} of Music Actions.
     */
    private HashMap<String, IntentInfo> registerMusicActions() {
        HashMap<String, IntentInfo> musicAction = new HashMap<>();
        musicAction.put(Categories.Skills.Music.COMMAND_ACTION_SEARCH, new IntentInfo(Categories.Skills.Music.MUSIC_SEARCH));
        musicAction.put(Categories.Skills.Music.COMMAND_ACTION_PLAY, new IntentInfo(Categories.Skills.Music.MUSIC_PLAY));
        musicAction.put(Categories.Skills.Music.COMMAND_ACTION_CONTROL_NEXT, new IntentInfo(Categories.Skills.Music.MUSIC_CONTROL_NEXT));
        musicAction.put(Categories.Skills.Music.COMMAND_ACTION_CONTROL_PREV, new IntentInfo(Categories.Skills.Music.MUSIC_CONTROL_PREV));
        musicAction.put(Categories.Skills.Music.COMMAND_ACTION_CONTROL_PAUSE, new IntentInfo(Categories.Skills.Music.MUSIC_CONTROL_PAUSE));
        musicAction.put(Categories.Skills.Music.COMMAND_ACTION_CONTROL_REPEAT, new IntentInfo(Categories.Skills.Music.MUSIC_CONTROL_REPEAT));
        musicAction.put(Categories.Skills.Music.COMMAND_ACTION_CONTROL_STOP, new IntentInfo(Categories.Skills.Music.MUSIC_CONTROL_STOP));
        musicAction.put(Categories.Skills.Music.COMMAND_ACTION_CONTROL_PLAY, new IntentInfo(Categories.Skills.Music.MUSIC_CONTROL_PLAY));
        musicAction.put(Categories.Skills.Music.COMMAND_ACTION_CONTROL_PLAYLIST, new IntentInfo(Categories.Skills.Music.MUSIC_CONTROL_PLAYLIST));

        return musicAction;
    }

    /**
     * Registers Control Sub-Category Intents.
     *
     * @return {@link HashMap} of Control Sub-Category.
     */
    private HashMap<String, HashMap<String, IntentInfo>> registerSubCategoriesControls() {
        HashMap<String, HashMap<String, IntentInfo>> subCategoryControls = new HashMap<>();
        subCategoryControls.put(Categories.Controls.COMMAND_CONTROLS_NAVIGATION, registerNavigationActions());
        subCategoryControls.put(Categories.Controls.COMMAND_CONTROLS_PLAYER, registerPlayerActions());
        subCategoryControls.put(Categories.Controls.COMMAND_CONTROLS_SYSTEM, registerSystemActions());
        subCategoryControls.put(Categories.Controls.COMMAND_CONTROLS_ALERT, registerAlertAction());
        return subCategoryControls;
    }

    private HashMap<String, IntentInfo> registerAlertAction() {
        HashMap<String, IntentInfo> alertActions = new HashMap<>();
        alertActions.put(Categories.Controls.Alert.COMMAND_ALERT_DISMISS, new IntentInfo(Categories.Controls.Alert.ALERT_DISMISS));
        return alertActions;
    }

    /**
     * Register Navigation Actions to Control Sub-Category Intents.
     *
     * @return {@link HashMap} of Navigation Actions.
     */
    private HashMap<String, IntentInfo> registerNavigationActions() {
        HashMap<String, IntentInfo> navigationActions = new HashMap<>();
        navigationActions.put(Categories.Controls.Navigation.COMMAND_NAVIGATION_HOME, new IntentInfo(Categories.Controls.Navigation.NAVIGATION_HOME));

        return navigationActions;
    }

    private HashMap<String, IntentInfo> registerPlayerActions() {
        HashMap<String, IntentInfo> playerActions = new HashMap<>();

        playerActions.put(Categories.Controls.Player.COMMAND_PLAYER_PAUSE, new IntentInfo(Categories.Controls.Player.PLAYER_PAUSE));
        playerActions.put(Categories.Controls.Player.COMMAND_PLAYER_PLAY, new IntentInfo(Categories.Controls.Player.PLAYER_PLAY));
        playerActions.put(Categories.Controls.Player.COMMAND_PLAYER_RESUME, new IntentInfo(Categories.Controls.Player.PLAYER_RESUME));
        playerActions.put(Categories.Controls.Player.COMMAND_PLAYER_PREVIOUS, new IntentInfo(Categories.Controls.Player.PLAYER_PREVIOUS));
        playerActions.put(Categories.Controls.Player.COMMAND_PLAYER_NEXT, new IntentInfo(Categories.Controls.Player.PLAYER_NEXT));
        playerActions.put(Categories.Controls.Player.COMMAND_PLAYER_STOP, new IntentInfo(Categories.Controls.Player.PLAYER_STOP));

        return playerActions;
    }

    /**
     * Registers Communication Sub-Category Intents.
     *
     * @return {@link HashMap} of Communication Sub-Category.
     */
    private HashMap<String, HashMap<String, IntentInfo>> registerSubCategoriesCommunications() {
        HashMap<String, HashMap<String, IntentInfo>> subCategoryCommunication = new HashMap<>();
        subCategoryCommunication.put(Categories.Communication.COMMAND_COMMUNICATION_CONTACT, registerContactsAction());

        subCategoryCommunication.put(Categories.Communication.COMMAND_COMMUNICATION_CALLS, registerCallsAction());

        return subCategoryCommunication;
    }


    private HashMap<String, IntentInfo> registerSystemActions() {
        HashMap<String, IntentInfo> systemActions = new HashMap<>();
        systemActions.put(Categories.Controls.Volume.COMMAND_VOLUME_DOWN, new IntentInfo(Categories.Controls.Volume.VOLUME_DOWN));
        systemActions.put(Categories.Controls.Volume.COMMAND_VOLUME_UP, new IntentInfo(Categories.Controls.Volume.VOLUME_UP));
        systemActions.put(Categories.Controls.Volume.COMMAND_VOLUME_SET, new IntentInfo(Categories.Controls.Volume.VOLUME_SET));
        systemActions.put(Categories.Controls.Volume.COMMAND_VOLUME_MUTE, new IntentInfo(Categories.Controls.Volume.VOLUME_MUTE));
        systemActions.put(Categories.Controls.Volume.COMMAND_VOLUME_UNMUTE, new IntentInfo(Categories.Controls.Volume.VOLUME_UNMUTE));
        systemActions.put(Categories.Controls.SystemSetting.COMMAND_NIGHT_MODE_ON, new IntentInfo(Categories.Controls.SystemSetting.NIGHT_MODE_ON));
        systemActions.put(Categories.Controls.SystemSetting.COMMAND_NIGHT_MODE_OFF, new IntentInfo(Categories.Controls.SystemSetting.NIGHT_MODE_OFF));
        systemActions.put(Categories.Controls.SystemSetting.COMMAND_BLUETOOTH_ON, new IntentInfo(Categories.Controls.SystemSetting.BLUETOOTH_ON));
        systemActions.put(Categories.Controls.SystemSetting.COMMAND_BLUETOOTH_OFF, new IntentInfo(Categories.Controls.SystemSetting.BLUETOOTH_OFF));
        systemActions.put(Categories.Controls.SystemSetting.COMMAND_BLUETOOTH_OPEN, new IntentInfo(Categories.Controls.SystemSetting.BLUETOOTH_OPEN));
        systemActions.put(Categories.Controls.SystemSetting.COMMAND_BRIGHTNESS_UP, new IntentInfo(Categories.Controls.SystemSetting.BRIGHTNESS_UP));
        systemActions.put(Categories.Controls.SystemSetting.COMMAND_BRIGHTNESS_DOWN, new IntentInfo(Categories.Controls.SystemSetting.BRIGHTNESS_DOWN));
        systemActions.put(Categories.Controls.SystemSetting.COMMAND_BRIGHTNESS_SET, new IntentInfo(Categories.Controls.SystemSetting.BRIGHTNESS_SET));
        return systemActions;
    }

    /**
     * Register Contact Actions to Communication Sub-Category Intents.
     *
     * @return {@link HashMap} of Contact Actions.
     */
    private HashMap<String, IntentInfo> registerContactsAction() {
        HashMap<String, IntentInfo> contactsAction = new HashMap<>();
        contactsAction.put(
                Categories.Communication.Contacts.COMMAND_COMMUNICATION_SHOW,
                new IntentInfo(new ContactsData(), Categories.Communication.Contacts.CONTACT_SHOW)
        );

        return contactsAction;
    }

    private HashMap<String, IntentInfo> registerCallsAction() {
        HashMap<String, IntentInfo> contactsAction = new HashMap<>();

        contactsAction.put(
                Categories.Communication.VoiceCall.COMMAND_VOICE_CALL,
                new IntentInfo(Categories.Communication.VoiceCall.VOICE_CALL)
        );

        return contactsAction;
    }

    /**
     * Register News Audio Actions to Communication Sub-Category Intents.
     *
     * @return {@link HashMap} of Contact Actions.
     */
    private HashMap<String, IntentInfo> registerNewsAudioActions() {
        HashMap<String, IntentInfo> newsAudioAction = new HashMap<>();
        newsAudioAction.put(Categories.Skills.News.COMMAND_ACTION_CONTROL_PAUSE, new IntentInfo(Categories.Skills.News.NEWS_CONTROL_PAUSE));
        newsAudioAction.put(Categories.Skills.News.COMMAND_ACTION_CONTROL_PLAY, new IntentInfo(Categories.Skills.News.NEWS_CONTROL_PLAY));
        newsAudioAction.put(Categories.Skills.News.COMMAND_ACTION_CONTROL_STOP, new IntentInfo(Categories.Skills.News.NEWS_CONTROL_STOP));
        newsAudioAction.put(Categories.Skills.News.COMMAND_ACTION_LAUNCH, new IntentInfo(Categories.Skills.News.NEWS_CONTROL_LAUNCH));
        newsAudioAction.put(Categories.Skills.News.COMMAND_ACTION_SEARCH, new IntentInfo(Categories.Skills.News.NEWS_CONTROL_SEARCH));
        newsAudioAction.put(Categories.Skills.News.COMMAND_ACTION_LOCATION, new IntentInfo(Categories.Skills.News.NEWS_CONTROL_LOCATION));
        newsAudioAction.put(Categories.Skills.News.COMMAND_ACTION_CONTROL_PREVIOUS, new IntentInfo(Categories.Skills.News.NEWS_CONTROL_PREVIOUS));
        newsAudioAction.put(Categories.Skills.News.COMMAND_ACTION_CONTROL_NEXT, new IntentInfo(Categories.Skills.News.NEWS_CONTROL_NEXT));
        return newsAudioAction;
    }


    @SuppressWarnings("ConstantConditions")
    public void getIntentInfo(
            String intent,
            HashMap<String, Object> parameters,
            IntentProcessCallback intentProcessCallback,
            SpeechResponse userResponse,
            String userQuery,
            String appUrl,
            boolean explicitFollowUp) {
        String categoryIntent;
        String subCategoryIntent;
        String[] contentsIntent = intent.split("\\.");
        String errorMessage = null;

        try {
            categoryIntent = contentsIntent[0] + "." + contentsIntent[1];
            subCategoryIntent = contentsIntent[0] + "." + contentsIntent[1] + "." + contentsIntent[2];

            if (categoryIntent != null && intentRegistry.containsKey(categoryIntent)) {

                HashMap<String, HashMap<String, IntentInfo>> subCategoryRegistry = intentRegistry.get(categoryIntent);
                if (subCategoryIntent != null && subCategoryRegistry.containsKey(subCategoryIntent)) {

                    HashMap<String, IntentInfo> actionsRegistry = subCategoryRegistry.get(subCategoryIntent);
                    if (actionsRegistry.containsKey(intent)) {

                        IntentInfo intentInfo = actionsRegistry.get(intent);
                        intentInfo.setCategory(categoryIntent);
                        intentInfo.setSubCategory(subCategoryIntent);
                        // Parse model
                        if (intentInfo.getIntentPojo() != null) {
                            intentInfo.getIntentPojo().parseModel(parameters);
                        }
                        // Attach parameters as well
                        intentInfo.setParameters(parameters);
                        // Voice response to user
                        intentInfo.setUserResponse(userResponse);
                        // Voice response to user
                        intentInfo.setUserQuery(userQuery);

                        intentInfo.setAppUrl(appUrl);
                        // Intent name
                        intentInfo.setIntentName(intent);
                        // Explicit followup status
                        intentInfo.setNeedsExplicitFollowUp(explicitFollowUp);

                        intentProcessCallback.onIntentProcessed(intentInfo);
                        return;

                    } else {
                        errorMessage = "Action not available!";
                        LoggerWrapper.d(TAG, errorMessage);
                    }
                } else {
                    errorMessage = contentsIntent[1] + " not available!";
                    LoggerWrapper.d(TAG, errorMessage);
                }
            } else {
                errorMessage = contentsIntent[0] + " not available!";
                LoggerWrapper.d(TAG, errorMessage);
            }

        } catch (NullPointerException nullException) {
            errorMessage = "Error parsing intent";
            LoggerWrapper.e(TAG, nullException.getMessage());
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            indexOutOfBoundsException.printStackTrace();
        }
        if (errorMessage != null && !errorMessage.isEmpty()) {
            intentProcessCallback.onIntentProcessError(errorMessage);
        }
    }

    public interface IntentProcessCallback {

        void onIntentProcessError(String errorMessage);

        void onIntentProcessed(IntentInfo intentInfo);

    }
}
