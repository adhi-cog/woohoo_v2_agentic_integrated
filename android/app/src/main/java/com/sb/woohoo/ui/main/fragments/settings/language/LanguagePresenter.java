package com.sb.woohoo.ui.main.fragments.settings.language;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.settings.language.pojo.LanguageData;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.ArrayList;

import javax.inject.Inject;

public class LanguagePresenter
        extends BasePresenter
        implements LanguageContracts.Actions{

    private LanguageContracts.Views views;
    private LanguageRepository languageRepository;

    @Inject
    public LanguagePresenter(LanguageChangeFragment views, LanguageRepository languageRepository) {
        this.views = views;
        this.languageRepository = languageRepository;
        this.languageRepository.onAttach(this);
    }


    @Override
    public void getAllLanguage() {
        languageRepository.getAllLocale();
    }

    @Override
    public void onAllLocaleFetchSucess(ArrayList<LanguageData> data) {
        views.onAllLocaleFetch(data);
    }

    @Override
    public void onError(int errorCode) {
        views.onError(errorCode);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        languageRepository.repositoryCleanUps();
    }
}
