package com.sb.woohoo.ui.main.fragments.screensaver.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.screensaver.ScreenSaverFragment;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class ScreenSaverModule {

    private ScreenSaverFragment screenSaverFragment;

    public ScreenSaverModule(ScreenSaverFragment screenSaverFragment) {
        this.screenSaverFragment = screenSaverFragment;
    }

    @Provides
    ScreenSaverFragment provideScreenSaverFragment(){
        return screenSaverFragment;
    }

    @Provides
    WooHooApiCall provideWooHooAPICall(){
        return WooHooApp.get(screenSaverFragment.getParentActivity()).getWooHooApiCall();
    }

    @Provides
    CompositeDisposable provideCompositeDisposable(){
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(screenSaverFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
