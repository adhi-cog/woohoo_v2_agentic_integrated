package com.sb.woohoo.ui.main.fragments.feedback;

import static com.sb.woohoo.ui.main.helpers.NetworkHelper.API_FAIL_ERROR_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.NETWORK_SUCCESS_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.EMPTY_RESULT_CODE;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.feedback.pojo.FeedbackSubmitRequest;
import com.sb.woohoo.ui.main.fragments.feedback.pojo.QuestionRequest;
import com.sb.woohoo.ui.main.fragments.feedback.pojo.QuestionSendRequest;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FeedbackRepository extends BaseRepository<FeedbackPresenter> {

    private final String DEVICE_ID = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");
    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;


    @Inject
    public FeedbackRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    public void makeFeedbackAPIRequest() {

        compositeDisposable.add(
                wooHooApiCall.getFeedbackQuestionsRequest(DEVICE_ID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<QuestionRequest>() {
                            @Override
                            public void onSuccess(QuestionRequest response) {
                                onFeedbackResponse(response);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().feedbackMessage(API_FAIL_ERROR_CODE);
                            }
                        }));
    }

    private void onFeedbackResponse(QuestionRequest questionResponse) {
        int status = questionResponse.getStatus();
        if (status == NETWORK_SUCCESS_CODE) {
            if (questionResponse.getData() != null && questionResponse.getData().getQuestion() != null) {
                getActions().feedbackSuccess(questionResponse.getData().getQuestion());
            } else {
                getActions().feedbackMessage(EMPTY_RESULT_CODE);
            }
        } else{
            getActions().feedbackMessage(status);
        }
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
    }

    public void submitFeedback(FeedbackSubmitRequest feedbackSubmitRequest) {

        compositeDisposable.add(
                wooHooApiCall.postFeedbackQuestions(feedbackSubmitRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<QuestionSendRequest>() {
                            @Override
                            public void onSuccess(QuestionSendRequest response) {
                                onFeedbackSaveResponse(response);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().feedbackMessage(API_FAIL_ERROR_CODE);
                            }
                        }));
    }

    private void onFeedbackSaveResponse(QuestionSendRequest questionResponse) {

        int status = questionResponse.getStatus();
        if (status == NETWORK_SUCCESS_CODE) {
                getActions().feedbackSaveSuccess(status);
        } else{
            getActions().feedbackMessage(status);
        }
    }
}
