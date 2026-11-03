package com.sb.woohoo.ui.main.fragments.home.di;

import com.sb.woohoo.ui.main.fragments.home.HomeFragment;

import dagger.Component;

/**
 * Created by Shrey on 18/03/18.
 */

@Component(modules = HomeModule.class)
public interface HomeComponent {
    void injectHomeFragment(HomeFragment fragment);
}
