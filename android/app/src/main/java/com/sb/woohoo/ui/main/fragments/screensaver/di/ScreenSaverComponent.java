package com.sb.woohoo.ui.main.fragments.screensaver.di;

import com.sb.woohoo.ui.main.fragments.screensaver.ScreenSaverFragment;

import dagger.Component;

@Component (modules = ScreenSaverModule.class)
public interface ScreenSaverComponent {
    void injectScreenSaverFragment(ScreenSaverFragment screenSaverFragment);
}
