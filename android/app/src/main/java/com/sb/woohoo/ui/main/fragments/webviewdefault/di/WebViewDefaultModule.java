package com.sb.woohoo.ui.main.fragments.webviewdefault.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.ui.main.fragments.webviewdefault.WebViewDefaultFragment;
import com.sb.woohoo.ui.main.fragments.webviewdefault.WebViewDefaultPresenter;
import com.sb.woohoo.ui.main.fragments.webviewdefault.WebViewDefaultRepository;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

/**
 * {@link Component} for {@link WebViewDefaultFragment}.
 * This needs to be updated
 *
 */
@Module
public class WebViewDefaultModule {
    private WebViewDefaultFragment mFragment;

    public WebViewDefaultModule(WebViewDefaultFragment fragment) {
        this.mFragment = fragment;
    }

    @Provides
    public WebViewDefaultFragment getWebViewDefaultFragment() {
        return mFragment;
    }

    @Provides
    public WebViewDefaultPresenter getWebViewDefaultPresenter(WebViewDefaultRepository repository) {
        return new WebViewDefaultPresenter(mFragment, repository);
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}