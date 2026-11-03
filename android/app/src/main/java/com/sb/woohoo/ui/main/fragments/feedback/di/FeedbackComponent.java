package com.sb.woohoo.ui.main.fragments.feedback.di;

import com.sb.woohoo.ui.main.fragments.feedback.FeedbackFragment;

import dagger.Component;

@Component(modules = FeedbackModule.class)
public interface FeedbackComponent {

    void injectFeedbackFragment(FeedbackFragment feedbackFragment);

}
