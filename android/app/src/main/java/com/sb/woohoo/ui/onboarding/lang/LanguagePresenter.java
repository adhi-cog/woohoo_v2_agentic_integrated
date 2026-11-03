package com.sb.woohoo.ui.onboarding.lang;

import java.util.List;

/**
 * Created by admin on 1/20/18.
 */

public class LanguagePresenter implements LanguageContract.Actions {

    private LanguageContract.View mView;

    public LanguagePresenter(LanguageContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void loadLanguages() {
        List<String> languageList = LanguageRepository.getLanguages();
        if (!languageList.isEmpty()) {
            mView.showLanguages(languageList);
            mView.hideEmptyText();
        } else {
            mView.showEmptyText();
        }
    }

    @Override
    public void loadLanguagesError(String msg) {
        mView.showMessage(msg);
    }
}
