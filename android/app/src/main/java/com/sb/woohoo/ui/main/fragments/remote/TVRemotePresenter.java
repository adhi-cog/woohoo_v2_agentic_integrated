package com.sb.woohoo.ui.main.fragments.remote;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.remote.pojo.TVRemoteChannelData;
import com.sb.woohoo.ui.main.fragments.remote.pojo.TVRemoteChannelRequest;
import com.sb.woohoo.ui.main.fragments.remote.pojo.TVRemoteData;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.List;

import javax.inject.Inject;

public class TVRemotePresenter extends BasePresenter  implements TVRemoteContract.Actions {

    private TVRemoteRepository tVRemoteRepository;
    private TVRemoteFragment mView;

    @Inject
    public TVRemotePresenter(TVRemoteRepository tVRemoteRepository, TVRemoteFragment mView) {
        this.tVRemoteRepository = tVRemoteRepository;
        this.mView = mView;
        tVRemoteRepository.onAttach(this);
    }

    public void makeRemoteAPI() {
        tVRemoteRepository.makeRemoteAPI();
        mView.showProgress();
    }

    public void makeChannelAPI(String actionId) {
        tVRemoteRepository.makeChannelAPI(actionId);
        mView.showProgress();
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        tVRemoteRepository.repositoryCleanUps();
    }

    @Override
    public void onTVRemoteDataFetched(List<TVRemoteData> tVRemoteDataList) {
        mView.onTVRemoteDataFetched(tVRemoteDataList);
        mView.hideProgress();
    }

    @Override
    public void onChannelListFetched(List<TVRemoteChannelData> tVRemoteChannelData) {
        mView.onChannelListFetched(tVRemoteChannelData);
        mView.hideProgress();
    }

    @Override
    public void onTVRemoteMessage(String error) {
        mView.onTVRemoteMessage(error);
        mView.hideProgress();
    }
    @Override
    public void onRemoteComponentResponse() {
        mView.onRemoteComponentResponse();
        mView.hideProgress();
    }
    public void controlTVCommand(String actionId) {
        tVRemoteRepository.controlTVCommand(actionId);
        mView.showProgress();
    }

    public void controlChannelTVCommand(TVRemoteChannelRequest tVRemoteChannelRequest) {
        tVRemoteRepository.controlChannelTVCommand(tVRemoteChannelRequest);
    }
}
