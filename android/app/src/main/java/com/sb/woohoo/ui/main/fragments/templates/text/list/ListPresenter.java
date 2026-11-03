package com.sb.woohoo.ui.main.fragments.templates.text.list;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import javax.inject.Inject;

public class ListPresenter extends BasePresenter implements ListContract.Actions {

    ListFragment mView;
    ListRepository mRepository;

    @Inject
    ListPresenter(ListFragment mView, ListRepository mRepository) {
        this.mView = mView;
        this.mRepository = mRepository;
        mRepository.onAttach(this);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }
}
