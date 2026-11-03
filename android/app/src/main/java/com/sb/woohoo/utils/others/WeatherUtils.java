package com.sb.woohoo.utils.others;

import android.util.Log;

import com.sb.woohoo.custom.LoggerWrapper;
import com.google.gson.internal.LinkedTreeMap;

public class WeatherUtils {

    public static String processLocationJson(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof String) {
            String location = ((String) object).trim();
            return location.isEmpty() ? null : location;
        }
        LoggerWrapper.i("object",object.toString());
        String json = object.toString().replaceAll("=", ":");
        if (json.isEmpty()) {
            return null;
        } else if (!(object instanceof LinkedTreeMap)) {
            return object.toString();
        } else {
            String location = "";
            LinkedTreeMap<String, String> locationMap = (LinkedTreeMap<String, String>) object;

            for (String key : locationMap.keySet()) {
                location += locationMap.get(key) + "-";
            }

            if (!location.isEmpty()) {
                if (location.endsWith("-")) {
                    location = location.substring(0, location.length() - 1);
                }
                location = location.replaceAll("-", ", ");

                return location.trim();
            } else {
                return null;
            }
        }
    }
}
