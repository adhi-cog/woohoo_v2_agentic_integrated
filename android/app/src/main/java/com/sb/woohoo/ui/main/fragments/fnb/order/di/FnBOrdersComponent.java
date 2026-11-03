package com.sb.woohoo.ui.main.fragments.fnb.order.di;

import com.sb.woohoo.ui.main.fragments.fnb.categories.FnBCategoriesFragment;
import com.sb.woohoo.ui.main.fragments.fnb.order.FnBOrderFragment;

import dagger.Component;

@Component(modules = FnBOrdersModule.class)
public interface FnBOrdersComponent {

    void injectFnBOrdersFragment(FnBOrderFragment feedbackFragment);

}

