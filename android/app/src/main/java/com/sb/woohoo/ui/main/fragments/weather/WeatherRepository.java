package com.sb.woohoo.ui.main.fragments.weather;

import com.sb.woohoo.R;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.weather.pojo.WeatherRequest;
import com.sb.woohoo.ui.main.fragments.weather.pojo.response.Data;
import com.sb.woohoo.ui.main.fragments.weather.pojo.response.Weather;
import com.sb.woohoo.ui.main.fragments.weather.pojo.response.WeatherItem;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class WeatherRepository extends BaseRepository<WeatherPresenter> {

    private WooHooApiCall mApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    WeatherRepository(WooHooApiCall apiCall, CompositeDisposable compositeDisposable) {
        this.mApiCall = apiCall;
        this.compositeDisposable = compositeDisposable;

    }

    void requestWeatherInfo(WeatherRequest weatherRequest) {
        compositeDisposable.add(
                mApiCall.getWeatherUpdate(weatherRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Weather>() {
                            @Override
                            public void onSuccess(Weather weather) {
                                onWeatherResponse(weather);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPIError(e.getMessage());
                            }
                        }));
    }

    private void onWeatherResponse(Weather weather) {
        if (weather == null) {
            getActions().onAPIError(R.string.whole_weather_response_corrupted);
        } else {
            if (weather.getError().isEmpty()) {
                Data data = weather.getData();
                if (data == null) {
                    getActions().onAPIError(R.string.weather_data_response_corrupted);
                } else {
                    WeatherItem currentWeather = data.getCurrentWeather();
                    String voiceResponse = data.getVoiceResponse();
                    List<WeatherItem> forecast = data.getForecast();
                    if (currentWeather != null && forecast != null && !forecast.isEmpty()) {
                        if (voiceResponse != null && !voiceResponse.isEmpty()) {
                            getActions().onCurrentWeatherFetched(currentWeather, voiceResponse);
                            getActions().onForecastWeatherFetched(forecast);
                        }
                    }
                }
            } else {
                getActions().onAPIError(weather.getError());
            }
        }
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
    }
}
