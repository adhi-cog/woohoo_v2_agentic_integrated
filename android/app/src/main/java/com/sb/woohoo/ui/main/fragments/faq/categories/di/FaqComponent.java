package com.sb.woohoo.ui.main.fragments.faq.categories.di;

import com.sb.woohoo.ui.main.fragments.faq.categories.FaqCategoriesFragment;

import dagger.Component;

@Component(modules = FaqModule.class)
public interface FaqComponent {
    void injectFaqCategoriesFragment(FaqCategoriesFragment faqCategoriesFragment);
}
