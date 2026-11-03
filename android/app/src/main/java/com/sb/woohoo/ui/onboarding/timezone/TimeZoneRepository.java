package com.sb.woohoo.ui.onboarding.timezone;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by admin on 1/30/18.
 */

public class TimeZoneRepository {


    public static List<TimeZoneModel> getTimeZones() {

        String[] TZ = TimeZone.getAvailableIDs();
        List<String> TZ1 = new ArrayList<>();
        List<String> timeDifferenceList = new ArrayList<>();
        List<TimeZoneModel> timeZoneList = new ArrayList<>();

        for (String aTZ : TZ) {
            if (!(TZ1.contains(TimeZone.getTimeZone(aTZ).getDisplayName()))) {
                TZ1.add(TimeZone.getTimeZone(aTZ).getDisplayName());
                int mGTMOffSet = TimeZone.getTimeZone(aTZ).getRawOffset();
                String timeZone = String.format("%s%02d:%02d", mGTMOffSet >= 0 ? "+" : "", mGTMOffSet / 3600000, (mGTMOffSet / 60000) % 60);
                timeDifferenceList.add(timeZone.replaceFirst("-", "-0"));
            }
        }

        for (int i = 0; i < 100; i++) {

            TimeZoneModel timeZoneModel = new TimeZoneModel();

            timeZoneModel.setGmtInHours("GMT   " + timeDifferenceList.get(i));
            timeZoneModel.setCountryStandard(TZ1.get(i));
            timeZoneList.add(timeZoneModel);
        }
        return timeZoneList;
    }

}
