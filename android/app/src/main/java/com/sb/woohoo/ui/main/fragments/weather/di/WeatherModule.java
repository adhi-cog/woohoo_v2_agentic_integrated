package com.sb.woohoo.ui.main.fragments.weather.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.weather.WeatherForecastAdapter;
import com.sb.woohoo.ui.main.fragments.weather.WeatherFragment;
import com.sb.woohoo.ui.main.fragments.weather.WeatherPresenter;
import com.sb.woohoo.ui.main.fragments.weather.WeatherRepository;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class WeatherModule {
    private WeatherFragment weatherFragment;

    public WeatherModule(WeatherFragment weatherFragment) {
        this.weatherFragment = weatherFragment;
    }

    @Provides
    public WeatherFragment provideWeatherFragment() {
        return weatherFragment;
    }

    @Provides
    public WeatherPresenter providesWeatherPresenter(WeatherRepository repository) {
        return new WeatherPresenter(weatherFragment, repository);
    }

    @Provides
    public WooHooApiCall provideWooHooApiCall() {
        return WooHooApp.get(weatherFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    public WeatherForecastAdapter provideForecastAdapter() {
        return new WeatherForecastAdapter();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(weatherFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}