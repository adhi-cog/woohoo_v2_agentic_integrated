package com.sb.woohoo.ui.onboarding.timezone;

import android.util.Log;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.utils.TimeZoneUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.sileria.android.Resource.getAssets;

/**
 * Created by admin on 1/30/18.
 */

public class TimeZonePresenter implements TimeZoneContract.Actions {

    private TimeZoneSelectionFragment mView;

    public TimeZonePresenter(TimeZoneSelectionFragment mView) {
        this.mView = mView;
    }

    public String getTimeZone() {
        return mView.getTimeZone();
    }

    @Override
    public void loadTimeZones() {
        List<TimeZone> timeZoneList = new ArrayList<>(0);
        int defaultTimeZonePosition = -1;
        TimeZone defaultTimeZone = TimeZoneUtils.getInstance(mView.getContext()).getDefaultTimeZone();
        String[] ids = TimeZoneUtils.getInstance(mView.getContext()).getTimeZoneAvailableIds();

        for (String id : ids) {
            TimeZone timeZone = TimeZoneUtils.getInstance(mView.getContext()).getTimeZoneFromId(id);
            if (!isAlreadyExist(timeZoneList, timeZone)) {
                String city = TimeZoneUtils.getInstance(mView.getContext()).getTimeZoneCityName(timeZone);
                LoggerWrapper.d("TIMEZONEPRESENTER", city);
                if (TimeZoneAdapter.getTimeZone(defaultTimeZone).equalsIgnoreCase(TimeZoneAdapter.getTimeZone(timeZone))) {
                    defaultTimeZonePosition = timeZoneList.size();
                }
            }
        }

        Collections.sort(timeZoneList,
                (s1, s2) -> s1.getRawOffset() - s2.getRawOffset());

        if (!timeZoneList.isEmpty()) {
            mView.showTimeZones(timeZoneList, defaultTimeZonePosition);
        } else {
            mView.showEmptyView();
        }
    }

    @Override
    public void loadTimeZonesError(String msg) {
        mView.showMessage(msg);
    }

    private boolean isAlreadyExist(List<TimeZone> timeZoneList, TimeZone timeZone) {
        for (TimeZone temp : timeZoneList
                ) {
            if (TimeZoneAdapter.getTimeZone(temp).equalsIgnoreCase(TimeZoneAdapter.getTimeZone(timeZone))) {
                return true;
            }
        }
        return false;
    }


}
