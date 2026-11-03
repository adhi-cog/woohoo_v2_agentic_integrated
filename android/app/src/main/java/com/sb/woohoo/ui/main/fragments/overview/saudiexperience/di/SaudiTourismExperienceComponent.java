package com.sb.woohoo.ui.main.fragments.overview.saudiexperience.di;


import com.sb.woohoo.ui.main.fragments.overview.saudiexperience.SaudiTourismExperienceFragment;

import dagger.Component;

@Component(modules = SaudiTourismExperienceModule.class)
public interface SaudiTourismExperienceComponent {
    void injectSaudiTourismExperienceFragment(SaudiTourismExperienceFragment saudiTourismExperienceFragment);

}

