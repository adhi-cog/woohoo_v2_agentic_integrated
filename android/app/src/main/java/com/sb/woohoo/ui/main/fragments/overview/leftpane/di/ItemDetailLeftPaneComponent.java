package com.sb.woohoo.ui.main.fragments.overview.leftpane.di;

import com.sb.woohoo.ui.main.fragments.fnb.leftpane.FnBLeftPaneFragment;
import com.sb.woohoo.ui.main.fragments.overview.leftpane.ItemDetailLeftPaneFragment;

import dagger.Component;

@Component(modules = ItemDetailLeftPaneModule.class)
public interface ItemDetailLeftPaneComponent {
    void injectItemDetailLeftPaneFragment(ItemDetailLeftPaneFragment fragment);
}
