package com.sb.woohoo.ui.main.fragments.webviewdefault.di;

import com.sb.woohoo.ui.main.fragments.webviewdefault.WebViewDefaultFragment;

import dagger.Component;

/**
 * {@link Component} for {@link WebViewDefaultFragment}.
 * This needs to be updated
 *
 */
@Component(modules = WebViewDefaultModule.class)
public interface WebViewDefaultComponent {
    void injectWebViewDefaultFragment(WebViewDefaultFragment fragment);
}