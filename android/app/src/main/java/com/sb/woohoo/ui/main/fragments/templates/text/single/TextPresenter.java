package com.sb.woohoo.ui.main.fragments.templates.text.single;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import javax.inject.Inject;

public class TextPresenter extends BasePresenter implements TextContract.Actions {

    TextFragment mFragment;
    TextRepository mRepository;

    @Inject
    public TextPresenter(TextFragment mFragment, TextRepository mRepository) {

        this.mFragment = mFragment;
        this.mRepository = mRepository;
        mRepository.onAttach(this);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }
}
