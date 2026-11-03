package com.sb.woohoo.ui.main.fragments.fnb.history.di;

import com.sb.woohoo.ui.main.fragments.fnb.history.FnBHistoryFragment;

import dagger.Component;

@Component(modules = FnBHistoryModule.class)
public interface FnBHistoryComponent {

    void injectFnBHistoryFragment(FnBHistoryFragment fnBHistoryFragment);

}

