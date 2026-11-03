package com.sb.woohoo.data.prefs;

/**
 * Created by admin on 2/22/18.
 */


public interface SharedPrefsKey {
    String PREF_KEY_USER_ID = "user_id";
    String PREF_KEY_EMAIL = "email";
    String PREF_KEY_SESSION_TOKEN = "session_token";
    String PREF_KEY_IS_FIRST_TIME = "is_first_time";
    String PREF_KEY_STATUS = "status";
    String PREF_KEY_REFRESH_TOKEN = "refresh_token";
    String PREF_KEY_FIRST_NAME = "first_name";
    String PREF_KEY_LAST_NAME = "last_name";
    String PREF_KEY_NAME = "name";
    String PREF_KEY_LAST_LOGIN = "last_login";
    String PREF_KEY_TIME_ZONE = "time_zone";
    String PREF_KEY_COUNTRY_CODE = "country_code";
    String PREF_KEY_TIME_ZONE_PLACE = "time_zone_place";
    String PREF_KEY_DEVICE_ID = "device_id";
    String PREF_KEY_ENGINE = "engine";
    String PREF_KEY_WAKEWORD = "wakeword";
    String PREF_KEY_IP = "wifi_ip";
    String PREF_USER_ID_TYPE = "user_type";
    String PREF_MUTE_STATE = "mute";
    String PREF_IMAGE = "image";
    String PREF_IS_LOGIN = "is_login";
    String PREF_EXPIRE_TIME = "expire_time";

    String PREF_DOMAIN_ID = "domain_id";
    String PREF_INFRA_SYS_URL = "infra_sys_url";

    String PREF_KEY_LIGHTS = "lights";
    String PREF_KEY_LIGHT_REQUEST_JSON = "light_request_json";
    String PREF_KEY_IS_FACTORY_RESET_DONE = "is_factory_reset";
    String PREF_KEY_SURVEILLANCE_MODE = "surveillance_mode";
    String PREF_INTRO_VIDEO_KEY = "LANG_INTRO_VIDEO";

    String PREF_KEY_USER_DETAILS = "userDetails";

    String PREF_DOWNLOAD_STATUS = "auto_update_status";
    String PREF_KEY_ALARM_IDS = "alarm_ids";


    String PREF_KEY_SIP_ID = "sip_id";
    String PREF_KEY_SIP_PASS = "sip_pass";
    String PREF_KEY_SIP_DOMAIN = "sip_domain";
    String PREF_KEY_SIP_PROXY = "sip_proxy";
    String PREF_KEY_SIP_TRANSPORT = "sip_proxy_transport";

    String PREF_KEY_FEATURE_LINPHONE = "feature_linphone";
    String PREF_KEY_FEATURE_NETWORK_LOG = "feature_network_log";
    String PREF_KEY_FEATURE_AGORA = "feature_agora";

    String PREF_KEY_FEATURE_RESTART= "feature_restart";
    String PREF_KEY_FEATURE_RESTART_HR= "feature_restart_hr";
    String PREF_KEY_FEATURE_RESTART_MIN= "feature_restart_min";

    String PREF_KEY_TEST_DEVICE_ID = "feature_test_device_id";

    String PREF_DEVICE_NAME = "device_name";

    String PREF_KEY_TEST_OPERATING_POINT = "feature_test_operating_point";

    String PREF_KEY_FEATURE_DARK_MODE = "feature_dark_mode";

    String PREF_KEY_DARK_MODE_START_POINT = "dark_mode_starting_point";

    String PREF_KEY_FEATURE_DARK_MODE_HR= "feature_dark_mode_hr";
    String PREF_KEY_FEATURE_DARK_MODE_MIN= "feature_dark_mode_min";

    String PREF_KEY_DARK_MODE_END_POINT = "dark_mode_ending_point";

    String PREF_KEY_SCREEN_COLOR = "screen_color";

    String PREF_KEY_MIC_PERMISSION_ASKED = "mic_permission_asked";

    String PREF_KEY_CALL_LOGS = "call_logs";

}
