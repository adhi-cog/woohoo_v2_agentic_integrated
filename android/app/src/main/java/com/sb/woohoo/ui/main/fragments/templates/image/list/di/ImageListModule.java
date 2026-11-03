package com.sb.woohoo.ui.main.fragments.templates.image.list.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.templates.image.list.ImageListFragment;

import dagger.Module;
import dagger.Provides;

@Module
public class ImageListModule {
    ImageListFragment imageListFragment;

    public ImageListModule(ImageListFragment imageListFragment) {
        this.imageListFragment = imageListFragment;
    }

    @Provides
    public ImageListFragment getFragment() {
        return imageListFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(imageListFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }
}
