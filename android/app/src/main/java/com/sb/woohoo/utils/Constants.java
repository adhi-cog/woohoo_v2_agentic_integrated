package com.sb.woohoo.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Interface to hold constant values.
 *
 * @author Ritwik Jamuar.
 */
public interface Constants {


    String IMAGE_BASE_URL = "https://s3-us-west-2.amazonaws.com/woohoo-enterprise/profilePictures/";

    String CHROME_UNTRUSTED_URL= "https://1.1.1.5/chromon.php?name=713&lang=en";
    String CONTACT_DETAILS = "Contact Details";

    String CKS_DOMAIN_MOCK_ROOM_ID = "72";
    String INTERNET_CHECK_SERVER = "enterprise.heywoohoo.com";
    String CONTACT_LIVE_FEED = "contactLiveFeed";
    String LIVE_FEED_AUDIO_TYPE = "audio";
    String LIVE_FEED_VIDEO_TYPE = "video";
    String HUB_DETAILS = "hub_details";
    String TABLE_CHECK = "tablecheck";

    String DARK_MODE = "darkmode";
    public static final String SERVICE_PACKAGE_NAME = "com.sb.woohoo.monitor";
    public static final String SERVICE_CLASS_PATH = SERVICE_PACKAGE_NAME + ".MyService";
    public static final String SERVICE_APK_NAME = "woohoomonitor.apk";
    String GRID_ITEM_LIST = "grid_item_list";
    int CHANNEL_PROFILE_COMMUNICATION = 100;
    String IS_FROM_LOGIN_SCREEN = "isFromLoginScreen";

    String BASE_URL_FOR_ST = "https://www.visitsaudi.com";

    /**
     * Constants for Default hour and minutes used for restart device
     */
    public static int RESTART_DEFAULT_HOUR = 4;
    public static  int RESTART_DEFAULT_MIN = 0;
    public static final String RESTART_WORK_PERIODIC_MANAGER_TAG = "restart_after_some_time";

    /**
     * Dark mode Starting point and ending point in 24 hr clock time
     *
     */

    public static String STARTING_POINT_TO_ENABLE_DARK_MODE = "22:00";

    public static String ENDING_POINT_TO_DISABLE_DARK_MODE = "7:00";


    /**
     * Interface to hold constant values for {@link MainActivity}
     */
    interface MainActivity {
        int FRAGMENT_HOME = 2;
        int FRAGMENT_CONTACTS_PROFILE = 5;
        int FRAGMENT_MUSIC = 6;
        int FRAGMENT_MEDIA = 7;
        int FRAGMENT_SETTINGS = 8;
        int FRAGMENT_NEWS_DETAIL = 10;
        int FRAGMENT_WEATHER = 12;
        int FRAGMENT_DEVICE_SETTING = 13;
        int FRAGMENT_CAMERA_CONTROL = 14;
        int FRAGMENT_DEVICES = 15;
        int FRAGMENT_THINGS_TO_SAY = 16;
        int USER_PROMPT_DELAY_MILLIS = 300;
        int WAKEWORD_FALLBACK_DELAY = 0;

        String REMINDER_INTENT = "reminder_intent";
        String ALARM_INTENT = "alarm_intent";
        int FRAGMENT_CONTACTS = 99;

//        String SOCKET_URL_HUB = "https://www.dev.smartbeings.ai";
    }

    interface RegisterHub {
        //TODO As told by backend team, can be changed later.
        String USER_ID_TYPE = "4";
    }

    interface Fragments {
        int FRAGMENT_DEVICES_LIST = 30;
        int FRAGMENT_DEVICES_DISCOVERY = 31;
        int FRAGMENT_DEVICES_REGISTER = 32;
        int FRAGMENT_DEVICES_CONTROL = 33;
        int FRAGMENT_NEWS_HEADLINES = 34;
        int FRAGMENT_DEVICES_SCENCES = 35;

        int FRAGMENT_ALARM_ALERT = 36;
        int FRAGMENT_ALARM = 37;
        int FRAGMENT_REMINDER_ALERT = 38;
        int FRAGMENT_MUSIC = 39;
        int FRAGMENT_LIVEFEED = 40;
        int FRAGMENT_VIDEO = 41;
        int FRAGMENT_NEWS_AUDIO = 42;
        int FRAGMENT_QNA = 43;
    }

    interface MusicFragment {
        String SPOTIFY_CLIENT_ID = "b01168b996914f6fbca0ca06ea644e39";
        String REDIRECT_URI = "https://enterprise.heywoohoo.com/v1/spotify";
    }

    interface HomeFragment {
        String HOME_INTENT = "home_intent";
    }

    interface SettingsFragment {
        int FRAGMENT_WIFI = 100;
        int FRAGMENT_UPDATE = 101;
    }

