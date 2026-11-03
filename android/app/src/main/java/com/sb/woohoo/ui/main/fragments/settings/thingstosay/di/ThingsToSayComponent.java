package com.sb.woohoo.ui.main.fragments.settings.thingstosay.di;

import com.sb.woohoo.ui.main.fragments.settings.thingstosay.ThingsToSayFragment;

import dagger.Component;

@ThingsToSayScope
@Component(modules = ThingsToSayModule.class)
public interface ThingsToSayComponent {
    void injectThingsToSay(ThingsToSayFragment fragment);
}
