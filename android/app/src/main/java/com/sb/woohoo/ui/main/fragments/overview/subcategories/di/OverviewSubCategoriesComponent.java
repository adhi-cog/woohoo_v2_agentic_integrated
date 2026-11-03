package com.sb.woohoo.ui.main.fragments.overview.subcategories.di;

import com.sb.woohoo.ui.main.fragments.overview.categories.OverviewCategoriesFragment;
import com.sb.woohoo.ui.main.fragments.overview.subcategories.OverviewSubCategoriesFragment;

import dagger.Component;

@Component(modules = OverviewSubCategoriesModule.class)
public interface OverviewSubCategoriesComponent {

    void injectOverviewSubCategoriesFragment(OverviewSubCategoriesFragment overviewSubCategoriesFragment);

}

