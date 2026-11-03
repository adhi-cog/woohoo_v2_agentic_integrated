package com.sb.woohoo.ui.main.fragments.overview.saudievents;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.overview.saudievents.pojo.DataList;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.List;

import javax.inject.Inject;

public class SaudiTourismEventsPresenter extends BasePresenter implements SaudiTourismEventsContract.Actions {

    private SaudiTourismEventsFragment mView;
    private SaudiTourismEventsRepository saudiTourismEventsRepository;

    @Inject
    public SaudiTourismEventsPresenter(SaudiTourismEventsRepository saudiTourismEventsRepository, SaudiTourismEventsFragment mView) {
        this.saudiTourismEventsRepository = saudiTourismEventsRepository;
        this.mView = mView;
        this.saudiTourismEventsRepository.onAttach(this);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {
    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
    }

    public void onSuccess(List<DataList> categories) {
        mView.onCategoriesResponse(categories);
        mView.hideProgress();
    }

    @Override
    public void errorMessage(int error) {
        mView.onErrorResponse(error);
        mView.hideProgress();
    }

    public void eventsAPIRequest(String language) {
        saudiTourismEventsRepository.eventsAPIRequest(language);
        mView.showProgress();
    }
}

