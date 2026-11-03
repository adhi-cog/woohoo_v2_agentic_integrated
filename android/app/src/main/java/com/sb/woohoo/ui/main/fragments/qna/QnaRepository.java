package com.sb.woohoo.ui.main.fragments.qna;

import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.qna.pojo.WolframData;
import com.sb.woohoo.ui.main.fragments.qna.pojo.WolframResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class QnaRepository extends BaseRepository<QnaPresenter> {
    private WooHooApiCall mApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    QnaRepository(WooHooApiCall mApiCall, CompositeDisposable compositeDisposable) {
        this.mApiCall = mApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    public void askQuestionToWolframAlpha(String query) {
        compositeDisposable.add(
                mApiCall.requestQNA(query)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<WolframResponse>() {
                            @Override
                            public void onSuccess(WolframResponse wolframAlphaResponse) {
                                onWolframResponse(wolframAlphaResponse);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onWolframAlphaError(e.getMessage());
                            }
                        }));
    }

    private void onWolframResponse(WolframResponse wolframAlphaResponse) {
        int status = wolframAlphaResponse.getStatus();
        switch (status) {
            case 200: {
                WolframData data = wolframAlphaResponse.getData();
                if (data != null) {
                    getActions().onWolframAlphaSuccess(data);
                } else {
                    getActions().onWolframAlphaFailure();
                }
            }
            break;

            case 501: {
                getActions().onWolframAlphaError("501 : Bad Gateway");
            }
            break;

            case 502: {
                getActions().onWolframAlphaError("502 : Bad Gateway");
            }
            break;

            default: {
                getActions().onWolframAlphaError(wolframAlphaResponse.getMessage());
            }
        }
    }
}