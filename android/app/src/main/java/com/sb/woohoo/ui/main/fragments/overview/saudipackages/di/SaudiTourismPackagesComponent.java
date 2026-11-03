package com.sb.woohoo.ui.main.fragments.overview.saudipackages.di;

import com.sb.woohoo.ui.main.fragments.overview.saudipackages.SaudiTourismPackagesFragment;

import dagger.Component;

@Component(modules = SaudiTourismPackagesModule.class)
public interface SaudiTourismPackagesComponent {
    void injectSaudiTourismPackagesFragment(SaudiTourismPackagesFragment saudiTourismPackagesFragment);

}

