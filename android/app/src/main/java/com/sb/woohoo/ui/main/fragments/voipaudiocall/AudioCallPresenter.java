package com.sb.woohoo.ui.main.fragments.voipaudiocall;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

/**
 * Presenter for {@link AudioCallFragment}.
 * This needs to be updated
 *
 * @author subham satyam.
 */
public class AudioCallPresenter
        extends BasePresenter
        implements AudioCallContracts.Actions {
    private AudioCallContracts.View mView; // View.
    private AudioCallRepository mRepository; // Repository.

    public AudioCallPresenter(AudioCallContracts.View view, AudioCallRepository repository) {
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