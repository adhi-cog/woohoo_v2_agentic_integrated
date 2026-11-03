package com.sb.woohoo.ui.main.fragments.alarm.alarm;

import android.util.Log;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.fragments.alarm.pojo.Alarm;
import com.sb.woohoo.ui.main.fragments.alarm.pojo.GetAlarm;
import com.sb.woohoo.ui.main.fragments.alarm.pojo.PostAlarm;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AlarmRepository extends BaseRepository<AlarmPresenter> {

    private final String TAG = AlarmRepository.class.getSimpleName();
    private WooHooApiCall mApiCall;
    private List<Alarm> list;
    private CompositeDisposable compositeDisposable;
    private SharedPrefsHelper mPrefsHelper;

    @Inject
    public AlarmRepository(WooHooApiCall client, CompositeDisposable compositeDisposable) {
        this.mApiCall = client;
        this.compositeDisposable = compositeDisposable;
        mPrefsHelper = WooHooApp.getSharedPrefsHelper();
    }

    public void getAlarms(final int action, final Alarm alarm) {
        compositeDisposable.add(
                mApiCall.getAlarms(getDeviceID())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<GetAlarm>() {
                            @Override
                            public void onSuccess(@NonNull GetAlarm getAlarm) {
                                list = getAlarm.getData();
                                if (action == Categories.Skills.Alarm.ALARM_SHOW) {
                                    getActions().showAlarms(list);
                                } else if (action == Categories.Skills.Alarm.ALARM_ADD) {
                                    getActions().formatAlarm(list, alarm);
                                } else if (action == Categories.Skills.Alarm.ALARM_DELETE_ORDINAL) {
                                    getActions().deleteAlarmFromList(list);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                //((HttpException) e).code();
                                LoggerWrapper.e(TAG, e.getMessage());
                            }
                        }));
    }

    public void addAlarm(final Alarm alarm) {
        compositeDisposable.add(
                mApiCall.postAlarm(getDeviceID(), alarm.getUTCTime(), alarm.isEveryday())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<PostAlarm>() {
                            @Override
                            public void onSuccess(@NonNull PostAlarm postAlarm) {
                                alarm.setAlarmID(postAlarm.getData().getAlarmID());
                                getActions().onAlarmAdded(alarm);
                            }

                            @Override
                            public void onError(Throwable e) {
                                LoggerWrapper.e(TAG, e.getMessage());
                            }
                        }));
    }

    public void deleteAlarm(final Alarm alarm, final boolean isDelete, final int index) {
        compositeDisposable.add(
                mApiCall.deleteAlarm(alarm.getAlarmID())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                if (isDelete) {
                                    getActions().onAlarmDeleted(alarm, index);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                LoggerWrapper.e(TAG, e.getMessage());
                            }
                        }));
    }

    public void deleteAllAlarm() {

        compositeDisposable.add(
                mApiCall.deleteAllAlarms(getDeviceID())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                getActions().deletedAlarms();
                            }

                            @Override
                            public void onError(Throwable e) {
                                LoggerWrapper.e(TAG, e.getMessage());
                            }
                        }));
    }

    public void updateAlarm(final Alarm alarm) {
        compositeDisposable.add(
                mApiCall.updateAlarm(alarm.getAlarmID(), alarm.getUTCTime(), alarm.isEveryday(), getDeviceID())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {

                            @Override
                            public void onComplete() {
                                getActions().updatedAlarm(alarm);
                            }

                            @Override
                            public void onError(Throwable e) {
                                LoggerWrapper.e(TAG, e.getMessage());
                            }
                        }));
    }

    private String getDeviceID() {
//        return "d0:f8:8c:78:08:4e";
        return mPrefsHelper == null ? "" : mPrefsHelper.get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
    }
}
