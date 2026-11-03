package com.sb.woohoo.ui.main.fragments.covid.di;

import com.sb.woohoo.ui.main.fragments.covid.CovidFragment;

import dagger.Component;

@Component(modules = CovidModule.class)
public interface CovidComponent {

    void injectCovidFragment(CovidFragment feedbackFragment);

}
