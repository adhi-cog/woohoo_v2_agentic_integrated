package com.sb.woohoo.ui.main.fragments.settings.wifi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.settings.wifi.pojo.WifiNetworkDetails;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class WifiRepository extends BaseRepository<WifiPresenter> {
    private final String WIFI = "wifi";
    private SharedPrefsHelper mSharedPrefsHelper;
    private Gson gson;

    @Inject
    WifiRepository(SharedPrefsHelper sharedPrefsHelper, Gson gson) {
        this.mSharedPrefsHelper = sharedPrefsHelper;
        this.gson = gson;
    }

    public List<WifiNetworkDetails> getWifi() {
        String EMPTY_STRING = "";
        String wifi = mSharedPrefsHelper.get(WIFI, EMPTY_STRING);
        List<WifiNetworkDetails> wifiList = gson.fromJson(wifi, new TypeToken<List<WifiNetworkDetails>>() {
        }.getType());
        if (wifiList == null) {
            wifiList = new ArrayList<>(0);
        }
        return wifiList;
    }

    public void setWifi(List<WifiNetworkDetails> wifiList) {
        String json = gson.toJson(wifiList);
        mSharedPrefsHelper.put(WIFI, json);
    }
}