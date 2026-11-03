package com.sb.woohoo.ui.main.fragments.covid;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.covid.pojo.CovidSubmitRequest;
import com.sb.woohoo.ui.main.fragments.covid.pojo.QuestionRequest;
import com.sb.woohoo.ui.main.fragments.covid.pojo.QuestionSendRequest;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CovidRepository extends BaseRepository<CovidPresenter> {

    private final String DEVICE_ID = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");
    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    public CovidRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }
    public void makeCovidAPIRequest() {

        compositeDisposable.add(
                wooHooApiCall.getCovidQuestionsRequest(DEVICE_ID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<QuestionRequest>() {
                            @Override
                            public void onSuccess(QuestionRequest covidResponse) {
                                onCovidResponse(covidResponse);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().covidMessage(e.getMessage());
                            }
                        }));
    }

    private void onCovidResponse(QuestionRequest questionResponse) {
        int status = questionResponse.getStatus();
        if (status == 200) {
            if (!questionResponse.getData().getQuestion().isEmpty()) {
                getActions().covidSuccess(questionResponse.getData().getQuestion());
            } else {
                getActions().covidMessage(WooHooApp.getAppContext().getString(R.string.no_result_fetched));
            }
        } else if (status == 502) {
            getActions().covidMessage(WooHooApp.getAppContext().getString(R.string.bad_gateway));
        }
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
    }

    public void submitCovid(CovidSubmitRequest covidSubmitRequest) {

        compositeDisposable.add(
                wooHooApiCall.postCovidQuestions(covidSubmitRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<QuestionSendRequest>() {
                            @Override
                            public void onSuccess(QuestionSendRequest covidResponse) {
                                onCovidSaveResponse(covidResponse);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().covidMessage(e.getMessage());
                            }
                        }));
    }
    private void onCovidSaveResponse(QuestionSendRequest questionResponse) {

        int status = questionResponse.getStatus();
        if (status == 200) {
                getActions().covidSaveSuccess(WooHooApp.getAppContext().getString(R.string.sucessful_submit));
        } else{
            getActions().covidMessage(WooHooApp.getAppContext().getString(R.string.bad_gateway));
        }
    }
}
