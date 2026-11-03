package com.sb.woohoo.ui.main.fragments.templates.image.single.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.templates.image.single.ImageSingleFragment;

import dagger.Module;
import dagger.Provides;

@Module
public class ImageSingleModule {

    ImageSingleFragment imageSingleFragment;

    public ImageSingleModule(ImageSingleFragment imageSingleFragment) {
        this.imageSingleFragment = imageSingleFragment;

    }

    @Provides
    public ImageSingleFragment getFragment() {
        return imageSingleFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(imageSingleFragment.getActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

}
