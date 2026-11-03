package com.sb.woohoo.ui.main.fragments.interel.di;

import com.sb.woohoo.ui.main.fragments.interel.InterelUtilityFragment;

import dagger.Component;

@Component(modules = InterelUtilityModule.class)
public interface InterelUtilityComponent {
    void injectInterelUtilityComponent(InterelUtilityFragment interelUtilityFragment);
}
