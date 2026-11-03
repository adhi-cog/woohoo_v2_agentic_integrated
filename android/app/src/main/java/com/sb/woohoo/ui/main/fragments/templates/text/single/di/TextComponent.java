package com.sb.woohoo.ui.main.fragments.templates.text.single.di;

import com.sb.woohoo.ui.main.fragments.templates.text.single.TextFragment;

import dagger.Component;

@Component(modules = TextModule.class)
public interface TextComponent {

    void injectTextTemplateComponent(TextFragment textFragment);
}
