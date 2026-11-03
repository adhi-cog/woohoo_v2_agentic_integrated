package com.sb.woohoo.di;

import android.content.Context;
import com.sb.woohoo.ApplicationContext;
import com.sb.woohoo.ContextModule;
import com.sb.woohoo.WooHooAppScope;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsModule;

import dagger.Module;
import dagger.Provides;

@Module(includes = {ContextModule.class, SharedPrefsModule.class})
public class AnalyticsModule {

    @Provides
    @WooHooAppScope
    public AnalyticsHelper getGATracker(@ApplicationContext Context context, SharedPrefsHelper prefsHelper) {
        return new AnalyticsHelper(context, prefsHelper);

    }
}

