package com.sb.woohoo;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {

    private final Context context;
    private final WooHooApp application;

    public ContextModule(WooHooApp context) {
        this.application = context;
        this.context = context.getApplicationContext();
    }

    @Provides
    public WooHooApp getApplication() {
        return application;
    }

    @Provides
    @ApplicationContext
    public Context context() {
        return context;
    }
}
