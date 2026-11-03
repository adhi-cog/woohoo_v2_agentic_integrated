package com.sb.woohoo.ui.main.fragments.settings.language.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.screensaver.ScreenSaverFragment;
import com.sb.woohoo.ui.main.fragments.settings.language.LanguageChangeFragment;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class LanguageModule {

    private LanguageChangeFragment languageChangeFragment;

    public LanguageModule(LanguageChangeFragment languageChangeFragment) {
        this.languageChangeFragment = languageChangeFragment;
    }

    @Provides
    LanguageChangeFragment provideScreenSaverFragment(){
        return languageChangeFragment;
    }

    @Provides
    WooHooApiCall provideWooHooAPICall(){
        return WooHooApp.get(languageChangeFragment.getParentActivity()).getWooHooApiCall();
    }

    @Provides
    CompositeDisposable provideCompositeDisposable(){
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(languageChangeFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
