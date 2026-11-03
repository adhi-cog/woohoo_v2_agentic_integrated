package com.sb.woohoo.ui.main.fragments.timer.alert;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import javax.inject.Inject;

public class TimerAlertPresenter extends BasePresenter {

    private TimerAlertContract.View timerAlertView;

    @Inject
    public TimerAlertPresenter(TimerAlertFragment timerAlertFragment) {
        this.timerAlertView = timerAlertFragment;
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }
}
