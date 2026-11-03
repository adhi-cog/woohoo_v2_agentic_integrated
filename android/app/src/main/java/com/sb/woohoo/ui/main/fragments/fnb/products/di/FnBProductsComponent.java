package com.sb.woohoo.ui.main.fragments.fnb.products.di;

import com.sb.woohoo.ui.main.fragments.fnb.categories.FnBCategoriesFragment;
import com.sb.woohoo.ui.main.fragments.fnb.products.FnBProductsFragment;

import dagger.Component;

@Component(modules = FnBProductsModule.class)
public interface FnBProductsComponent {

    void injectFnBProductsFragment(FnBProductsFragment feedbackFragment);

}

