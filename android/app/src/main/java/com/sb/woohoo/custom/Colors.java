package com.sb.woohoo.custom;

import java.util.HashMap;

public class Colors {

    private HashMap<String, String> hueColors = new HashMap<>();

    public Colors() {
        hueColors.put("orange", "6166");
        hueColors.put("white", "35186");
        hueColors.put("red", "1112");
        hueColors.put("green", "23617");
        hueColors.put("yellow", "20930");
        hueColors.put("blue", "42804");
        hueColors.put("pink", "54038");
        hueColors.put("purple", "47880");
    }

    public String getColorCode(String key) {
        key = key.toLowerCase();
        if (hueColors.containsKey(key)) {
            return hueColors.get(key);
        } else {
            return null;
        }
    }
}
