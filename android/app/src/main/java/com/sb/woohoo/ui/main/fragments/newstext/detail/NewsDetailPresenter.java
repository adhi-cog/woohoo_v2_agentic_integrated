package com.sb.woohoo.ui.main.fragments.newstext.detail;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import javax.inject.Inject;

public class NewsDetailPresenter extends BasePresenter implements NewsDetailContract.Actions {

    private NewsDetailFragment mView;
    private NewsDetailRepository mRepository;

    @Inject
    public NewsDetailPresenter(NewsDetailFragment mView, NewsDetailRepository mRepository) {
        this.mView = mView;
        this.mRepository = mRepository;
        mRepository.onAttach(this);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }
}
