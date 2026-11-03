package com.sb.woohoo.ui.main.fragments.overview.leftpane.di;

import com.sb.woohoo.ui.main.fragments.fnb.leftpane.FnBLeftPaneFragment;
import com.sb.woohoo.ui.main.fragments.overview.leftpane.ItemDetailLeftPaneFragment;

import dagger.Module;
import dagger.Provides;

@Module
public class ItemDetailLeftPaneModule {
    private ItemDetailLeftPaneFragment mFragment;

    public ItemDetailLeftPaneModule(ItemDetailLeftPaneFragment fragment) {
        this.mFragment = fragment;
    }

    @Provides
    public ItemDetailLeftPaneFragment getLeftPaneFragment() {
        return mFragment;
    }

}