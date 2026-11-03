package com.sb.woohoo.ui.main.fragments.alarm.alarmalart;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import javax.inject.Inject;

public class AlarmAlertPresenter extends BasePresenter {

    AlarmAlertFragment mFragment;
    AlarmAlertRepository mRepository;

    @Inject
    public AlarmAlertPresenter(AlarmAlertFragment mFragment, AlarmAlertRepository mRepository) {
        this.mFragment = mFragment;
        this.mRepository = mRepository;
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }
}
