package com.sb.woohoo.ui.main.fragments.settings.wifi;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;

import com.sb.woohoo.ui.main.fragments.settings.wifi.pojo.WifiNetworkDetails;

import java.util.List;

/**
 * Created by Shrey on 26/02/18.
 */

public interface WifiContract {

    interface Views {

        void setUpWifi();

        void getWifiPassword(ScanResult selectedWifi, WifiFragment.PasswordCallback passwordCallback, String securityType);
    }

    interface Actions {

        void scanWifi();

        List<ScanResult> getWifiList();

        void setUpWifi();

        void connectWifi(WifiNetworkDetails selectedWifi);

        boolean isSecure(ScanResult wifiNetwork);

        boolean isConnectedToSelectedWifi();

        boolean hasTriedWifiConnection();

        String getConnectedWifi();

        boolean isWifiConnected();

        void removeNetwork(String SSID);

        List<WifiConfiguration> getConfiguredNetworks();

        List<WifiNetworkDetails> configureNetworks(List<ScanResult> wifiNetworks, List<WifiConfiguration> configuredNetworks);


    }

}
