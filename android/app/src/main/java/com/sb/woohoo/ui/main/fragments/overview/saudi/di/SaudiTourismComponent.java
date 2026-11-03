package com.sb.woohoo.ui.main.fragments.overview.saudi.di;

import com.sb.woohoo.ui.main.fragments.overview.saudi.SaudiTourismFragment;

import dagger.Component;

@Component(modules = SaudiTourismModule.class)
public interface SaudiTourismComponent {
    void injectSaudiTourismFragment(SaudiTourismFragment saudiTourismFragment);

}

