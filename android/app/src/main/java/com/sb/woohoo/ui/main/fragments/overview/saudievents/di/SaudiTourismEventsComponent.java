package com.sb.woohoo.ui.main.fragments.overview.saudievents.di;

import com.sb.woohoo.ui.main.fragments.overview.saudievents.SaudiTourismEventsFragment;

import dagger.Component;

@Component(modules = SaudiTourismEventsModule.class)
public interface SaudiTourismEventsComponent {
    void injectSaudiTourismEventsFragment(SaudiTourismEventsFragment saudiTourismEventsFragment);

}

