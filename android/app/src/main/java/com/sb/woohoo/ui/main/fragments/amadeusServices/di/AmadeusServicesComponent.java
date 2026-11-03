package com.sb.woohoo.ui.main.fragments.amadeusServices.di;

import com.sb.woohoo.ui.main.fragments.amadeusServices.AmadeusServicesFragment;

import dagger.Component;

@Component(modules = AmadeusServiceModule.class)
public interface AmadeusServicesComponent {
    void injectFragment(AmadeusServicesFragment amadeusServicesFragment);
}
