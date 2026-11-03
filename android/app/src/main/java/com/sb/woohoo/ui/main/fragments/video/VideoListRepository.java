package com.sb.woohoo.ui.main.fragments.video;

import android.util.Log;

import com.sb.woohoo.R;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.video.pojo.VideoResponse;
import com.sb.woohoo.ui.main.fragments.video.pojo.VideoTrendingResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class VideoListRepository extends BaseRepository<VideoListPresenter> {

    private WooHooApiCall client;
    private CompositeDisposable compositeDisposable;

    @Inject
    public VideoListRepository(WooHooApiCall client, CompositeDisposable compositeDisposable) {
        this.client = client;
        this.compositeDisposable = compositeDisposable;
    }

    void getMediaRequest(String query, final int request) {

        compositeDisposable.add(
                client.getMediaList(query)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<VideoResponse>() {
                            @Override
                            public void onSuccess(VideoResponse videoResponse) {
                                if (request == VideoListPresenter.REQUEST_ALL) {
                                    getActions().displayList(videoResponse);
                                } else if (request == VideoListPresenter.REQUEST_FIRST) {
                                    if (videoResponse.getData() != null && !videoResponse.getData().isEmpty()) {
                                        getActions().displayList(videoResponse);
                                        getActions().playFirstVideo(videoResponse.getData().get(0));
                                    } else {
                                        getActions().onAPIError(R.string.error_no_videos_listed);
                                    }
                                } else {
                                    getActions().onAPIError(R.string.unknown_category);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                LoggerWrapper.e("VideoSearch", e.getMessage());
                                getActions().onAPIError(e.getMessage());
                            }
                        })
        );
    }

    void getTrendingVideo(final int request) {

        compositeDisposable.add(
                client.getTrendingList("trending", WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_COUNTRY_CODE, "US"))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<VideoTrendingResponse>() {
                            @Override
                            public void onSuccess(VideoTrendingResponse videoTrendingResponse) {
                                if (request == VideoListPresenter.REQUEST_ALL)
                                    getActions().displayList(videoTrendingResponse);
                                else if (request == VideoListPresenter.REQUEST_FIRST) {
                                    if (videoTrendingResponse.getData() != null
                                            && videoTrendingResponse.getData().getItems() != null
                                            && !videoTrendingResponse.getData().getItems().isEmpty()) {
                                        getActions().displayList(videoTrendingResponse);
                                        getActions().playFirstVideo(videoTrendingResponse.getData().getItems().get(0));
                                    } else {
                                        getActions().onAPIError(R.string.error_no_videos_listed);
                                    }
                                }else{
                                    getActions().onAPIError(R.string.unknown_category);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                LoggerWrapper.e("VideoSearch", e.getMessage());
                                getActions().onAPIError(e.getMessage());
                            }
                        })
        );
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
    }
}
