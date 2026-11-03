package com.sb.woohoo.ui.onboarding.timezone;

import java.util.List;
import java.util.TimeZone;

/**
 * Created by admin on 1/30/18.
 */

public interface TimeZoneContract {
    interface View {
        void showTimeZones(List<TimeZone> timeZoneList, int defaultPosition);

        void showEmptyView();

        void hideEmptyView();

        void showMessage(String message);

        String getTimeZone();

        void setTimeZone(String timeZone);

    }

    interface Actions {
        void loadTimeZones();

        void loadTimeZonesError(String msg);

    }
}
