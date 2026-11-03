package com.sb.woohoo.ui.main.fragments.fnb.leftpane.di;

import com.sb.woohoo.ui.main.fragments.fnb.leftpane.FnBLeftPaneFragment;

import dagger.Module;
import dagger.Provides;

@Module
public class FnBLeftPaneModule {
    private FnBLeftPaneFragment mFragment;

    public FnBLeftPaneModule(FnBLeftPaneFragment fragment) {
        this.mFragment = fragment;
    }

    @Provides
    public FnBLeftPaneFragment getLeftPaneFragment() {
        return mFragment;
    }

}