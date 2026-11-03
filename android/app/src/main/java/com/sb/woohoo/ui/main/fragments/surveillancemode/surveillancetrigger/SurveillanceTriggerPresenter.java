package com.sb.woohoo.ui.main.fragments.surveillancemode.surveillancetrigger;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import javax.inject.Inject;

public class SurveillanceTriggerPresenter
        extends BasePresenter
        implements SurveillanceTriggerContract.Actions {

    private SurveillanceTriggerContract.Views views;
    private SurveillanceTriggerRepository surveillanceTriggerRepository;

    @Inject
    public SurveillanceTriggerPresenter(SurveillanceTriggerFragment surveillanceTriggerFragment,
                                        SurveillanceTriggerRepository surveillanceTriggerRepository) {
        this.views = surveillanceTriggerFragment;
        this.surveillanceTriggerRepository = surveillanceTriggerRepository;
        surveillanceTriggerRepository.onAttach(this);
    }

    @Override
    public void cancelSurveillanceMode(String mPin) {
        surveillanceTriggerRepository.setHubInSurveillanceMode(mPin, false);
    }

    void onHubModeInSurveillanceSuccess(boolean surveillanceMode) {
        if (!surveillanceMode) {
            views.cancelSurveillanceMode();
        }
    }

    public void onError(String errorMessage) {
        views.onError(errorMessage);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
    }
}
