package com.sb.woohoo.ui.main.fragments.qrcode;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.qrcode.pojo.QRCodeResponse;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import javax.inject.Inject;

public class QRCodePresenter extends BasePresenter implements QRCodeContract.Actions {

    private QRCodeRepository qrCodeRepository;
    private QRCodeContract.Views mView;

    @Inject
    public QRCodePresenter(QRCodeRepository qrCodeRepository, QRCodeContract.Views mView) {
        this.qrCodeRepository = qrCodeRepository;
        this.mView = mView;
        this.qrCodeRepository.onAttach(this);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    public void qrCodeSuccess(QRCodeResponse response) {
        mView.hideProgress();
        mView.qrCodeResult(response);
    }

    @Override
    public void qrCodeMessage(String error) {
        mView.hideProgress();
    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        mView.hideProgress();
        qrCodeRepository.repositoryCleanUps();
    }

    public void qrCodeAPIRequest() {
        qrCodeRepository.makeQRCodeAPIRequest();
        mView.showProgress();
    }

}
