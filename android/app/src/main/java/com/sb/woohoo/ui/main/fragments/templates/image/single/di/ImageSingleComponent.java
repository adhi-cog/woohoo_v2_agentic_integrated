package com.sb.woohoo.ui.main.fragments.templates.image.single.di;

import com.sb.woohoo.ui.main.fragments.templates.image.single.ImageSingleFragment;

import dagger.Component;

@Component(modules = ImageSingleModule.class)
public interface ImageSingleComponent {
    void injectImageSingleComponent(ImageSingleFragment imageSingleFragment);
}
