package com.sb.woohoo.ui.main.fragments.webview.di;

import com.sb.woohoo.ui.main.fragments.webview.WebViewFragment;

import dagger.Component;

/**
 * {@link Component} for {@link WebViewFragment}.
 * This needs to be updated
 *
 * @author subham satyam.
 */
@Component(modules = WebViewModule.class)
public interface WebViewComponent {
    void injectWebViewFragment(WebViewFragment fragment);
}