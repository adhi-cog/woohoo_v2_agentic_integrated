package com.sb.woohoo.ui.main.fragments.reminders;

import android.util.Log;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.reminders.pojo.Reminder;
import com.sb.woohoo.ui.main.fragments.reminders.pojo.ReminderGet;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ReminderRepository extends BaseRepository<ReminderPresenter> {

    private List<Reminder> list;
    private WooHooApiCall client;
    private CompositeDisposable compositeDisposable;
    private SharedPrefsHelper mPrefsHelper;

    @Inject
    public ReminderRepository(WooHooApiCall client, CompositeDisposable compositeDisposable) {
        this.client = client;
        this.compositeDisposable = compositeDisposable;
        mPrefsHelper = WooHooApp.getSharedPrefsHelper();
    }

    void addReminder(final Reminder reminder) {

        compositeDisposable.add(
                client.postReminder(reminder.getReminder(), reminder.getUTCTime(), "description", getDeviceID())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Reminder>() {
                            @Override
                            public void onSuccess(Reminder reminderPost) {
                                reminder.setReminderId(reminderPost.getData().getReminderId());
                                getActions().setAlarm(reminder);
                            }

                            @Override
                            public void onError(Throwable e) {
                                LoggerWrapper.e(ReminderRepository.class.getSimpleName(), e.getMessage());
                            }
                        })
        );
    }

    void getRemindersList(final int reminderAction, final IntentInfo intentInfo) {

        compositeDisposable.add(client.getReminders(getDeviceID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ReminderGet>() {
                    @Override
                    public void onSuccess(ReminderGet reminderGet) {
                        list = reminderGet.getData();
                        getActions().sortReminders(list, reminderAction, intentInfo);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoggerWrapper.e(ReminderRepository.class.getSimpleName(), e.getMessage());
                    }
                })
        );
    }

    void deleteReminder(final String reminderId, final int delete) {

        compositeDisposable.add(
                client.deleteReminder(String.valueOf(reminderId))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {

                            @Override
                            public void onComplete() {
                                if (delete != 100) {
                                    getActions().cancelAlarm(Integer.parseInt(reminderId));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                LoggerWrapper.e(ReminderRepository.class.getSimpleName(), e.getMessage());
                            }
                        })
        );
    }

    void deleteReminders() {

        compositeDisposable.add(
                client.deleteReminders(getDeviceID())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {

                            @Override
                            public void onComplete() {
                                getActions().updateReminderAfterDelete();
                            }

                            @Override
                            public void onError(Throwable e) {
                                LoggerWrapper.e(ReminderRepository.class.getSimpleName(), e.getMessage());
                            }
                        })
        );
    }

    void editReminder(Reminder reminder) {

        compositeDisposable.add(
                client.putReminder(reminder.getReminderId(), reminder.getReminder(), reminder.getUTCTime(), "Description", getDeviceID())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                LoggerWrapper.i(ReminderRepository.class.getSimpleName(), "Reminder Updated");
                            }

                            @Override
                            public void onError(Throwable e) {
                                LoggerWrapper.e(ReminderRepository.class.getSimpleName(), e.getMessage());
                            }
                        })
        );
    }

    private String getDeviceID() {
        return mPrefsHelper == null ? "" : mPrefsHelper.get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
    }
}
