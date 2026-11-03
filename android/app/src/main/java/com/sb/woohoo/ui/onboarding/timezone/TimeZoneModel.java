package com.sb.woohoo.ui.onboarding.timezone;

/**
 * Created by admin on 2/16/18.
 */

public class TimeZoneModel {
    private String gmtInHours;
    private String countryStandard;

    public String getGmtInHours() {
        return gmtInHours;
    }

    public void setGmtInHours(String gmtInHours) {
        this.gmtInHours = gmtInHours;
    }

    public String getCountryStandard() {
        return countryStandard;
    }

    public void setCountryStandard(String countryStandard) {
        this.countryStandard = countryStandard;
    }
}
