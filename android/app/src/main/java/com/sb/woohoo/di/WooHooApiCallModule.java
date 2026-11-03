package com.sb.woohoo.di;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.sb.woohoo.BuildConfig;
import com.sb.woohoo.WooHooAppScope;
import com.sb.woohoo.data.network.DateTimeConverter;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.network.di.NetworkModule;

import org.joda.time.DateTime;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by admin on 1/31/18.
 */

@Module(includes = NetworkModule.class)
public class WooHooApiCallModule {

    @Provides
    @WooHooAppScope
    public WooHooApiCall wooHooApiCall(Retrofit wooHooApiCallRetrofit) {
        return wooHooApiCallRetrofit.create(WooHooApiCall.class);
    }

    @Provides
    @WooHooAppScope
    public Gson gson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeConverter());
        return gsonBuilder.create();
    }

    @Provides
    @WooHooAppScope
    public Retrofit retrofit(OkHttpClient okHttpClient, Gson gson) {
        return new Retrofit.Builder()
//                .addConverterFactory(new EnvelopeConverterFactory())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(okHttpClient)
                .baseUrl(BuildConfig.WOOHOO_BASE_URL)
                .build();
    }

    @Provides
    @WooHooAppScope
    public Retrofit.Builder retrofitBuilder(OkHttpClient.Builder okHttpClientBuilder, Gson gson) {
        return new Retrofit.Builder()
//                .addConverterFactory(new EnvelopeConverterFactory())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(okHttpClientBuilder.build())
                .baseUrl(BuildConfig.WOOHOO_BASE_URL);
    }

    @Provides
    @WooHooAppScope
    public JsonParser getJsonParser() {
        return new JsonParser();
    }
}
