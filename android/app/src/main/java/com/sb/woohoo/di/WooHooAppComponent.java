package com.sb.woohoo.di;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.sb.woohoo.WooHooAppScope;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.PicassoModule;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsModule;
import com.sb.woohoo.utils.CameraCapturerCompat;
import com.squareup.picasso.Picasso;

import dagger.Component;
import io.socket.client.Socket;
import retrofit2.Retrofit;

/**
 * Created by admin on 1/31/18.
 */

@WooHooAppScope
@Component(modules = {WooHooApiCallModule.class, PicassoModule.class, SharedPrefsModule.class,  AnalyticsModule.class, SocketModule.class})
public interface WooHooAppComponent {

    Picasso getPicasso();

    WooHooApiCall getWooHooApiCall();

    SharedPrefsHelper getSharedPrefsHelper();

    Gson getGson();

    Retrofit getRetrofit();

    Retrofit.Builder getRetrofitBuilder();

    JsonParser getJsonParser();

    CameraCapturerCompat getCamerSource();

    AnalyticsHelper getAnalyticsHelper();

    Socket getSocket();
}
