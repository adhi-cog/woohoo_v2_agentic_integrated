package com.sb.woohoo.ui.main.fragments.contactsprofile;

import static com.sb.woohoo.utils.Constants.HOLIDAY_INN_DOMAIN_ID;
import static com.sb.woohoo.utils.Constants.HUB_DETAILS;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.custom.CircleImageTransform;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.custom.RecyclerViewDivider;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.FragmentContactsProfileBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.PopFragment;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.StaffMember;
import com.sb.woohoo.ui.main.fragments.contactsprofile.di.ContactsProfileComponent;
import com.sb.woohoo.ui.main.fragments.contactsprofile.di.ContactsProfileModule;
import com.sb.woohoo.ui.main.fragments.contactsprofile.di.DaggerContactsProfileComponent;
import com.sb.woohoo.ui.main.fragments.contactsprofile.pojo.ContactInfo;
import com.sb.woohoo.ui.main.fragments.livefeedhub.LiveFeedToHubFragment;
import com.sb.woohoo.ui.main.fragments.livefeedhubaudio.LiveFeedToHubAudioFragment;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.Hub;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.sip.SipAccountManager;
import com.sb.woohoo.ui.main.sip.state.SipCallActionState;
import com.sb.woohoo.utils.Constants;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

/**
 * Shows the Profile of a Contact.
 *
 * @author Ritwik Jamuar.
 */
