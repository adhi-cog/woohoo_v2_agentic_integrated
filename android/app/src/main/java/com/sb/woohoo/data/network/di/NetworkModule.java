package com.sb.woohoo.data.network.di;


import static com.sb.woohoo.utils.LocaleHelper.getLanguage;

import android.content.Context;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ApplicationContext;
import com.sb.woohoo.ContextModule;
import com.sb.woohoo.WooHooAppScope;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.data.prefs.SharedPrefsModule;
import com.sb.woohoo.di.TokenAuthenticator;

import java.io.File;
import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by admin on 1/31/18.
 */

@Module(includes = {ContextModule.class, SharedPrefsModule.class})
public class NetworkModule {

    private final int CONNECT_TIMEOUT = 30 * 1000;
    private final int READ_TIMEOUT = 30 * 1000;
    private final long CACHE_SIZE = 10 * 10 * 1000;
    private final String FILE_CACHE_CHILD = "okhttp_cache";

    private Context context;

    @Provides
    @WooHooAppScope
    public HttpLoggingInterceptor loggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message -> LoggerWrapper.i(NetworkModule.class.getSimpleName(), message));
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    @Provides
    @WooHooAppScope
    public Cache cache(File cacheFile) {
        return new Cache(cacheFile, CACHE_SIZE);
    }

    @Provides
    @WooHooAppScope
    public File cacheFile(@ApplicationContext Context context) {
        this.context = context;
        return new File(context.getCacheDir(), FILE_CACHE_CHILD);
    }

    @Provides
    @WooHooAppScope
    public Authenticator getAuthenticator(@ApplicationContext Context context) {
        this.context = context;
        return new TokenAuthenticator(context);
    }


    @Provides
    @WooHooAppScope
    public Interceptor getInterceptor(final SharedPrefsHelper prefsHelper) {
        return chain -> {
            Request request = chain.request();

            request = request.newBuilder()
                    .addHeader("language", getLanguage(context))
                    .build();

            if (request.header("No-Authentication") == null) {
                String sessionToken = prefsHelper.get(SharedPrefsKey.PREF_KEY_SESSION_TOKEN, "");
                if (!sessionToken.isEmpty()) {
                    request = request.newBuilder()
                            .addHeader("Authorization", sessionToken)
                            .build();
                }
            }

            return chain.proceed(request);
        };
    }

    @Provides
    @WooHooAppScope
    public OkHttpClient okHttpClient(HttpLoggingInterceptor loggingInterceptor, Cache cache, Authenticator authenticator, Interceptor interceptor, @ApplicationContext Context context) {

        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .cache(cache)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .authenticator(authenticator)
                .addInterceptor(interceptor)
//                .addInterceptor(new ChuckerInterceptor(context))
                .build();
    }

    @Provides
    @WooHooAppScope
    public OkHttpClient.Builder okHttpClientBuilder(HttpLoggingInterceptor loggingInterceptor, Cache cache, Authenticator authenticator, @ApplicationContext Context context) {

        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .cache(cache)
//                .addInterceptor(new ChuckerInterceptor(context))
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS);
    }
}
