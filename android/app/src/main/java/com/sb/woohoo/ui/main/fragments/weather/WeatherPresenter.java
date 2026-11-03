package com.sb.woohoo.ui.main.fragments.weather;

import android.text.TextUtils;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.weather.pojo.WeatherRequest;
import com.sb.woohoo.ui.main.fragments.weather.pojo.response.WeatherItem;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.Constants;
import com.sb.woohoo.utils.others.MethodUtils;
import com.sb.woohoo.utils.others.WeatherUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;

import static com.sb.woohoo.ui.main.Categories.Skills.Weather.WEATHER_CONDITION;
import static com.sb.woohoo.ui.main.Categories.Skills.Weather.WEATHER_OUTFIT;
import static com.sb.woohoo.ui.main.Categories.Skills.Weather.WEATHER_SEARCH;
import static com.sb.woohoo.ui.main.Categories.Skills.Weather.WEATHER_SHOW;

public class WeatherPresenter extends BasePresenter implements WeatherContract.Action {

    public static final String UNIT_TEMP_F = "\u00b0" + "F";
    private static final String NLP_KEY_DATETIME = "datetime";
    private static final String NLP_KEY_LOCATION = "location";
    private static final String NLP_KEY_CITY = "city";
    private static final String NLP_KEY_CONDITION = "condition";
    private static final String NLP_KEY_OUTFIT = "outfit";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String UNIT_PRESSURE_BAR = "bar";
    private static final String UNIT_WIND_SPEED_KMH = "km/h";
    public static final String UNIT_TEMPS_C = "\u00b0" + "C";
    private static WeatherFragment mView;
    private WeatherRepository mRepository;
    private double locationLatLng[];
    private String todayDate;

    @Inject
    public WeatherPresenter(WeatherFragment mView, WeatherRepository mRepository) {
        this.mView = mView;
        this.mRepository = mRepository;
        this.mRepository.onAttach(WeatherPresenter.this);
        locationLatLng = mView.getParentActivity().getLocation();
        todayDate = getTodayDate();
    }

    private static String getTodayDate() {
        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return df.format(Calendar.getInstance(TimeZone.getDefault()).getTime());
    }

