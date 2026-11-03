package com.sb.woohoo.ui.main.fragments.contactsprofile;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.StaffMember;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.StaffMembersResponse;
import com.sb.woohoo.ui.main.fragments.contactsprofile.pojo.StaffMemberResponse;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.Hub;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.HubListModel;

import org.json.JSONObject;

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
 * Repository for {@link ContactsProfilePresenter}.
 *
 * @author Ritwik Jamuar
 */
public class ContactsProfileRepository extends BaseRepository<ContactsProfilePresenter> {
    private WooHooApiCall mApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    public ContactsProfileRepository(WooHooApiCall mApiCall, CompositeDisposable compositeDisposable) {
        this.mApiCall = mApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    /**
     * API Call to get list of Accessible Hubs of Contacts, provided the access been granted by the contact
     *
     * @param contactId
     */

    public void getListOfAccessibleHubs(String contactId) {
        compositeDisposable.add(
                mApiCall.getAccessibleHubsOfContacts(contactId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<HubListModel>() {
                            @Override
                            public void onSuccess(HubListModel hubListModel) {
                                onAccessibleHubsFetched(hubListModel);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                            }
                        })
        );
    }

    public void getContactByName(String contactId) {
        compositeDisposable.add(
                mApiCall.getContactByName(WooHooApp.getSharedPrefsHelper().get(PREF_KEY_DEVICE_ID, ""),contactId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<StaffMemberResponse>() {
                            @Override
                            public void onSuccess(StaffMemberResponse hubListModel) {
                                onStaffMemberFetched(hubListModel);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                            }
                        })
        );
    }


    private void onStaffMemberFetched(StaffMemberResponse staffMembersResponse) {
        if (staffMembersResponse == null) {
            getActions().onAPICallError("PostResponse Corrupted");
        } else {
            if (staffMembersResponse.getStatus() == 200) {
                StaffMember member = staffMembersResponse.getData();
                if (member == null) {
                    getActions().onStaffMemberNotFound();
                } else {
                    getActions().onStaffMemberFound(member);
                }
            } else {
                getActions().onAPICallError(staffMembersResponse.getMessage());
            }
        }
    }
    public void getAudioListOfAccessibleHubs(String contactId) {
        compositeDisposable.add(
                mApiCall.getAccessibleHubsOfContacts(contactId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<HubListModel>() {
                            @Override
                            public void onSuccess(HubListModel hubListModel) {
                                onAudioAccessibleHubsFetched(hubListModel);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                            }
                        })
        );
    }

    /**
     * Handles the successful response of API call for list of Accessible Hubs of Contacts, fetched as {@link HubListModel}.
     *
     * @param hubListModel {@link HubListModel}.
     */
    private void onAccessibleHubsFetched(HubListModel hubListModel) {
        if (hubListModel == null) {
            getActions().onAPICallError("PostResponse Corrupted");
        } else {
            if (hubListModel.getStatus() == 200) {
                List<Hub> hubList = hubListModel.getData();
                if (hubList == null || hubList.size() == 0) {
                    getActions().onAccessibleHubsEmpty();
                } else {
                    getActions().onAccessibleHubsFound(hubList);
                }
            } else {
                getActions().onAPICallError(hubListModel.getMessage());
            }
        }
    }

    private void onAudioAccessibleHubsFetched(HubListModel hubListModel) {
        if (hubListModel == null) {
            getActions().onAPICallError("PostResponse Corrupted");
        } else {
            if (hubListModel.getStatus() == 200) {
                List<Hub> hubList = hubListModel.getData();
                if (hubList == null || hubList.size() == 0) {
                    getActions().onAccessibleHubsEmpty();
                } else {
                    getActions().onAudioAccessibleHubsFound(hubList);
                }
            } else {
                getActions().onAPICallError(hubListModel.getMessage());
            }
        }
    }


    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
    }


}