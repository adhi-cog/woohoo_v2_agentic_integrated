package com.sb.woohoo.ui.main.fragments.overview.item.di;

import com.sb.woohoo.ui.main.fragments.overview.item.OverviewItemFragment;
import com.sb.woohoo.ui.main.fragments.overview.subcategories.OverviewSubCategoriesFragment;

import dagger.Component;

@Component(modules = OverviewItemModule.class)
public interface OverviewItemComponent {

    void injectOverviewItemFragment(OverviewItemFragment overviewItemFragment);

}

