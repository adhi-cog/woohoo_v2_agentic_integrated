package com.sb.woohoo.ui.main.fragments.settings.wifi.pojo;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;

import java.io.Serializable;

public class WifiNetworkDetails implements Serializable {

    private ScanResult wifiNetwork;

    private WifiConfiguration wifiConfiguration;

    private boolean connected;

    public ScanResult getWifiNetwork() {
        return wifiNetwork;
    }

    public void setWifiNetwork(ScanResult wifiNetwork) {
        this.wifiNetwork = wifiNetwork;
    }

    public WifiConfiguration getWifiConfiguration() {
        return wifiConfiguration;
    }

    public void setWifiConfiguration(WifiConfiguration wifiConfiguration) {
        this.wifiConfiguration = wifiConfiguration;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
