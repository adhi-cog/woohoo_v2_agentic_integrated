package com.sb.woohoo.ui.main.fragments.fnb.categories.di;

import com.sb.woohoo.ui.main.fragments.fnb.categories.FnBCategoriesFragment;

import dagger.Component;

@Component(modules = FnBCategoriesModule.class)
public interface FnBCategoriesComponent {

    void injectFnBCategoriesFragment(FnBCategoriesFragment feedbackFragment);

}

