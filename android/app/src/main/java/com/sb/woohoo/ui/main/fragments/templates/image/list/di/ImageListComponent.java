package com.sb.woohoo.ui.main.fragments.templates.image.list.di;

import com.sb.woohoo.ui.main.fragments.templates.image.list.ImageListFragment;

import dagger.Component;

@Component(modules = ImageListModule.class)
public interface ImageListComponent {
    void injectImageListComponent(ImageListFragment imageListFragment);
}
