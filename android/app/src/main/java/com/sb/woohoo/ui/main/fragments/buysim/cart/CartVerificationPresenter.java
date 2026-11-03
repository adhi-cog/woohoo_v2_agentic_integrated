package com.sb.woohoo.ui.main.fragments.buysim.cart;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.buysim.buypackage.pojo.GetEachPackageDetails;
import com.sb.woohoo.ui.main.fragments.buysim.cart.pojo.RedirectUrl;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import javax.inject.Inject;

public class CartVerificationPresenter extends BasePresenter  implements CartVerificationContract.Actions {

    private CartVerificationRepository cartVerificationRepository;
    private CartVerificationFragment mView;

    @Inject
    public CartVerificationPresenter(CartVerificationRepository cartVerificationRepository, CartVerificationFragment mView) {
        this.cartVerificationRepository = cartVerificationRepository;
        this.mView = mView;
        cartVerificationRepository.onAttach(this);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        cartVerificationRepository.repositoryCleanUps();
    }

    @Override
    public void onSuccess(RedirectUrl data) {
        mView.onSuccess(data);
        mView.hideProgress();
    }

    @Override
    public void errorMessage(int emptyResultCode) {
        mView.errorMessage(emptyResultCode);
        mView.hideProgress();
    }

    @Override
    public void onErrorResponse(int apiFailErrorCode) {
        mView.onErrorResponse(apiFailErrorCode);
        mView.hideProgress();
    }

    public void callBuyPackageApi(String email, GetEachPackageDetails packageId, GetEachPackageDetails id) {
        cartVerificationRepository.callBuyPackageApi(email,packageId,packageId);
        mView.showProgress();
    }
}
