package com.sb.woohoo.ui.main.fragments.webviewdefault;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

/**
 * Presenter for {@link WebViewDefaultFragment}.
 * This needs to be updated
 *
 */
public class WebViewDefaultPresenter
        extends BasePresenter
        implements WebViewDefaultContracts.Actions {
    private WebViewDefaultContracts.View mView; // View.
    private WebViewDefaultRepository mRepository; // Repository.

    public WebViewDefaultPresenter(WebViewDefaultContracts.View view, WebViewDefaultRepository repository) {
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