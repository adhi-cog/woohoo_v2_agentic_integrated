package com.sb.woohoo.ui.main.fragments.templates.text.list.di;

import com.sb.woohoo.ui.main.fragments.templates.text.list.ListFragment;

import dagger.Component;

@Component(modules = ListModule.class)
public interface ListComponent {
    void injectListTempleteComponent(ListFragment listFragment);
}
