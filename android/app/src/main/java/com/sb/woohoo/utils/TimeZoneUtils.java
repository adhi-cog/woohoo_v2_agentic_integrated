package com.sb.woohoo.utils;

import android.content.Context;
import android.text.TextUtils;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TimeZoneUtils {

    private static TimeZoneUtils instance;
    private static Context mContext;
    public static TimeZoneUtils getInstance(Context context){
        if (context != null)
            mContext = context;
        if (instance == null)
            instance = new TimeZoneUtils();
        return instance;
    }
    public TimeZone getDefaultTimeZone(){
        return TimeZone.getDefault();
    }
    public TimeZone getTimeZoneFromId(String id){
        return  TimeZone.getTimeZone(id);
    }

    public String[] getTimeZoneAvailableIds(){
        return TimeZone.getAvailableIDs();
    }

    public TimeZone setTimeZoneId(String cityId, TimeZone timeZone){
        timeZone.setID(cityId);
        return timeZone;
    }

    public String getTimeZoneId(TimeZone timeZone){
        return timeZone.getID();
    }

    public void setTimeZoneToDefault(TimeZone timeZoneToDefault){
        TimeZone.setDefault(timeZoneToDefault);
    }

    public String getTimeZoneFromSharedPref(){
        TimeZone timeZone = getDefaultTimeZone();
        String sharedPrefTimeZoneId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_TIME_ZONE, "");
        if (!TextUtils.isEmpty(sharedPrefTimeZoneId)){
            if (sharedPrefTimeZoneId.contains("GMT")){
                sharedPrefTimeZoneId = getCityName(sharedPrefTimeZoneId);
                if (TextUtils.isEmpty(sharedPrefTimeZoneId))
                    return "";
            }
        }
        Calendar calendar = Calendar.getInstance(timeZone);
        return formatUTCDateToLocal(calendar.getTime());
    }

    private String formatUTCDateToLocal(Date date){

        SimpleDateFormat simpleDateFormat = getSimpleDateFormat();
        return simpleDateFormat.format(date);
    }

    private Date parseDateFromTimeZone(TimeZone timeZone, String timeZoneId){
        SimpleDateFormat simpleDateFormat = getSimpleDateFormat();
        try {
            return simpleDateFormat.parse(timeZoneId);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private SimpleDateFormat getSimpleDateFormat() {
        return new SimpleDateFormat("hh:mm aa, MMM dd", Locale.ENGLISH);
    }

    public String getTimeZoneCityName(TimeZone tz) {
        long hours = TimeUnit.MILLISECONDS.toHours(tz.getRawOffset());
        long minutes = TimeUnit.MILLISECONDS.toMinutes(tz.getRawOffset())
                - TimeUnit.HOURS.toMinutes(hours);
        minutes = Math.abs(minutes);
        String result;
        if (hours > 0) {
            result = String.format("GMT+%02d:%02d", hours, minutes);
        } else if (hours < 0) {
            result = String.format("GMT-%02d:%02d", Math.abs(hours), minutes);
        } else {
            result = "GMT+00:00";
        }
        LoggerWrapper.d("TIMEZONEPRESENTER", result.toString());
        return getCityName(result);
    }

    private String getCityName(String id) {
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            return obj.getString(id);
        } catch (JSONException e) {
        }
        return "";
    }

    private String loadJSONFromAsset() {
        String json = null;
        if (mContext != null) {
            try {
                InputStream is = mContext.getAssets().open("timezone.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return json;
    }

}
