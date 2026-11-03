package com.sb.woohoo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import com.sb.woohoo.analytics.UserProperty;
import com.sb.woohoo.custom.FileLoggingTree;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.data.prefs.SharedPrefsModule;
import com.sb.woohoo.di.AnalyticsModule;
import com.sb.woohoo.di.DaggerWooHooAppComponent;
import com.sb.woohoo.di.SocketModule;
import com.sb.woohoo.di.WooHooApiCallModule;
import com.sb.woohoo.di.WooHooAppComponent;
import com.sb.woohoo.utils.logger.SentryLogger;
import com.sb.woohoo.utils.others.AssetUtils;
import com.sb.woohoo.utils.others.MethodUtils;
import com.sileria.android.Kit;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.TimeZone;

import retrofit2.Retrofit;
import timber.log.Timber;
import timber.log.Timber.DebugTree;

public class WooHooApp extends Application {

    private static SharedPrefsHelper sharedPrefsHelper;
    public static String sDeviceId = null;
    Picasso picasso;
    WooHooApiCall wooHooApiCall;
    WooHooAppComponent wooHooAppComponent;
    AudioManager audioManager;

    public static SimpleCache cache;
    public static ProgressiveMediaSource.Factory mProgressiveMediaSource;
    public static long cacheSize  = 10 * 1024 * 1024;
    public static long maxCacheFileSize  = 90 * 1024 * 1024;
    private LeastRecentlyUsedCacheEvictor cacheEvictor;
    private StandaloneDatabaseProvider standaloneDatabaseProvider;

    private static Context context;
    public static SharedPrefsHelper getSharedPrefsHelper() {
        return sharedPrefsHelper;
    }

    public static WooHooApp get(Activity activity) {
        return (WooHooApp) activity.getApplication();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        WooHooApp.context = getApplicationContext();
        sDeviceId = Settings.Secure.getString(
                getContentResolver(), Settings.Secure.ANDROID_ID);
        initializeWooHooAppComponent();
        initLogger();
        initializeLibraries();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        cacheEvictor = new LeastRecentlyUsedCacheEvictor(cacheSize);
        standaloneDatabaseProvider = new StandaloneDatabaseProvider(this);

        cache = new SimpleCache(
                new File(this.getCacheDir(), "media"),
                cacheEvictor,
                standaloneDatabaseProvider);

        mProgressiveMediaSource = new ProgressiveMediaSource.Factory(
                new CacheDataSource.Factory()
                        .setCache(cache)
                        .setUpstreamDataSourceFactory(
                                new DefaultHttpDataSource.Factory()
                                        .setUserAgent(getString(R.string.app_name))
                        )
                        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        );
    }

    public static Context getAppContext() {
        return WooHooApp.context;
    }
    @Override
    public void onTerminate() {
        context = null;
        cacheEvictor = null;
        audioManager = null;
        cache = null;
        mProgressiveMediaSource = null;
        standaloneDatabaseProvider = null;
        super.onTerminate();
        Kit.destroy();
    }

