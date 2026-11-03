package com.sb.woohoo.data.network;

import android.content.Context;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.sb.woohoo.ApplicationContext;
import com.sb.woohoo.ContextModule;
import com.sb.woohoo.WooHooAppScope;
import com.sb.woohoo.data.network.di.NetworkModule;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module(includes = {ContextModule.class, NetworkModule.class})
public class PicassoModule {

    @Provides
    @WooHooAppScope
    public Picasso picasso(@ApplicationContext Context context, OkHttp3Downloader okHttp3Downloader) {
        return new Picasso.Builder(context)
                .build();
    }

    @Provides
    @WooHooAppScope
    public OkHttp3Downloader okHttp3Downloader(OkHttpClient okHttpClient) {
        return new OkHttp3Downloader(okHttpClient);
    }

}