    interface StateActions {
        int PAUSE = 0;
        int SNOOZE = 1;
        int DISMISS = 2;
        int NO_ACTION = 3;
    }

    interface PhillipsHue {
        String SOCKET_URL_DEVICES = "https://app.mywoohoo.io/socket/device";
    }

    interface PINVIEW {
        String IS_PIN_EXISTS = "userPinExists";
        String PIN = "PIN";

    }

    interface LANGUAGE {
        String ENGLISH = "en";
        String ENGLISH_FOR_STT = "en-US";
        String ENGLISH_US = "en-US";
        String ENGLISH_GB = "en-GB";
        String CHINESH = "zh";
        String CHINESE_CN = "zh-CN";
        String ARABIC = "ar";
        String ARABIC_SA = "ar-SA";
        String GERMAN = "de";
        String GERMAN_DE = "de-DE";
        String HINDI = "hi";
        String HINDI_IN = "hi-IN";

        String FRENCH = "fr";
        String FRENCH_FR = "fr-FR";

        String DUTCH = "nl";
        String DUTCH_NL = "nl-NL";
        String RUSSIAN = "ru";
        String RUSSIAN_RU = "ru-RU";
        String SPANISH = "es";
        String SPANISH_ES = "es-ES";
        String ITALIAN = "it";
        String ITALIAN_IT = "it-IT";

    }
    interface BUT_SIM_PARAMS {
        String PACKAGE_ID = "package_id";
        String PHONE_ID = "phone_id";
        String EMAIL_ID = "email_id";
        String URL = "url";
    }

    interface FNB_PARAMS {
        String FRAGMENT_PRODUCT_TYPE = "product";
        String FRAGMENT_SUB_CATEGORIES_TYPE = "sub_categories";
        String FRAGMENT_CATEGORIES_TYPE = "categories";

    }

    interface ROOM_AUTOMATION {
        String ROOM_AUTOMATION = "room_automation";
        String FAN_MODE = "fanmode";

    }

    interface ROOM_AUTOMATION_FAN_MODE {
        String AUTO = "Auto";
        String ECO = "Eco";
        String OFF = "Off";
        String LOW = "Low";
        String MEDIUM = "Medium";
        String HIGH = "High";
    }

    int AUTO_CALL_DISCONNECT = 20000;

    // Using whitlisted device id for testing of firmware
    String USE_SYSTEM_ID = "Use Mobile's Device ID";
    String[] devices = { USE_SYSTEM_ID, "4c86b2c78dafd539", "2c78da4c86bfd539" };

    public static int DEFAULT_SENSORY_OPERATING_POINT = 2;

    public static String RC_DOMAIN_ID = "100";
    public static String GBTB_DOMAIN_ID = "101";
    public static String POSTILLION_DOMAIN_ID = "104";

    public static String SAUDI_TOURISM_DEFAULT_LIMIT = "50";

    public static String SAUDI_TOURISM_DEFAULT_PAGE_LIMIT = "1";
    public static String DEFAULT_VALUE_FOR_ORDER = "1";


    /* ----- FNB WebView Constants ----- */
    public static String REGENT_DOMAIN_ID = "108";
    public static String SHANGRILA_DOMAIN_ID = "109"; // TODO - check whether 108 is for CKS or Regent
    public static String CKS_DOMAIN_USER_ID = "110";
    public static String PARK_ROYAL_DOMAIN_ID = "112";
    public static String KHALIDIA_PALACE_DOMAIN_ID = "125";

    public static String HOLIDAY_INN_DOMAIN_ID = "122";

    public static String PARK_ROYAL_PICKERING_DOMAIN_ID = "127";
    public static String SHERAA_HQ = "90";

    public static List<String> INFRASYS_SUPPORTED_DOMAINS = new ArrayList(Arrays.asList(REGENT_DOMAIN_ID, SHANGRILA_DOMAIN_ID));


    public static String CRASH_LOG = "CrashLog";
    public static String AGORA_APP_ID = "45fac1f788c849bc94b8f1ad8232f501";

    public static boolean IS_AGORA_ENABLED = true;

//    public enum TVRemoteComponent{
//        CHANNEL_Up("channel_down"),
//        MONDAY("");
//    }

    public enum TVRemoteComponent {
        CHANNEL_DOWN ("channel_down"),
        CHANNEL_UP ("channel_up"),
        POWER_OFF ("power_off"),
        POWER_ON ("power_on"),
        MIRRORING ("mirroring"),
        GET_CHANNELS ("get_channels"),
        VOL_DOWN ("vol_down"),
        VOL_UP ("vol_up"),
        MUTE_ON ("mute"),
        MUTE_OFF ("unmute");

        private String value;

        private TVRemoteComponent(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }
}