    private void initializeLibraries() {
        Kit.init(getApplicationContext());
        Fresco.initialize(this);
        String dir_path = Environment.getExternalStorageDirectory() + "/woohoo_logs";

        File logsDirectory = new File(dir_path);

        int logFileMaxSizeBytes = 2 * 1024 * 1024; // 2Mb
        LoggerWrapper.d("path",logsDirectory.getAbsolutePath());
        try {

           /* AndroidLogger.initialize(
                    this,
                    this.getFilesDir(),
                    "",
                    logFileMaxSizeBytes,
                    false
            );*/
            Timber.plant(new DebugTree());
            Timber.plant(new FileLoggingTree(this, getFilesDir(), "", logFileMaxSizeBytes));
        } catch (Exception e) {
            e.printStackTrace();
            LoggerWrapper.e("initializeLibraries:",e.getMessage());
            // Some error happened - most likely there is no free space on the system
        }

        FirebaseApp.initializeApp(this);
        FirebaseAnalytics.getInstance(this).setUserId(sharedPrefsHelper.get(SharedPrefsKey.PREF_KEY_DEVICE_ID, ""));
        FirebaseAnalytics.getInstance(this).setUserProperty(UserProperty.DOMAIN_ID, sharedPrefsHelper.get(SharedPrefsKey.PREF_DOMAIN_ID, ""));
        FirebaseAnalytics.getInstance(this).setUserProperty(UserProperty.DEVICE_ID, sharedPrefsHelper.get(SharedPrefsKey.PREF_KEY_DEVICE_ID, ""));
        FirebaseAnalytics.getInstance(this).setUserProperty(UserProperty.SDK_VERSION, String.valueOf(Build.VERSION.SDK_INT));
        FirebaseAnalytics.getInstance(this).setUserProperty(UserProperty.MODEL, Build.MODEL);
        FirebaseAnalytics.getInstance(this).setUserProperty(UserProperty.OS_VERSION, Build.VERSION.RELEASE);
        FirebaseAnalytics.getInstance(this).setUserProperty(UserProperty.OEM_NAME, Build.MANUFACTURER);
        FirebaseAnalytics.getInstance(this).setUserProperty(UserProperty.APP_VERSION, BuildConfig.VERSION_NAME);
        FirebaseAnalytics.getInstance(this).setUserProperty(UserProperty.APP_BUILD, String.valueOf(BuildConfig.VERSION_CODE));
        FirebaseAnalytics.getInstance(this).setUserProperty(UserProperty.APP_NAMESPACE, BuildConfig.APPLICATION_ID);
        FirebaseAnalytics.getInstance(this).setUserProperty(UserProperty.TIME_ZONE_OFFSET, TimeZone.getDefault().getDisplayName());
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            FirebaseAnalytics.getInstance(this).setUserProperty(UserProperty.DEVICE_ORIENTATION, "landscape");
        else
            FirebaseAnalytics.getInstance(this).setUserProperty(UserProperty.DEVICE_ORIENTATION, "portrait");
        if(MethodUtils.isNetworkAvailable(this))
            FirebaseAnalytics.getInstance(this).setUserProperty(UserProperty.NETWORK_CONNECTION_STATUS, "connected");
        else
            FirebaseAnalytics.getInstance(this).setUserProperty(UserProperty.NETWORK_CONNECTION_STATUS, "disconnected");


        FirebaseCrashlytics.getInstance().setUserId(sharedPrefsHelper.get(SharedPrefsKey.PREF_KEY_DEVICE_ID, ""));
        FirebaseCrashlytics.getInstance().setCustomKey(UserProperty.DOMAIN_ID, sharedPrefsHelper.get(SharedPrefsKey.PREF_DOMAIN_ID, ""));
        FirebaseCrashlytics.getInstance().setCustomKey(UserProperty.DEVICE_ID, sharedPrefsHelper.get(SharedPrefsKey.PREF_KEY_DEVICE_ID, ""));
        FirebaseCrashlytics.getInstance().setCustomKey(UserProperty.SDK_VERSION, String.valueOf(Build.VERSION.SDK_INT));
        FirebaseCrashlytics.getInstance().setCustomKey(UserProperty.MODEL, Build.MODEL);
        FirebaseCrashlytics.getInstance().setCustomKey(UserProperty.OS_VERSION, Build.VERSION.RELEASE);
        FirebaseCrashlytics.getInstance().setCustomKey(UserProperty.OEM_NAME, Build.MANUFACTURER);
        FirebaseCrashlytics.getInstance().setCustomKey(UserProperty.APP_VERSION, BuildConfig.VERSION_NAME);
        FirebaseCrashlytics.getInstance().setCustomKey(UserProperty.APP_BUILD, String.valueOf(BuildConfig.VERSION_CODE));
        FirebaseCrashlytics.getInstance().setCustomKey(UserProperty.APP_NAMESPACE, BuildConfig.APPLICATION_ID);
        FirebaseCrashlytics.getInstance().setCustomKey(UserProperty.TIME_ZONE_OFFSET, TimeZone.getDefault().getDisplayName());
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            FirebaseCrashlytics.getInstance().setCustomKey(UserProperty.DEVICE_ORIENTATION, "landscape");
        else
            FirebaseCrashlytics.getInstance().setCustomKey(UserProperty.DEVICE_ORIENTATION, "portrait");
        if(MethodUtils.isNetworkAvailable(this))
            FirebaseCrashlytics.getInstance().setCustomKey(UserProperty.NETWORK_CONNECTION_STATUS, "connected");
        else
            FirebaseCrashlytics.getInstance().setCustomKey(UserProperty.NETWORK_CONNECTION_STATUS, "disconnected");
    }

    public WooHooApiCall getWooHooApiCall() {
        return wooHooApiCall;
    }

    private void initializeWooHooAppComponent() {
        wooHooAppComponent =
                DaggerWooHooAppComponent
                        .builder()
                        .contextModule(new ContextModule(this))
                        .analyticsModule(new AnalyticsModule())
                        //.cameraModule(new CameraModule())
                        .wooHooApiCallModule(new WooHooApiCallModule())
                        .sharedPrefsModule(new SharedPrefsModule())
                        .socketModule(new SocketModule())
                        .build();
        wooHooApiCall = wooHooAppComponent.getWooHooApiCall();
        picasso = wooHooAppComponent.getPicasso();
        sharedPrefsHelper = wooHooAppComponent.getSharedPrefsHelper();
        AssetUtils.getModelFile(getApplicationContext());
    }

    private Retrofit.Builder getRetrofitNoAuth() {
        return wooHooAppComponent.getRetrofitBuilder();
    }

    public WooHooApiCall getWoohooApiNoAuth() {
        return getRetrofitNoAuth().build().create(WooHooApiCall.class);
    }

    public WooHooAppComponent getWooHooAppComponent() {
        return wooHooAppComponent;
    }


    private void initLogger() {
        AppCenter.start((Application) getApplicationContext(), getString(R.string.app_center_app_secret),
                Analytics.class, Crashes.class);
        AppCenter.setLogLevel(Log.VERBOSE);

        LoggerWrapper.plusAssign(new SentryLogger(BuildConfig.APPLICATION_ID));
    }
}