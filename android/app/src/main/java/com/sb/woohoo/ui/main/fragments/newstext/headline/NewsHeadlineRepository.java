package com.sb.woohoo.ui.main.fragments.newstext.headline;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.newstext.headline.pojo.NewsHeadline;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NewsHeadlineRepository extends BaseRepository<NewsHeadlinePresenter> {

    private final String LOCATION_CODE = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_COUNTRY_CODE, "US");
    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    public NewsHeadlineRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    public void makeGeneralApiRequest(String query) {
        String NEWS_QUERY = query.replace(" ", "-");

        compositeDisposable.add(
                wooHooApiCall.getNewsQueryApiRequest(NEWS_QUERY)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<NewsHeadline>() {
                            @Override
                            public void onSuccess(NewsHeadline newsHeadline) {
                                onNewsResponse(newsHeadline);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().newsHeadlineError(e.getMessage());
                            }
                        }));
    }

    public void makeLocationApiRequest() {

        compositeDisposable.add(
                wooHooApiCall.getNewsLocationApiRequest(LOCATION_CODE)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<NewsHeadline>() {
                            @Override
                            public void onSuccess(NewsHeadline newsHeadline) {
                                onNewsResponse(newsHeadline);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().newsHeadlineError(e.getMessage());
                            }
                        }));
    }

    private void onNewsResponse(NewsHeadline newsHeadline) {
        int status = newsHeadline.getStatus();
        if (status == 200) {
            if (!newsHeadline.getData().getArticles().isEmpty()) {
                getActions().newsHeadlineSuccess(newsHeadline);
            } else {
                getActions().newsHeadlineError("No result fetched");
            }
        } else if (status == 502) {
            getActions().newsHeadlineError("Bad Gateway");
        }
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
    }
}