    private static boolean isDatePassed(String date) {
        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        Date dateToCompare;
        Date todayDate;
        try {
            dateToCompare = df.parse(date);
            todayDate = df.parse(getTodayDate());
            return dateToCompare.before(todayDate);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    static String getFormattedTemperatureInFah(String temperature) {
        return "" + temperature + UNIT_TEMP_F;
    }

    static String getFormattedTemperatureInFah(double temperature) {
        return "" + Math.round(temperature) + UNIT_TEMP_F;
    }

    static String getFormattedTemperatureInCel(double temperature) {
        return "" + Math.round(temperature) + UNIT_TEMPS_C;
    }

    static String getFormattedTemperatureInCel(String temperature) {
        return "" + temperature + UNIT_TEMPS_C;
    }

    static String getConvertedFToCTemp(double tempInFehrenheit){
        double tempInCelcius = (tempInFehrenheit-32)*(0.5556);
        return String.valueOf(Math.round(tempInCelcius));
    }

    static String getFormattedLocation(String location) {
        return (location == null || location.length() == 0) ? mView.getString(R.string.location_not_available) : location;
    }

    @Override
    public void onAPIError(Object message) {
        String msgString;
        if (message instanceof Integer)
            msgString = mView.getString((Integer) message);
        else
            msgString = (String) message;
        mView.showError(msgString);
    }

    @Override
    public void onCurrentWeatherFetched(WeatherItem weatherItem, String voiceResponse) {
        mView.updateCurrentWeather(weatherItem, voiceResponse);
        mView.hideProgress();
    }

    @Override
    public void onForecastWeatherFetched(List<WeatherItem> weatherItemList) {
        mView.updateForecastWeather(weatherItemList);
    }

    @Override
    public void callWeatherApiByCity(String city) {
        WeatherRequest weatherRequest = processIntentResponse(null, city, 0);
        mRepository.requestWeatherInfo(weatherRequest);
        mView.showProgress();
    }

    @Override
    public void processActions(IntentInfo intentInfo) {
        String dateTime, location;
        WeatherRequest weatherRequest;
        if (intentInfo == null) {
            weatherRequest = processIntentResponse(null, null, 0);
        } else {
            HashMap<String, Object> intentParameters = intentInfo.getParameters();
            dateTime = getStringSlot(intentParameters, NLP_KEY_DATETIME);
            //TODO : Below code is for dialog-flow because location received is not string..it is in json format.
            Object object1 = intentParameters.get(NLP_KEY_LOCATION);
            location = WeatherUtils.processLocationJson(object1);
            if (TextUtils.isEmpty(location)) {
                location = getStringSlot(intentParameters, NLP_KEY_CITY);
            }
            if (!dateTime.equals("") && isDatePassed(dateTime)) {
                dateTime = getTodayDate();
            }
            switch (intentInfo.getAction()) {
                case WEATHER_SHOW:
                case WEATHER_SEARCH: {
                    weatherRequest = processIntentResponse(dateTime, location, 0);
                }
                break;
                case WEATHER_CONDITION: {
                    String condition = getStringSlot(intentParameters, NLP_KEY_CONDITION);
                    int weatherCode = getWeatherCode(condition);
                    weatherRequest = processIntentResponse(dateTime, location, weatherCode);
                }
                
                break;
                case WEATHER_OUTFIT: {
                    String condition = getStringSlot(intentParameters, NLP_KEY_OUTFIT);
                    int weatherCode = getWeatherCode(condition);
                    weatherRequest = processIntentResponse(dateTime, location, weatherCode);
                }
                break;
                default:
                    weatherRequest = processIntentResponse(null, null, 0);
            }
        }
        mRepository.requestWeatherInfo(weatherRequest);
        mView.showProgress();
    }

    private String getStringSlot(HashMap<String, Object> intentParameters, String key) {
        if (intentParameters == null || intentParameters.get(key) == null) {
            return "";
        }
        return String.valueOf(intentParameters.get(key));
    }

    private int getWeatherCode(String condition) {
        int conditionInteger = 0;
        try {
            conditionInteger = Integer.parseInt(condition);
        } catch (NumberFormatException e) {
            MethodUtils.printLog(WeatherPresenter.class, e.getMessage());
        }
        return conditionInteger;
    }

    private WeatherRequest processIntentResponse(String dateTime, String location, Integer condition) {
        String latitude;
        String longitude;
        if (WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "").equalsIgnoreCase(Constants.GBTB_DOMAIN_ID)) {
            latitude = String.valueOf(1.282375);
            longitude = String.valueOf(103.864273);
        } else {
            latitude = String.valueOf(locationLatLng[0]);
            longitude = String.valueOf(locationLatLng[1]);
        }
        String deviceID = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");
        WeatherRequest weatherRequest = new WeatherRequest();

        if (dateTime == null || dateTime.isEmpty()) {
            if (location == null || location.isEmpty()) {
                weatherRequest.setLatitude(latitude);
                weatherRequest.setLongitude(longitude);
                weatherRequest.setDeviceId(deviceID);
            } else {
                weatherRequest.setCity(location);
            }
        } else if (location == null || location.isEmpty()) {
            weatherRequest.setLatitude(latitude);
            weatherRequest.setLongitude(longitude);
            weatherRequest.setDate(dateTime);
        } else {
            weatherRequest.setCity(location);
            weatherRequest.setDate(dateTime);
        }

        if (condition != 0) {
            weatherRequest.setWeatherCode(condition);
        }
        return weatherRequest;
    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        mRepository.repositoryCleanUps();
    }
}
