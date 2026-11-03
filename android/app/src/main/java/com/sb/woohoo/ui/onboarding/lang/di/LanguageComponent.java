package com.sb.woohoo.ui.onboarding.lang.di;

import com.sb.woohoo.ui.onboarding.lang.LanguageSelectionFragment;

import dagger.Component;

/**
 * Created by Shrey on 30/01/18.
 */

@Component(modules = LanguageModule.class)
@LanguageScope
public interface LanguageComponent {

    void injectLanguageFragment(LanguageSelectionFragment languageSelectionFragment);

}
