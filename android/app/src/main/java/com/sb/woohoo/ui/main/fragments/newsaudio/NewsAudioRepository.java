package com.sb.woohoo.ui.main.fragments.newsaudio;

import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.newsaudio.pojo.NewsAudioResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NewsAudioRepository extends BaseRepository<NewsAudioPresenter> {
    private WooHooApiCall mApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    NewsAudioRepository(WooHooApiCall apiCall, CompositeDisposable compositeDisposable) {
        this.mApiCall = apiCall;
        this.compositeDisposable = compositeDisposable;
    }

    public void requestNewsBroadcastAudio() {
        compositeDisposable.add(
                mApiCall.getNewsBroadcastAudio()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<NewsAudioResponse>() {
                            @Override
                            public void onSuccess(NewsAudioResponse newsAudioResponse) {
                                onNewsAudioResponse(newsAudioResponse);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPIError(e.getMessage());
                            }
                        }));
    }

    private void onNewsAudioResponse(NewsAudioResponse serverResponse) {
        if (serverResponse == null) {
            getActions().onAPIError("Server Response Corrupted");
        } else {
            if (serverResponse.getStatus() == 200) {
                getActions().onNewsAudioSourceFound(serverResponse.getData());
            } else {
                getActions().onAPIError(serverResponse.getMessage());
            }
        }
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
    }
}