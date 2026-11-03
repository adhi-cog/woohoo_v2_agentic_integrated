package com.sb.woohoo.ui.main.fragments.overview.itemDetail.di;

import com.sb.woohoo.ui.main.fragments.fnb.categories.FnBCategoriesFragment;
import com.sb.woohoo.ui.main.fragments.overview.itemDetail.ItemDetailFragment;

import dagger.Component;

@Component(modules = ItemDetailModule.class)
public interface ItemDetailComponent {

    void injectItemDetailFragment(ItemDetailFragment itemDetailFragment);

}

