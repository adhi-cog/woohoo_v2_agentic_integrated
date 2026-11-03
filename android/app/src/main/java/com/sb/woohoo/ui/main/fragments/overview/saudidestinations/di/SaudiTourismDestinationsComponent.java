package com.sb.woohoo.ui.main.fragments.overview.saudidestinations.di;

import com.sb.woohoo.ui.main.fragments.overview.saudidestinations.SaudiTourismDestinationsFragment;
import com.sb.woohoo.ui.main.fragments.overview.saudipackages.SaudiTourismPackagesFragment;
import com.sb.woohoo.ui.main.fragments.overview.saudipackages.di.SaudiTourismPackagesModule;

import dagger.Component;

@Component(modules = SaudiTourismDestinationsModule.class)
public interface SaudiTourismDestinationsComponent {
    void injectSaudiTourismDestinationsFragment(SaudiTourismDestinationsFragment saudiTourismDestinationsFragment);

}

