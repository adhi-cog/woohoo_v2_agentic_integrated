package com.sb.woohoo.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.sb.woohoo.ApplicationContext;
import com.sb.woohoo.ContextModule;
import com.sb.woohoo.WooHooAppScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by admin on 2/21/18.
 */

@Module(includes = ContextModule.class)
public class SharedPrefsModule {

    @Provides
    @WooHooAppScope
    public SharedPrefsHelper sharedPrefsHelper(@ApplicationContext Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return new SharedPrefsHelper(sharedPreferences);
    }

}
