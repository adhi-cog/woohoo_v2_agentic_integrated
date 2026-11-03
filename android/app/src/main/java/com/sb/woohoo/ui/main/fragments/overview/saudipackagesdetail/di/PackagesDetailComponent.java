package com.sb.woohoo.ui.main.fragments.overview.saudipackagesdetail.di;

import com.sb.woohoo.ui.main.fragments.overview.saudipackagesdetail.PackagesDetailFragment;

import dagger.Component;

@Component(modules = PackagesDetailModule.class)
public interface PackagesDetailComponent {
    void injectPackagesDetailFragment(PackagesDetailFragment eventDetailFragment);

}

