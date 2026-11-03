package com.sb.woohoo.ui.main.fragments.buysim.qrscan;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import javax.inject.Inject;

public class QRScanPresenter extends BasePresenter{

    private QRScanRepository qRScanRepository;
    private QRScanFragment mView;

    @Inject
    public QRScanPresenter(QRScanRepository qRScanRepository, QRScanFragment mView) {
        this.qRScanRepository = qRScanRepository;
        this.mView = mView;
        qRScanRepository.onAttach(this);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        qRScanRepository.repositoryCleanUps();
    }
}
