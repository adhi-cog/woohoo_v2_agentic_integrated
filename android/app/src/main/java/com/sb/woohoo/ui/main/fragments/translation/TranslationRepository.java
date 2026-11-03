package com.sb.woohoo.ui.main.fragments.translation;

import android.util.Log;

import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.pojo.PostQuery;
import com.sb.woohoo.ui.main.pojo.translate.PostTranslateQuery;
import com.sb.woohoo.ui.main.pojo.translate.TranslateResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;

/**
 * Repository for {@link TranslationPresenter}.
 * This needs to be updated
 *
 * @author subham satyam.
 */
public class TranslationRepository extends BaseRepository<TranslationPresenter> {

    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    public TranslationRepository(WooHooApiCall wooHooApiCall,
                                 CompositeDisposable compositeDisposable
    ) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    public void callTranslateApi(String source, String target, String inputValue) {

        PostTranslateQuery postQuery = new PostTranslateQuery();
        postQuery.setSource(source);
        postQuery.setTarget(target);
        postQuery.setText(inputValue);

        compositeDisposable.add(
                wooHooApiCall.translate(postQuery)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<TranslateResponse>() {

                            @Override
                            public void onSuccess(TranslateResponse translateResponse) {

                                getActions().onTranslateResult(translateResponse);
//                                Log.e(MainActivity.class.getSimpleName(), "response : " + translateResponse);
                            }

                            @Override
                            public void onError(Throwable e) {

                                if (e instanceof HttpException) {
                                    HttpException exception = (HttpException) e;
                                    int errorCode = exception.code();
                                    Log.e(MainActivity.class.getSimpleName(), "ERROR CODE : " + errorCode);
                                } else {
                                    Log.e(MainActivity.class.getSimpleName(), e.getMessage());
                                }
                            }
                        })
        );
    }
}