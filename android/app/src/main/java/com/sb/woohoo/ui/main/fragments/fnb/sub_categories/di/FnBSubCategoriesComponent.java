package com.sb.woohoo.ui.main.fragments.fnb.sub_categories.di;

import com.sb.woohoo.ui.main.fragments.fnb.sub_categories.FnBSubCategoriesFragment;

import dagger.Component;

@Component(modules = FnBSubCategoriesModule.class)
public interface FnBSubCategoriesComponent {

    void injectFnBSubCategoriesFragment(FnBSubCategoriesFragment feedbackFragment);

}

