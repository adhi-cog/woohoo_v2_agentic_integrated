package com.sb.woohoo.ui.main.fragments.webview;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

/**
 * Presenter for {@link WebViewFragment}.
 * This needs to be updated
 *
 * @author subham satyam.
 */
public class WebViewPresenter
        extends BasePresenter
        implements WebViewContracts.Actions {
    private WebViewContracts.View mView; // View.
    private WebViewRepository mRepository; // Repository.

    public WebViewPresenter(WebViewContracts.View view, WebViewRepository repository) {
        this.mView = view;
        this.mRepository = repository;
        repository.onAttach(this);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
    }


}