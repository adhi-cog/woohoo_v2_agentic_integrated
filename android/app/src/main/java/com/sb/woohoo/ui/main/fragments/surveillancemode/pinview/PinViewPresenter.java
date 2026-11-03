package com.sb.woohoo.ui.main.fragments.surveillancemode.pinview;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import javax.inject.Inject;

public class PinViewPresenter
        extends BasePresenter
        implements PinViewContracts.Actions {


    private PinViewRepository pinViewRepository;
    private PinViewContracts.View view;

    @Inject
    public PinViewPresenter(PinViewRepository pinViewRepository, PinViewFragment pinViewFragment) {
        this.pinViewRepository = pinViewRepository;
        this.view = pinViewFragment;
        pinViewRepository.onAttach(this);
    }

    void setHubToSurveillanceMode(String pin, boolean mode) {
        pinViewRepository.setHubInSurveillanceMode(pin, mode);
    }

    void updateUserPinForSurveillance(String pin) {
        pinViewRepository.setUserPin(pin);
    }

    @Override
    public void onHubModeChangeFailed() {
        view.onInCorrectPin();
    }

    @Override
    public void onHubModeInSurveillanceSuccess(boolean enabled) {
        view.isHubModeInSurveillance(enabled);
    }

    @Override
    public void onPinUpdateError(String error) {
        view.onError(error);
    }

    @Override
    public void onAPICallError(String errorMessage) {
        view.onError(errorMessage);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        pinViewRepository.repositoryCleanUps();
    }

}
