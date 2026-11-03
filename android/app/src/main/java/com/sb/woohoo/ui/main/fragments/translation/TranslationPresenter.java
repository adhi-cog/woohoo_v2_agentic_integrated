package com.sb.woohoo.ui.main.fragments.translation;

import android.content.Context;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.Interactors.HelperInteractor;
import com.sb.woohoo.ui.main.Interactors.listener.PresenterListener;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.pojo.translate.TranslateResponse;

import javax.inject.Inject;

/**
 * Presenter for {@link TranslationFragment}.
 * This needs to be updated
 *
 * @author subham satyam.
 */
public class TranslationPresenter
        extends BasePresenter
        implements TranslationContracts.Actions {
    private TranslationContracts.View mView; // View.
    private TranslationRepository mRepository; // Repository.

    @Inject
    public TranslationPresenter(TranslationContracts.View view,
                                TranslationRepository repository) {
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


    public void setTranslate(String source, String target, String inputValue) {

        mRepository.callTranslateApi(source,target,inputValue);
    }

    @Override
    public void onTranslateResult(TranslateResponse translateResponse) {

        mView.onTraslateResult(translateResponse);

    }
}