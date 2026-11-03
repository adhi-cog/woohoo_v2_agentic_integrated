package com.sb.woohoo.ui.main.fragments.fnb.leftpane.di;

import com.sb.woohoo.ui.main.fragments.fnb.leftpane.FnBLeftPaneFragment;

import dagger.Component;

@Component(modules = FnBLeftPaneModule.class)
public interface FnBLeftPaneComponent {
    void injectFnBLeftPaneFragment(FnBLeftPaneFragment fragment);
}
