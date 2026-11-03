package com.sb.woohoo.ui.main.fragments.buysim.emailverification.di;

import com.sb.woohoo.ui.main.fragments.buysim.emailverification.EmailVerificationFragment;

import dagger.Component;

@Component(modules = EmailVerificationModule.class)
public interface EmailVerificationComponent {
    void injectEmailVerificationFragment(EmailVerificationFragment emailVerificationFragment);
}
