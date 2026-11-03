package com.sb.woohoo.ui.main.fragments.weather;

import com.sb.woohoo.ui.main.fragments.weather.pojo.response.WeatherItem;

import java.util.List;

public interface WeatherContract {
    interface View {
        void showError(String errorCause);

        void showProgress();

        void hideProgress();

        void updateCurrentWeather(WeatherItem weatherItem, String voiceResponse);

        void updateForecastWeather(List<WeatherItem> weatherItemList);
    }

    interface Action {
        void onAPIError(Object errorCause);

        void onCurrentWeatherFetched(WeatherItem weatherItem, String voiceResponse);

        void onForecastWeatherFetched(List<WeatherItem> weatherItemList);

        void callWeatherApiByCity(String city);
    }
}
