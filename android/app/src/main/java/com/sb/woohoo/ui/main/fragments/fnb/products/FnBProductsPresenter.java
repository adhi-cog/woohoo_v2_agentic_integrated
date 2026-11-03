package com.sb.woohoo.ui.main.fragments.fnb.products;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.fnb.categories.FnBCategoriesContract;
import com.sb.woohoo.ui.main.fragments.fnb.categories.FnBCategoriesFragment;
import com.sb.woohoo.ui.main.fragments.fnb.categories.FnBCategoriesRepository;
import com.sb.woohoo.ui.main.fragments.fnb.categories.pojo.Banner;
import com.sb.woohoo.ui.main.fragments.fnb.categories.pojo.FnBCategoriesResponse;
import com.sb.woohoo.ui.main.fragments.fnb.products.pojo.FnBProductsResponse;
import com.sb.woohoo.ui.main.fragments.fnb.products.pojo.Product;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.List;

import javax.inject.Inject;

public class FnBProductsPresenter extends BasePresenter implements FnBProductsContract.Actions {

private FnBProductsRepository fnBProductsRepository;
private FnBProductsFragment mView;

    @Inject
    public FnBProductsPresenter(FnBProductsRepository fnBProductsRepository, FnBProductsFragment mView) {
        this.fnBProductsRepository = fnBProductsRepository;
        this.mView = mView;
        fnBProductsRepository.onAttach(this);
    }

    public void makeProductsRequestFromCategoryId(String id){
        fnBProductsRepository.makeProductsRequest(id, false);
        mView.showProgress();
    }

    public void makeProductsRequestFromSubCategoryId(String id){
        fnBProductsRepository.makeProductsRequest(id, true);
        mView.showProgress();
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void onSuccess(List<Product> productsList, List<Banner> bannerList) {
        mView.onSuccess(productsList, bannerList);
        mView.hideProgress();
    }


    @Override
    public void errorMessage(int error) {
        mView.errorMessage(error);
        mView.hideProgress();
    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        fnBProductsRepository.repositoryCleanUps();
    }
}

