package com.sb.woohoo.ui.main.fragments.overview.categories.di;

import com.sb.woohoo.ui.main.fragments.overview.categories.OverviewCategoriesFragment;

import dagger.Component;

@Component(modules = OverviewCategoriesModule.class)
public interface OverviewCategoriesComponent {
    void injectOverviewCategoriesFragment(OverviewCategoriesFragment overviewCategoriesFragment);

}

