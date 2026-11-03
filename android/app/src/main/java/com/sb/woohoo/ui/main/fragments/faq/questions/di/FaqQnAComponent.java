package com.sb.woohoo.ui.main.fragments.faq.questions.di;

import com.sb.woohoo.ui.main.fragments.faq.categories.FaqCategoriesFragment;
import com.sb.woohoo.ui.main.fragments.faq.questions.FaqQnAFragment;

import dagger.Component;

@Component(modules = FaqQnAModule.class)
public interface FaqQnAComponent {
    void injectFaqQnAFragment(FaqQnAFragment faqQnAFragment);
}
