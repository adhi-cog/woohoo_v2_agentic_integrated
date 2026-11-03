package com.sb.woohoo.ui.main.fragments.dialer;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.StaffMember;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.StaffMembersResponse;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.sb.woohoo.data.prefs.SharedPrefsKey.PREF_KEY_DEVICE_ID;

/**
 * Repository for {@link DialerPresenter}.
 *
 * @author Ritwik Jamuar
 */
public class DialerRepository extends BaseRepository<DialerPresenter> {

    String hubID;
    private WooHooApiCall mApiCall;
    private CompositeDisposable compositeDisposable;


    @Inject
    public DialerRepository(WooHooApiCall mApiCall, CompositeDisposable compositeDisposable) {
        this.mApiCall = mApiCall;
        this.compositeDisposable = compositeDisposable;
        hubID = WooHooApp.getSharedPrefsHelper().get(PREF_KEY_DEVICE_ID, null);
    }

    /**
     * Calls the API to fetch Family Members.
     */
    void fetchHotelContacts() {
        compositeDisposable.add(
                mApiCall.getAllHotelStaffMembers(WooHooApp.getSharedPrefsHelper().get(PREF_KEY_DEVICE_ID, ""))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<StaffMembersResponse>() {
                            @Override
                            public void onSuccess(StaffMembersResponse staffMembersResponse) {

                                switch (staffMembersResponse.getStatus()) {
                                    case 200: {
                                        onStaffMembersFetched(staffMembersResponse);
                                    }
                                    break;
                                    case 401: {
                                    }
                                    break;
                                    case 502: {
                                        getActions().onAPICallError("Bad Gateway");
                                    }
                                    break;
                                    default: {
                                        getActions().onAPICallError(staffMembersResponse.getError());
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                            }
                        }));
    }

    /**
     * Handles the successful response of API call, fetched as {@link StaffMembersResponse}.
     *
     * @param staffMembersResponse {@link StaffMembersResponse}.
     */
    private void onStaffMembersFetched(StaffMembersResponse staffMembersResponse) {
        if (staffMembersResponse == null) {
            getActions().onAPICallError("PostResponse Corrupted");
        } else {
            if (staffMembersResponse.getStatus() == 200) {
                List<StaffMember> members = staffMembersResponse.getData();
                if (members == null || members.size() == 0) {
                    getActions().onStaffMembersNotFound();
                } else {
                    getActions().onStaffMembersFetched(members);
                }
            } else {
                getActions().onAPICallError(staffMembersResponse.getMessage());
            }
        }
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
    }
}