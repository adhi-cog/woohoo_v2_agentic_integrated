package com.sb.woohoo.ui.main.fragments.overview.saudiexperiencedetail.di;

import com.sb.woohoo.ui.main.fragments.overview.saudiexperiencedetail.ExperienceDetailFragment;

import dagger.Component;

@Component(modules = ExperienceDetailModule.class)
public interface ExperienceDetailComponent {
    void injectExperienceDetailFragment(ExperienceDetailFragment experienceDetailFragment);

}

