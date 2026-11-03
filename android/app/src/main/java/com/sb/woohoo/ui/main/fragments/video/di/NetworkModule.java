package com.sb.woohoo.ui.main.fragments.video.di;

import com.google.gson.Gson;
import com.sb.woohoo.WooHooApp;

import androidx.fragment.app.Fragment;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by Satyaraj on 3/13/2018.
 */
@Module
public class NetworkModule {

    @Provides
    Gson getGson(Fragment fragment) {
        return WooHooApp.get(fragment.getActivity()).getWooHooAppComponent().getGson();
    }

    @Provides
    Retrofit getRetrofit(Fragment fragment) {
        return WooHooApp.get(fragment.getActivity()).getWooHooAppComponent().getRetrofit();
    }
}
