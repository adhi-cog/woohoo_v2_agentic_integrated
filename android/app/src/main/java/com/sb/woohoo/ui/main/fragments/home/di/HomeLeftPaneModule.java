package com.sb.woohoo.ui.main.fragments.home.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.ui.main.fragments.home.HomeLeftPaneFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;

@Module
public class HomeLeftPaneModule {
    private HomeLeftPaneFragment mFragment;

    public HomeLeftPaneModule(HomeLeftPaneFragment fragment) {
        this.mFragment = fragment;
    }

    @Provides
    public HomeLeftPaneFragment getLeftPaneFragment() {
        return mFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }
}