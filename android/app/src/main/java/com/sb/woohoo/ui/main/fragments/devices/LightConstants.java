package com.sb.woohoo.ui.main.fragments.devices;

public interface LightConstants {
    //Commons
    String HTTP = "http://";
    String API = "/api/";
    String LIGHTS = "/lights/";
    String STATE = "/state";
    String NEW = "/new";
    String LIGHTS_ = "/lights";
    String SYMBOL_PERCENT = "%";

    //NLP Keys
    String NLP_KEY_NAME = "name";
    String NLP_KEY_PERCENTAGE = "percentage";
    String NLP_KEY_COLOR = "color";
    String NLP_PARAM_KEY_INDEX = "index";

    //NLP Values
    String NLP_VALUE_ALL = "all";
    String NLP_VALUE_COLOR_BLUE = "blue";
    String NLP_VALUE_COLOR_YELLOW = "yellow";

    //JSON KEYS
    String JSON_KEY_NAME = "name";
    String JSON_KEY_BRI = "bri";
    String JSON_KEY_ON = "on";
    String JSON_KEY_HUE = "hue";
    String JSON_KEY_CT = "ct";

    String JSON_KEY_TIME_ZONE = "timeZone";

    //Utterances
    String JSON_PROPERTY_DEVICE_TYPE = "devicetype";
    String JSON_PROPERTY_DEVICE_VALUE = "woohoo_hub";
    String ALL_LIGHTS_NOT_REG = "No lights are registered.";
    String LIGHT_NOT_REG = "This light is not registered.";
    String LIGHT_NOT_REACHABLE = " light is not reachable.";
    String LIGHT_NAME_UPDATE_MSG = "Light name updated :)";
    String LIGHT_DELETE_MSG = "That light was deleted :)";
    String LIGHT_REG_UTTERANCE = "Which light you want to register ?";
    String LIGHT_SELECT_UTTERANCE = "Select light from the list";

    //Hue Control Constants
    int EXTENDED_COLOR_LIGHT_MUL_FACTOR = 182;
    int COLOR_TEMPERATURE_LIGHT_MUL_FACTOR = 2;
    int COLOR_TEMPERATURE_LIGHT_SUB_FACTOR = 500;
    int COLOR_BLUE_VALUE = 264;
    int COLOR_YELLOW_VALUE = 385;
    double COLOR_TEMPERATURE_BRIGHTNESS_MUL_FACTOR = 2.5;
    int COLOR_TEMPERATURE_BRIGHTNESS_FALLBACK_MUL_FACTOR = 100;
    int COLOR_UNV_VALUE = 255;
    String LIGHT_BRIGHTNESS_VALUE_ERROR_MSG = "Ambiguous percentage value.";
    String LIGHT_HUE_VALUE_ERROR_MSG = "Invalid color.";

    //TODO will be removed later
    interface Actions {
        int ACTION_DEFAULT = -1;
    }

    interface LightState {
        int MODE_BRIGHTNESS = 0;
        int MODE_HUE = 1;
        int MODE_OFF = 2;
        int MODE_ON = 3;
        int FROM_SOCKET = 4;
        int MODE_RENAME = 5;
    }

    interface LightType {
        String COLOR_TEMPERATURE_LIGHT = "Color temperature light";
        String EXTENDED_COLOR_LIGHT = "Extended color light";
    }
}
