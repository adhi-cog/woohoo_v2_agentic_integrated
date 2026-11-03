package com.sb.woohoo.ui.onboarding.login.di;

import com.sb.woohoo.ui.onboarding.lang.di.LanguageScope;
import com.sb.woohoo.ui.onboarding.login.LoginFragment;

import dagger.Component;

/**
 * Created by admin on 2/20/18.
 */

@Component(modules = LoginModule.class)
@LanguageScope
public interface LoginComponent {

    void injectLoginFragment(LoginFragment loginFragment);


}
