package com.sb.woohoo.utils.gps;

/**
 * Contains methods to indicate Location Update across {@link com.sb.woohoo.ui.base.BaseActivity}.
 *
 * @author Ritwik Jamuar.
 */
public interface GPSListener {
    /**
     * Invokes when Location has been changed.
     *
     * @param location Location of Device.
     */
    void onLocationChanged(double lat, double lon);

    void onIpAdressFound(String wifiIP);

}