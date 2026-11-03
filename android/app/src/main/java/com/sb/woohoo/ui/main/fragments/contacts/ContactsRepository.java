package com.sb.woohoo.ui.main.fragments.contacts;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.ContactsData;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.ContactsResponse;
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
 * API Repository for {@link ContactsPresenter}.
 *
 * @author Ritwik Jamuar.
 */
public class ContactsRepository extends BaseRepository<ContactsPresenter> {
    private WooHooApiCall mApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    ContactsRepository(WooHooApiCall mApiCall, CompositeDisposable compositeDisposable) {
        this.mApiCall = mApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    /*-------------------------------------- Methods to fetch Family Members -----------------------------------*/

    /**
     * Calls the API to fetch Family Members.
     */
    void fetchFamilyContacts() {
        compositeDisposable.add(
                mApiCall.getAllHotelStaffMembers(WooHooApp.getSharedPrefsHelper().get(PREF_KEY_DEVICE_ID, ""))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<StaffMembersResponse>() {
                            @Override
                            public void onSuccess(StaffMembersResponse staffMembersResponse) {
                                onStaffMembersFetched(staffMembersResponse);
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

    /*-------------------------------------- Methods to fetch Contact Members -----------------------------------*/

    /**
     * Initializes the API to fetch Contacts of User.
     */
    void fetchContacts() {
        compositeDisposable.add(
                mApiCall.getAllContacts()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ContactsResponse>() {
                            @Override
                            public void onSuccess(ContactsResponse contactsResponse) {
                                onContactsFetched(contactsResponse);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                            }
                        }));
    }

    /**
     * Handles the successful response of API call, fetched as {@link ContactsResponse}.
     *
     * @param contactsResponse {@link ContactsResponse}.
     */
    private void onContactsFetched(ContactsResponse contactsResponse) {
        if (contactsResponse == null) getActions().onAPICallError("PostResponse Corrupted");
        else {
            if (contactsResponse.getStatus() == 200) {
                List<ContactsData> contacts = contactsResponse.getData();
                if (contacts == null || contacts.size() == 0) {
                    getActions().onContactsNotFound();
                } else {
                    getActions().onContactsFetched(contacts);
                }
            } else {
                getActions().onAPICallError(contactsResponse.getMessage());
            }
        }
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
    }
}