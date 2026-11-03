package com.sb.woohoo.ui.main.fragments.settings.wifi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.settings.wifi.pojo.WifiNetworkDetails;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shrey on 26/02/18.
 */

public class WifiPresenter extends BasePresenter implements WifiContract.Actions, WifiFragment.PasswordCallback {

    static final String OPEN = "Open";
    // Constants used for different security types
    private final String WPA2 = "WPA2";
    private final String WPA = "WPA";
    private final String WEP = "WEP";
    /* For EAP Enterprise fields */
    private final String WPA_EAP = "WPA-EAP";
    private final String IEEE8021X = "IEEE8021X";
    private WifiManager mWifiManager;
    private WifiFragment mView;
    private ScanResult connectingWifi = null;

    public WifiPresenter(WifiManager mWifiManager, WifiFragment mView) {
        this.mWifiManager = mWifiManager;
        this.mView = mView;
    }

    @Override
    public void setUpWifi() {
        mView.setUpWifi();
    }

    @Override
    public void scanWifi() {
        mWifiManager.startScan();
    }

    @Override
    public List<ScanResult> getWifiList() {
        return mWifiManager.getScanResults();
    }

    @Override
    public List<WifiConfiguration> getConfiguredNetworks() {
        return mWifiManager.getConfiguredNetworks();
    }

    @Override
    public List<WifiNetworkDetails> configureNetworks(List<ScanResult> wifiNetworks, List<WifiConfiguration> configuredNetworks) {
        List<WifiNetworkDetails> networks = new ArrayList<>(0);

        for (ScanResult wifiNetwork : wifiNetworks) {
            WifiNetworkDetails wifiNetworkDetail = new WifiNetworkDetails();
            wifiNetworkDetail.setWifiNetwork(wifiNetwork);
            try {
                for (WifiConfiguration configuredNetwork : configuredNetworks) {
                    String SSID = "\"" + wifiNetwork.SSID + "\"";
                    if (SSID.equals(configuredNetwork.SSID)) {
                        wifiNetworkDetail.setWifiConfiguration(configuredNetwork);
                        break;
                    }
                }
            } catch (RuntimeException r) {
                LoggerWrapper.d("Run time Ex", r.getMessage());
            }

            networks.add(wifiNetworkDetail);
        }
        return networks;
    }

    @Override
    public void connectWifi(WifiNetworkDetails selectedWifi) {

        connectingWifi = selectedWifi.getWifiNetwork();
        String wifiSecurity = getWifiSecurity(selectedWifi.getWifiNetwork());
        if (!wifiSecurity.contentEquals(OPEN)) {
            mView.getWifiPassword(selectedWifi.getWifiNetwork(), this, wifiSecurity);
        } else {
            // Open wifi connection
            mView.getWifiPassword(selectedWifi.getWifiNetwork(), this, OPEN);
            // connectToWiFi(selectedWifi.getWifiNetwork(), "", OPEN);
        }
    }


    @Override
    public boolean isSecure(ScanResult wifiNetwork) {
        return !getWifiSecurity(wifiNetwork).contentEquals(OPEN);
    }

    @Override
    public boolean isConnectedToSelectedWifi() {
        if (connectingWifi != null) {
            WifiInfo currentWifi = mWifiManager.getConnectionInfo();
            String SSID = "\"" + connectingWifi.SSID + "\"";
            return currentWifi.getSSID().equalsIgnoreCase(SSID);
        }
        return false;
    }

