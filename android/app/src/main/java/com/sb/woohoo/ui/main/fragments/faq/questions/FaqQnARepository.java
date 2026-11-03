package com.sb.woohoo.ui.main.fragments.faq.questions;

import static com.sb.woohoo.ui.main.helpers.NetworkHelper.API_FAIL_ERROR_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.EMPTY_RESULT_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.NETWORK_SUCCESS_CODE;

import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.faq.questions.pojo.FaqQuestionsResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FaqQnARepository extends BaseRepository<FaqQnAPresenter> {

    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    public FaqQnARepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    public void getFaqQuestions(String categoryId) {

        compositeDisposable.add(
                wooHooApiCall.getAllQuestionsOfCategory(categoryId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<FaqQuestionsResponse>() {
                            @Override
                            public void onSuccess(FaqQuestionsResponse response) {
                                try {
                                    onQuestionsResponse(response);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onErrorResponse(API_FAIL_ERROR_CODE);
                            }
                        }));
    }

    private void onQuestionsResponse(FaqQuestionsResponse response) {
        int status = response.getStatus();
        if (status == NETWORK_SUCCESS_CODE) {
            if (!response.getData().isEmpty()) {
                getActions().onQuestionsResponse(response.getData());
            } else {
                getActions().onErrorResponse(EMPTY_RESULT_CODE);
            }
        } else {
            getActions().onErrorResponse(status);
        }
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
    }
}
