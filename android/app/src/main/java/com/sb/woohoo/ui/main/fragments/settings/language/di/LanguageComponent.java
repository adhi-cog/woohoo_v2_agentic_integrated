package com.sb.woohoo.ui.main.fragments.settings.language.di;

import com.sb.woohoo.ui.main.fragments.screensaver.ScreenSaverFragment;
import com.sb.woohoo.ui.main.fragments.settings.language.LanguageChangeFragment;

import dagger.Component;

@Component (modules = LanguageModule.class)
public interface LanguageComponent {
    void injectLanguageChangeFragment(LanguageChangeFragment languageChangeFragment);
}
