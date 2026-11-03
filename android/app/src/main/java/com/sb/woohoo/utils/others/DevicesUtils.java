package com.sb.woohoo.utils.others;

import android.graphics.Color;
import android.util.Log;

import com.sb.woohoo.custom.LoggerWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.Light;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DevicesUtils {

    /**
     * Utility methods for IOT Devices
     **/

    public final static String MEET_HUE_BASE_URL = "https://discovery.meethue.com/";

    public static float[] getHSV(int color) {
        float hsv[] = new float[3];
        Color.colorToHSV(color, hsv);
        return hsv;
    }

    /*---------------------------------   SharedPrefs for Lights  -----------------------------*/

    public static List<Light> getLights() {
        List<Light> result = new ArrayList<>();
        Map<String, Light> data;

        Gson gson = new Gson();
        String allLights = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_LIGHTS, null);

        if (allLights != null) {
            Type type = new TypeToken<HashMap<String, Light>>() {
            }.getType();
            data = gson.fromJson(allLights, type);

            for (Map.Entry<String, Light> light : data.entrySet()) {
                result.add(light.getValue());
                LoggerWrapper.d("First Light", result.get(0).getName());
            }
            return result;
        }
        return null;
    }

    // Update light status for any changes Pref's
    public static void updateLightStatus(Light light) {
        HashMap<String, Light> data = new HashMap<>();
        Gson gson = new Gson();
        String allLights = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_LIGHTS, null);
        if (null != light) {
            Type type = new TypeToken<HashMap<String, Light>>() {
            }.getType();

            if(allLights!=null) {
                data = gson.fromJson(allLights, type);
            }
//            if (data.containsKey(light.getBridgeId() + "_" + light.getLightId())) {
                data.put(light.getBridgeId() + "_" + light.getLightId(), light);
//            }
            WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_LIGHTS, gson.toJson(data));
        }
    }

    public static Light findLightFromIntentInfo(IntentInfo intentInfo) {

        List<Light> lightList = DevicesUtils.getLights();
        if (lightList != null) {
            if (intentInfo.getParameters().containsKey("name")) {
                String lightName = (String) intentInfo.getParameters().get("name");
                if (lightName != null) {
                    for (Light light : lightList) {
                        if (light.getName().equalsIgnoreCase(lightName) && light.getState().isReachable()) {
                            LoggerWrapper.i("Light", light.getName());
                            return light;
                        }
                    }
                }
            }
        }
        return null;
    }
}
