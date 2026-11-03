package com.sb.woohoo.ui.main.fragments.webview.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.ui.main.fragments.webview.WebViewFragment;
import com.sb.woohoo.ui.main.fragments.webview.WebViewPresenter;
import com.sb.woohoo.ui.main.fragments.webview.WebViewRepository;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

/**
 * {@link Component} for {@link WebViewFragment}.
 * This needs to be updated
 *
 * @author subham satyam.
 */
@Module
public class WebViewModule {
    private WebViewFragment mFragment;

    public WebViewModule(WebViewFragment fragment) {
        this.mFragment = fragment;
    }

    @Provides
    public WebViewFragment getWebViewFragment() {
        return mFragment;
    }

    @Provides
    public WebViewPresenter getWebViewPresenter(WebViewRepository repository) {
        return new WebViewPresenter(mFragment, repository);
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}