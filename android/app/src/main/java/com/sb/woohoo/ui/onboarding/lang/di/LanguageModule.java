package com.sb.woohoo.ui.onboarding.lang.di;


import com.sb.woohoo.ui.onboarding.lang.LanguageAdapter;
import com.sb.woohoo.ui.onboarding.lang.LanguagePresenter;
import com.sb.woohoo.ui.onboarding.lang.LanguageSelectionFragment;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Shrey on 30/01/18.
 */

@Module
public class LanguageModule {

    private LanguageSelectionFragment languageSelectionFragment;

    public LanguageModule(LanguageSelectionFragment languageSelectionFragment) {
        this.languageSelectionFragment = languageSelectionFragment;
    }

    @Provides
    public LanguageSelectionFragment languageSelectionFragment() {
        return languageSelectionFragment;
    }

    @Provides
    public LanguagePresenter getLanguagePresenter() {
        return new LanguagePresenter(languageSelectionFragment);
    }

    @Provides
    public LanguageAdapter getLanguageAdapter() {
        return new LanguageAdapter(languageSelectionFragment);
    }

}
