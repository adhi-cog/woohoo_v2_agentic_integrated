package com.sb.woohoo.ui.main.fragments.home.di;

import com.sb.woohoo.ui.main.fragments.home.HomeLeftPaneFragment;

import dagger.Component;

@Component(modules = HomeLeftPaneModule.class)
public interface HomeLeftPaneComponent {
    void injectHomeLeftPaneFragment(HomeLeftPaneFragment fragment);
}
