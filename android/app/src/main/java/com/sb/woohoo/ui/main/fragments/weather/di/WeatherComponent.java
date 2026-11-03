package com.sb.woohoo.ui.main.fragments.weather.di;

import com.sb.woohoo.ui.main.fragments.weather.WeatherFragment;

import dagger.Component;

@WeatherScope
@Component(modules = WeatherModule.class)
public interface WeatherComponent {
    void injectWeatherFragment(WeatherFragment fragment);
}