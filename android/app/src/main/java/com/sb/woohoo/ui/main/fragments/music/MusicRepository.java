package com.sb.woohoo.ui.main.fragments.music;

import android.util.Log;

import com.sb.woohoo.R;
import com.sb.woohoo.custom.LoggerWrapper;
import com.google.gson.Gson;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.music.pojo.Parameter;
import com.sb.woohoo.ui.main.fragments.music.pojo.Query;
import com.sb.woohoo.ui.main.fragments.music.pojo.song.Data;
import com.sb.woohoo.ui.main.fragments.music.pojo.song.Song;
import com.sb.woohoo.ui.main.fragments.music.pojo.token.AccessToken;
import com.sb.woohoo.ui.onboarding.login.LoginModel;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

/*** Repository for {@link MusicRepository}.
 * @author Ritwik Jamuar.*/
public class MusicRepository extends BaseRepository<MusicPresenter> {
    private WooHooApiCall mApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    MusicRepository(WooHooApiCall mApiCall, CompositeDisposable compositeDisposable) {
        this.mApiCall = mApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    void makeApiRequest(final String request, String type) {
        compositeDisposable.add(
                mApiCall.getSongs(buildSongSearchParameter(request, type))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Song>() {
                            @Override
                            public void onSuccess(Song song) {
                                onSongFetchSuccess(song);
                            }

                            @Override
                            public void onError(Throwable e) {
                                handleRxError(e);
                            }
                        })
        );
    }

    private void handleRxError(Throwable e) {
        if (e instanceof HttpException) {
            ResponseBody body = ((HttpException) e).response().errorBody();
            int code = ((HttpException) e).code();
            //If 502 is not handled then you will get JsonSyntaxException while fetching LoginModel
            //Because 502's response is HTML and others have LoginModel type of Response...
            if (code == 502) {
                getActions().onError(R.string.server_is_down);
            } else if (code == 403) {
                getActions().onAccountNotLinked();
            } else {
                if (body != null) {
                    LoginModel loginModel = null;
                    try {
                        loginModel = new Gson().fromJson(body.string(), LoginModel.class);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    if (loginModel != null) {
                        getActions().onError(loginModel.getError());
                    }
                }
            }
        }
    }

    void searchTopCharts() {
        compositeDisposable.add(
                mApiCall.getSongs()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Song>() {
                            @Override
                            public void onSuccess(Song song) {
                                onSongFetchSuccess(song);
                            }

                            @Override
                            public void onError(Throwable e) {
                                handleRxError(e);
                            }
                        })
        );
    }

    void getToken() {
        compositeDisposable.add(
                mApiCall.getToken()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<AccessToken>() {
                            @Override
                            public void onSuccess(AccessToken accessToken) {
                                onTokenSuccess(accessToken);
                            }

                            @Override
                            public void onError(Throwable e) {
                                handleRxError(e);
                            }
                        }));
    }

    private Parameter buildSongSearchParameter(String request, String type) {
        Query query = new Query();
        query.setQ(request);
        query.setType(type);

        Parameter parameter = new Parameter();
        parameter.setQuery(query);
        return parameter;
    }

    private void onSongFetchSuccess(Song song) {
        if (song != null) {
            int code = song.getStatus();
            switch (code) {
                case 200: {
                    Data data = song.getData();
                    if (data != null) {
                        String uri = data.getUri();
                        getActions().onQueryResult(uri);
                    } else {
                        LoggerWrapper.d(MusicRepository.class.getSimpleName(), "No songs found");
                        getActions().onNoResult();
                    }
                }
                break;

                case 403: {
                    getActions().onAccountNotLinked();
                    getActions().onError(R.string.spotify_account_not_linked);
                }
                break;

                case 401: {
                    LoggerWrapper.e(MusicRepository.class.getSimpleName(), "Token Expired");
                    getToken();
                    getActions().onError(R.string.token_expired);
                    //getActions ().onTokenExpired ();
                    //TODO Refresh Token
                    //TODO Search with previous query
                }
                break;

                default: {
                    LoggerWrapper.e(MusicRepository.class.getSimpleName(), "Server Error!");
                    getActions().onError(R.string.server_error);
                }
            }
        } else {
            LoggerWrapper.e(MusicRepository.class.getSimpleName(), "Spotify Model Null");
            getActions().onNoResult();
        }
    }

    private void onTokenSuccess(AccessToken accessToken) {
        if (accessToken != null) {
            switch (accessToken.getStatus()) {
                case 200: {
                    String token = accessToken.getData().getAccessToken();
                    getActions().onTokenSuccess(token);
                }
                break;

                case 403: {
                    getActions().onAccountNotLinked();
                }
                break;

                case 401: {
                    getActions().onError(R.string.error_fetching_spotify_token);
                    LoggerWrapper.e(MusicRepository.class.getSimpleName(), "Token Expired");
                }
                break;

                default: {
                    LoggerWrapper.e(MusicRepository.class.getSimpleName(), "Server Error! = " + accessToken.getStatus());
                }
            }
        } else {
            getActions().onError(R.string.server_not_available);
        }
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
    }
}