    @Override
    public void removeNetwork(String SSID) {
        List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID.equals("\"" + SSID + "\"")) {
                LoggerWrapper.d(this.getClass().getSimpleName(), "removing the SSID :: " + i.SSID);
                mWifiManager.removeNetwork(i.networkId);
            }
        }
    }

    @Override
    public boolean hasTriedWifiConnection() {
        return connectingWifi != null;
    }


    public boolean isNetworkDetailsSaved(String SSID) {
        List<WifiConfiguration> WiFiConfigs = mWifiManager.getConfiguredNetworks();

        for (WifiConfiguration WiFiConfig : WiFiConfigs) {
            if (WiFiConfig.SSID.equals("\"" + SSID + "\"")) {
                return true;
            }
        }
        return false;
    }

    private String getWifiSecurity(ScanResult wifiNetwork) {
        final String capabilities = wifiNetwork.capabilities;
        final String[] securityModes = {WEP, WPA, WPA2, WPA_EAP, IEEE8021X};
        for (int i = securityModes.length - 1; i >= 0; i--) {
            if (capabilities.contains(securityModes[i])) {
                return securityModes[i];
            }
        }
        return OPEN;
    }

    private void connectToWiFi(ScanResult scanResult, String password, String securityType) {
        try {
            String networkSSID = scanResult.SSID;

            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes
            conf.status = WifiConfiguration.Status.ENABLED;
            conf.priority = 40;

            if (securityType.equalsIgnoreCase(WEP)) {
                LoggerWrapper.e("Wifi Connection", "Configuring WEP");
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

                if (password.matches("^[0-9a-fA-F]+$")) {
                    conf.wepKeys[0] = password;
                } else {
                    conf.wepKeys[0] = "\"".concat(password).concat("\"");
                }

                conf.wepTxKeyIndex = 0;

            } else if (securityType.equalsIgnoreCase(WPA) || securityType.equalsIgnoreCase(WPA2)) {
                LoggerWrapper.e("Wifi Connection", "Configuring WPA");

                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);

                conf.preSharedKey = "\"" + password + "\"";

            } else {
                LoggerWrapper.e("Wifi Connection", "Configuring OPEN network");
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedAuthAlgorithms.clear();
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            }

            int networkId = mWifiManager.addNetwork(conf);

            LoggerWrapper.i("Wifi Connection", "Add result " + networkId);

            List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
            for (WifiConfiguration i : list) {
                if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                    LoggerWrapper.i("Wifi Connection", "WifiConfiguration SSID " + i.SSID);

                    boolean isDisconnected = mWifiManager.disconnect();
                    LoggerWrapper.i("Wifi Connection", "isDisconnected : " + isDisconnected);

                    boolean isEnabled = mWifiManager.enableNetwork(i.networkId, true);
                    LoggerWrapper.i("Wifi Connection", "isEnabled : " + isEnabled);

                    boolean isReconnected = mWifiManager.reconnect();
                    LoggerWrapper.i("Wifi Connection", "isReconnected : " + isReconnected);

                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean disconnectCurrentNetwork() {
        if (mWifiManager != null && mWifiManager.isWifiEnabled()) {
            int netId = mWifiManager.getConnectionInfo().getNetworkId();
            mWifiManager.disableNetwork(netId);
            return mWifiManager.disconnect();
        }
        return false;
    }

    @Override
    public void onPasswordEntered(ScanResult selectedWifi, String password, String securityType) {
        if (selectedWifi != null) {
            LoggerWrapper.i("Wifi", "Trying to connect to " + selectedWifi.SSID + " with security " + securityType);
            connectToWiFi(selectedWifi, password, securityType);
        }
    }

    /**
     * Method to return data from {@link WifiManager}.
     *
     * @param type Provided by caller to specify what is needed.
     * @return String containing requested data from {@link WifiManager} to the caller of method.
     * @author Ritwik Jamuar.
     */

    public String getWifiInformation(String type) {
        String data = "NA";
        switch (type) {
            case "MAC_ADDRESS":
                data = getMACAddress(mWifiManager.getConnectionInfo());
                break;
            case "IP_ADDRESS":
                data = getIPAddress(mWifiManager.getConnectionInfo());
                break;
            case "CONNECTED_WIFI":
                data = getConnectedWifi();
                break;
        }
        return data;
    }

    /**
     * Fetches MAC Address from {@link WifiManager}.
     *
     * @param info Instance of {@link WifiInfo} to extract MAC Address.
     * @return String representing MAC Address of Connected Network Router.
     * @author Ritwik Jamuar.
     */
    @SuppressLint("HardwareIds")
    private String getMACAddress(WifiInfo info) {
        String MACAddress = "NA";
        if (info != null) MACAddress = info.getMacAddress();
        return MACAddress;
    }

    /**
     * Fetches IP Address from {@link WifiManager} using {@link WifiInfo}.
     *
     * @param info Instance of {@link WifiInfo}.
     * @return String representing IP Address of Connected Network Router.
     * @author Ritwik Jamuar.
     */
    @SuppressWarnings("deprecation")
    private String getIPAddress(WifiInfo info) {
        String IPAddress = "NA";
        if (info != null) IPAddress = Formatter.formatIpAddress(info.getIpAddress());
        return IPAddress;
    }

    /**
     * Fetches the Currently Connected Network Router in WiFi.
     *
     * @return SSID of Network which device is currently connected to.
     * @author Ritwik Jamuar.
     */
    public String getConnectedWifi() {
        String connectedWifiSSID = "NA";
        if (WifiManager.WIFI_STATE_ENABLED == mWifiManager.getWifiState()) {
            for (WifiConfiguration configuration : mWifiManager.getConfiguredNetworks()) {
                if (WifiConfiguration.Status.CURRENT == configuration.status) {
                    connectedWifiSSID = configuration.SSID;
                    break;
                }
            }
        }
        return connectedWifiSSID;
    }

    @Override
    public boolean isWifiConnected() {
        ConnectivityManager connManager = (ConnectivityManager) mView.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi;
        if (connManager != null) {
            mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return mWifi.isConnected();
        }
        return false;
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }
}