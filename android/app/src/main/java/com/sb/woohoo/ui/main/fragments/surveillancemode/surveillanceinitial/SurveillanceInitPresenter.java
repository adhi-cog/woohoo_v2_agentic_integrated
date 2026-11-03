package com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceinitial;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.surveillancemode.pojo.UserData;
import com.sb.woohoo.ui.main.fragments.surveillancemode.pojo.UserDetailModel;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import javax.inject.Inject;

public class SurveillanceInitPresenter
        extends BasePresenter
        implements SurveillanceInitContract.Actions {

    private SurveillanceInitRepository repository;
    private SurveillanceInitContract.Views views;

    @Inject
    public SurveillanceInitPresenter(SurveillanceInitialFragment surveillanceInitialFragment, SurveillanceInitRepository repository) {
        this.repository = repository;
        this.views = surveillanceInitialFragment;
        repository.onAttach(this);
    }

    @Override
    public void verifyIfUserPinExists() {
//        repository.verifyUserPinExists();
    }

    void onUserDetailsFetched(UserDetailModel userDetailModel) {
        UserData userData = userDetailModel.getUserData();
        views.onUserPinExists(userData.getPinAvailable());
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }
}