public class ContactsProfileFragment
        extends FeatureFragment<MainActivity>
        implements ContactsProfileContracts.View, ContactsLiveFeedAdapter.LiveFeedClickListener,
        ContactsLiveAudioFeedAdapter.LiveFeedAudioClickListener, PopFragment {

    private final String FEATURE_CATEGORY = Categories.Communication.COMMAND_COMMUNICATION_CONTACT;


    private FragmentContactsProfileBinding binding;

    // Presenter.
    @Inject
    ContactsProfilePresenter mPresenter;

    // Picasso.
    @Inject
    Picasso mPicasso;
    @Inject
    AnalyticsHelper helper;
    @Inject
    SharedPrefsHelper sharedPrefs;

    private String domainId;
    // Models.
    private ContactInfo mInfo;
    private Dialog dialog;

    public ContactsProfileFragment() { /*Required empty public constructor*/ }

    /**
     * Use this factory method to create a new instance of {@link ContactsProfileFragment} using the provided parameters.
     *
     * @return A new instance of {@link ContactsProfileFragment}.
     */
    public static ContactsProfileFragment newInstance(Bundle args) {
        ContactsProfileFragment fragment = new ContactsProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ContactsProfileFragment newInstance(HashMap<String, String> parameters) {
        ContactsProfileFragment fragment = new ContactsProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable("contact", parameters.get("contact"));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public String createTag() {
        return ContactsProfileFragment.class.getSimpleName();
    }

    /*********************************************** Fragment Callback Methods ***********************************************/

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    /**
     * Injects the {@link ContactsProfileFragment} to {@link ContactsProfileComponent}.
     */
    @Override
    public void initDagger() {
        ContactsProfileComponent component =
                DaggerContactsProfileComponent
                        .builder()
                        .contactsProfileModule(new ContactsProfileModule(ContactsProfileFragment.this))
                        .build();
        component.injectContactsProfileFragment(ContactsProfileFragment.this);
    }

    /**
     * Initializes the Views.
     *
     * @param view View of {@link ContactsProfileFragment}.
     */
    @Override
    public void bindViews(View view) {
        binding.interceptorLayout.setContext(getParentActivity());
        domainId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "");

        if (!isQuickLinkOnDomainEnable())
            binding.fragmentContactsProfileToolbar.commonToolbarTitle.setText(getString(R.string.Profile));

        onClickEvents();
    }


    boolean isQuickLinkOnDomainEnable() {
        return WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "-1").equals(Constants.RC_DOMAIN_ID);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        helper.track(new Screen.ContactsProfile(), null);
        binding = FragmentContactsProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        String KEY_CONTACT_INFO = "contactInfo";
        if (bundle != null) {
            //mPresenter.getContactByName(bundle.getString("contact"));
            if (bundle.containsKey(KEY_CONTACT_INFO)) {
                mInfo = (ContactInfo) bundle.get(KEY_CONTACT_INFO);
                bindDataToView();
            } else {
                mPresenter.getContactByName(bundle.getString("contact"));
            }
        }

    }

    public void onStaffMemberFetched(StaffMember member) {
        mInfo = convertToContactInfo(member);
        bindDataToView();
    }

    private ContactInfo convertToContactInfo(StaffMember member) {
        ContactInfo info = new ContactInfo();
        info.setId(String.valueOf(member.getId()));
        info.setEmail(member.getEmail());
        info.setImageURL(member.getImageUrl());
        info.setName(member.getName());
        info.setUser_id(String.valueOf(member.getUserId()));
        info.setLiveFeedAccess(member.getHub() != null ? member.getHub().getLiveFeedAccess() : true);
        info.setDrawableSource(member.getDrawableSource());
        info.setHub(member.getHub());

        return info;
    }


    @Override
    public BasePresenter getPresenter() {
        return mPresenter;
    }


    /**
     * Bind the DeviceData to the View.
     **/
    private void bindDataToView() {
        if (mInfo.getImageURL() != null && !mInfo.getImageURL().equalsIgnoreCase("")) {
            mPicasso.load(mInfo.getImageURL())
                    .fit()
                    .placeholder(R.drawable.ic_all_contact_default_48dp)
                    .error(R.drawable.ic_all_contact_default_48dp)
                    .transform(new CircleImageTransform())
                    .into(binding.fragmentContactsProfileImageViewContact);
        } else {
            mPicasso.load(mInfo.getDrawableSource())
                    .fit()
                    .transform(new CircleImageTransform())
                    .into(binding.fragmentContactsProfileImageViewContact);
        }
        binding.fragmentContactsProfileTextViewContactName.setText(getName(mInfo.getName()));
        binding.fragmentContactsProfileTextViewContactNumber.setText(!mInfo.getEmail().equalsIgnoreCase("") ?
                getPhoneNumber(mInfo.getPhoneNumbers(), mInfo.getEmail()) : getString(R.string.no_contact_added));

        if (mInfo.getLiveFeedAccess() != null && mInfo.getLiveFeedAccess() && !mInfo.getEmail().equalsIgnoreCase("")) {
            binding.tvNoAccess.setVisibility(View.GONE);
            binding.ivArrow.setVisibility(View.VISIBLE);
            binding.tvNoAccessAudio.setVisibility(View.GONE);
            binding.ivArrowAudio.setVisibility(View.VISIBLE);
            binding.rlLiveFeedLayout.setVisibility(View.VISIBLE);
            binding.rlAudioCallLayout.setVisibility(View.VISIBLE);
            binding.view.setVisibility(View.VISIBLE);

            binding.feedLayout.setVisibility(View.VISIBLE);
        } else {
            binding.tvNoAccess.setVisibility(View.VISIBLE);
            binding.ivArrow.setVisibility(View.GONE);
            binding.tvNoAccessAudio.setVisibility(View.VISIBLE);
            binding.ivArrowAudio.setVisibility(View.GONE);
            binding.rlLiveFeedLayout.setVisibility(View.GONE);
            binding.rlAudioCallLayout.setVisibility(View.GONE);
            binding.view.setVisibility(View.GONE);

            binding.feedLayout.setVisibility(View.GONE);

        }
        if (domainId.equalsIgnoreCase(HOLIDAY_INN_DOMAIN_ID)) {
            binding.rlLiveFeedLayout.setVisibility(View.GONE);
        }

        if (isQuickLinkOnDomainEnable())
            binding.fragmentContactsProfileToolbar.commonToolbarTitle.setText(getName(mInfo.getName()));
    }

    /**
     * Returns Name of User. Also returns 'NA' if {@code name} is null or its length is 0.
     *
     * @param name Name of User.
     * @return Formatted Name of User.
     */
    private String getName(String name) {
        if (name == null || name.length() == 0) name = "NA";
        return name;
    }

    /**
     * Returns Email/Phone Number of User. Also returns 'NA' if {@code email/phoneNumbers} is null or its size is 0.
     *
     * @param phoneNumbers {@link List} of Phone Numbers of User.
     * @param email
     * @return Formatted Phone Number of User.
     */
    private String getPhoneNumber(List<String> phoneNumbers, String email) {
        String phoneNumber;
        if (!email.equalsIgnoreCase("") && email.length() > 0) {
            phoneNumber = email;
        } else if (phoneNumbers != null && phoneNumbers.size() > 0) {
            phoneNumber = phoneNumbers.get(0);
        } else {
            phoneNumber = "NA";
        }
        return phoneNumber;
    }


    /************************************************ Views on-Click Listeners ***********************************************/

    private void onClickEvents() {
        binding.fragmentContactsProfileToolbar.commonToolbarBack.setOnClickListener(view -> onBackPressed());
        binding.rlLiveFeedLayout.setOnClickListener(view -> onLiveFeeLayoutClicked());
        binding.rlAudioCallLayout.setOnClickListener(view -> onAudioCallLayoutClicked());
    }

    void onBackPressed() {
        getParentActivity().onBackPressed();
    }

    void onLiveFeeLayoutClicked() {

        if (WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "-1").equals(Constants.RC_DOMAIN_ID)) {
            Toast.makeText(getContext(), getString(R.string.temp_feature_unavailable), Toast.LENGTH_LONG).show();
        } else {
            if (mInfo.getHub() != null) {
                doLiveFeedToHub(mInfo.getHub());
            } else {
                mPresenter.getListOfAccessibleHubs(mInfo.getId());
            }
        }
    }


    void onAudioCallLayoutClicked() {
        if (WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "-1").equals(Constants.RC_DOMAIN_ID)) {
            Toast.makeText(getContext(), getString(R.string.temp_feature_unavailable), Toast.LENGTH_LONG).show();
        } else if(WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_FEATURE_LINPHONE, false)) {
                Log.i("XXX", "onCallClicked:: " + mInfo.getPhoneNumber());
                SipCallActionState action = new SipCallActionState.outgoingCallInit(
                        mInfo.getPhoneNumber(),
                        mInfo.getName(),
                        mInfo.getPhoneNumber(),
                        mInfo.getImageURL());
                SipAccountManager.getInstance().sipCallActionSubject.onNext(action);
                //Toast.makeText(requireContext(), "Calling " + binding.fragmentDialerEditTextNumber.getText().toString(), Toast.LENGTH_SHORT).show();
        } else {
            if (mInfo.getHub() != null) {
                doAudioFeedToHub(mInfo.getHub());
            } else {
                mPresenter.getAudioListOfAccessibleHubs(mInfo.getId());
            }
        }
    }

    @Override
    public void onEmptyHubs() {
    }

    @Override
    public void onStaffMemberNotFound() {
        getParentActivity().onRespondToUser(getResources().getString(R.string.contact_not_found));

    }

    @Override
    public void onListOfHubsFetched(List<Hub> hubList) {
//        dialog = new Dialog(Objects.requireNonNull(getContext()), R.style.MyDialog);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        dialog.setContentView(R.layout.dialog_live_feed);
//        dialog.setCanceledOnTouchOutside(true);
//        dialog.setCancelable(true);
//        dialog.show();
        //setViews(dialog, hubList);
        if (hubList != null && hubList.size() > 0) {
            onItemClicked(hubList.get(0));
        } else {
            MethodUtils.showToastMessage(requireContext(), getString(R.string.try_again));

            LoggerWrapper.e(ContactsProfileFragment.class.getSimpleName(), "-- Hub List is empty/null");
        }
    }

    @Override
    public void onAudioListOfHubsFetched(List<Hub> hubList) {
//        dialog = new Dialog(Objects.requireNonNull(getContext()), R.style.MyDialog);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        dialog.setContentView(R.layout.dialog_live_feed);
//        dialog.setCanceledOnTouchOutside(true);
//        dialog.setCancelable(true);
//        dialog.show();
//        setViewsForAudio(dialog,hubList);
        if (hubList != null && hubList.size() > 0) {
            onItemClickedAudio(hubList.get(0));
        } else {
            MethodUtils.showToastMessage(requireContext(), getString(R.string.try_again));
            LoggerWrapper.e(ContactsProfileFragment.class.getSimpleName(), "-- Hub List is empty/null");
        }
    }


    private void setViews(final Dialog dialog, List<Hub> hubList) {
        if (dialog != null) {
            TextView tvContactsDevices = dialog.findViewById(R.id.txt_contacts_devices);
            tvContactsDevices.setText(!mInfo.getName().isEmpty() ?
                    splitName(mInfo.getName()) + "'s WooHoo Devices" : splitName(mInfo.getEmail()));

            dialog.findViewById(R.id.iv_cancel).setOnClickListener(view -> dialog.dismiss());

            dialog.findViewById(R.id.live_feed_layout).setOnClickListener(view -> dialog.dismiss());

            RecyclerView recyclerView = dialog.findViewById(R.id.rv_live_feed_list);
            recyclerView.setAdapter(new ContactsLiveFeedAdapter(hubList, this));
            recyclerView.addItemDecoration(new RecyclerViewDivider(getResources().getDrawable(R.drawable.line_divider)));
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }

    private void setViewsForAudio(final Dialog dialog, List<Hub> hubList) {
        if (dialog != null) {
            TextView tvContactsDevices = dialog.findViewById(R.id.txt_contacts_devices);
            tvContactsDevices.setText(!mInfo.getName().isEmpty() ?
                    splitName(mInfo.getName()) + "'s WooHoo Devices" : splitName(mInfo.getEmail()));

            dialog.findViewById(R.id.iv_cancel).setOnClickListener(view -> dialog.dismiss());

            dialog.findViewById(R.id.live_feed_layout).setOnClickListener(view -> dialog.dismiss());

            RecyclerView recyclerView = dialog.findViewById(R.id.rv_live_feed_list);
            recyclerView.setAdapter(new ContactsLiveAudioFeedAdapter(hubList, this));
            recyclerView.addItemDecoration(new RecyclerViewDivider(getResources().getDrawable(R.drawable.line_divider)));
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }

    private String splitName(String inputString) {
        if (inputString.contains(" ")) {
            String[] names = inputString.split(" ");
            return MethodUtils.setWordWithCaps(names[0]);
        } else if (inputString.contains("@")) {
            String[] emailName = inputString.split("@");
            return MethodUtils.setWordWithCaps(emailName[0]);
        } else {
            return inputString;
        }
    }

    void onParentClicked() {
    }

    @Override
    public void onItemClicked(Hub hub) {
        dismissDialog();
        LoggerWrapper.d("onItemClicked", hub.toString());
        if (hub.getConnected() != null && hub.getConnected()) {
            if (hub.getBusy() != null && hub.getBusy()) {
                MethodUtils.showToastMessage(requireContext(), getString(R.string.hub_busy));
            } else if (hub.getSurveillanceMode() != null && hub.getSurveillanceMode()) {
                MethodUtils.showToastMessage(requireContext(), getString(R.string.restricted_by_contact));
            } else {
                doLiveFeedToHub(hub);
            }
        } else {
            MethodUtils.showToastMessage(requireContext(), getString(R.string.hub_currently_offline));

        }
    }

    private void doLiveFeedToHub(Hub hub) {
        if (Constants.IS_AGORA_ENABLED && hub.getContactUserID() != Integer.parseInt(sharedPrefs.get(SharedPrefsKey.PREF_KEY_USER_ID, ""))) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(HUB_DETAILS, hub);
            bundle.putBoolean(Constants.CONTACT_LIVE_FEED, true);
            bundle.putParcelable(Constants.CONTACT_DETAILS, mInfo);

            getParentActivity().initLiveFeedToContactHub();
            getParentActivity().switchFragmentUsingAdd(LiveFeedToHubFragment.newInstance(bundle));
        } else {
            Toast.makeText(getParentActivity(), getString(R.string.call_not_allow), Toast.LENGTH_SHORT).show();
        }
    }


    private void doAudioFeedToHub(Hub hub) {
        if (Constants.IS_AGORA_ENABLED && hub.getContactUserID() != Integer.parseInt(sharedPrefs.get(SharedPrefsKey.PREF_KEY_USER_ID, ""))) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(HUB_DETAILS, hub);
            bundle.putBoolean(Constants.CONTACT_LIVE_FEED, true);
            bundle.putParcelable(Constants.CONTACT_DETAILS, mInfo);

            getParentActivity().initLiveFeedToContactHub();
            getParentActivity().switchFragmentUsingAdd(LiveFeedToHubAudioFragment.newInstance(bundle));
        } else {
            Toast.makeText(getParentActivity(), getString(R.string.call_not_allow), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClickedAudio(Hub hub) {
        dismissDialog();
        LoggerWrapper.d("onItemClickedAudio", hub.toString());
        if (hub.getConnected()) {
            if (hub.getBusy() != null && hub.getBusy()) {
                MethodUtils.showToastMessage(requireContext(), getString(R.string.hub_busy));
            } else if (hub.getSurveillanceMode() != null && hub.getSurveillanceMode()) {
                MethodUtils.showToastMessage(requireContext(), getString(R.string.restricted_by_contact));
            } else {
                doAudioFeedToHub(hub);
            }
        } else {
            MethodUtils.showToastMessage(requireContext(), getString(R.string.hub_currently_offline));

        }
    }

    private void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public void onError(String errorMessage) {
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        mPresenter.presenterCleanUps();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dismissDialog();
    }
}
