package com.sb.woohoo.ui.main.fragments.dialer;

import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.FragmentDialerBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.base.IBackPressInterceptor;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.contacts.ContactsFragment;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.ContactsData;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.StaffMember;
import com.sb.woohoo.ui.main.fragments.contactsprofile.ContactsProfileFragment;
import com.sb.woohoo.ui.main.fragments.contactsprofile.pojo.ContactInfo;
import com.sb.woohoo.ui.main.fragments.calllog.CallLogFragment;
import com.sb.woohoo.ui.main.fragments.dialer.di.DaggerDialerComponent;
import com.sb.woohoo.ui.main.fragments.dialer.di.DialerComponent;
import com.sb.woohoo.ui.main.fragments.dialer.di.DialerModule;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.sip.SipAccountManager;
import com.sb.woohoo.ui.main.sip.state.SipCallActionState;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

/**
 * Shows Dialer and Contact on Dialed Number Search.
 *
 * @author Ritwik Jamuar
 */
public class DialerFragment
        extends FeatureFragment<MainActivity>
        implements DialerContract.View,
        DialerRecyclerViewListener,
        IBackPressInterceptor,
        SipQuickActionAdapter.Listener {


    private final String FEATURE_CATEGORY = Categories.Communication.COMMAND_COMMUNICATION_CONTACT;

    private static String KEY_CONTACT_INFO = "contactInfo";
    private static final String STATE_MANUAL_DIALER_VISIBLE = "manualDialerVisible";

    @Inject
    DialerPresenter mPresenter;
    @Inject
    AnalyticsHelper helper;
    @Inject
    Picasso picasso;

    private static final String IS_SIP_MODE = "sipMode";
    private FragmentDialerBinding binding;
    private List<StaffMember> staffMembers = new ArrayList<>();
    private SipQuickActionAdapter quickActionAdapter;
    private boolean isManualDialerVisible = false;

    public DialerFragment() { /*Required empty public constructor*/ }

    /**
     * Use this factory method to create a new instance of this fragment using the provided parameters.
     *
     * @return A new instance of fragment DialerFragment.
     */
    public static DialerFragment newInstance() {
        DialerFragment fragment = new DialerFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_SIP_MODE, true);
        fragment.setArguments(args);
        return fragment;
    }

    public void getHotelStaffMembers() {
        mPresenter.fetchStaffMembers();
    }

    @Override
    public void onAPIError(String errorMessage) {
        MethodUtils.showToastMessage(getActivity(), errorMessage);
    }

    @Override
    public void setFamilyMembersToRecyclerView(List<StaffMember> staffMembers) {
        this.staffMembers = staffMembers != null ? staffMembers : new ArrayList<>();
        quickActionAdapter.submitContacts(this.staffMembers);
    }

    @Override
    public void showProgress() {
        getParentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.fragmentContactsProgressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void removeProgress() {
        getParentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.fragmentContactsProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public String createTag() {
        return DialerFragment.class.getSimpleName();
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        helper.track(new Screen.Dialer(),  null);
        binding = FragmentDialerBinding.inflate(inflater, container, false);
        if (savedInstanceState != null) {
            isManualDialerVisible = savedInstanceState.getBoolean(STATE_MANUAL_DIALER_VISIBLE, false);
        }
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

//    private void toggleFamilyMembersVisibility(boolean isFamilyMemberToBeVisible) {
//        if (isFamilyMemberToBeVisible) {
//            mRvFamily.setVisibility(View.VISIBLE);
//            relNoContact.setVisibility(View.GONE);
//        } else {
//            mRvFamily.setVisibility(View.GONE);
//            relNoContact.setVisibility(View.VISIBLE);
//        }
//    }

    /**
     * Injects the {@link DialerFragment} to {@link DialerComponent}.
     */
    @Override
    public void initDagger() {
        @SuppressWarnings("deprecation") DialerComponent component = DaggerDialerComponent
                .builder()
                .dialerModule(new DialerModule(DialerFragment.this))
                .build();
        component.injectDialerFragment(DialerFragment.this);
    }


    /**
     * Initializes the Views.
     *
     * @param view View of {@link DialerFragment}.
     */
    @Override
    public void bindViews(View view) {
        setClickEvents();
        setHint(HINT_NO_CHANGE, HINT_NO_CHANGE, View.GONE);
        binding.interceptorLayout.setContext(getParentActivity());
        binding.fragmentDialerEditTextNumber.setShowSoftInputOnFocus(false);

        quickActionAdapter = new SipQuickActionAdapter(picasso, this);
        GridLayoutManager quickActionsLayoutManager = new GridLayoutManager(getActivity(), 2);
        binding.sipQuickActionsRecyclerView.setLayoutManager(quickActionsLayoutManager);
        binding.sipQuickActionsRecyclerView.setAdapter(quickActionAdapter);
        binding.sipQuickActionsRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        binding.sipQuickActionsRecyclerView.setNestedScrollingEnabled(false);

        bindQuickActionsHeader();
        updateScreenState();
        getHotelStaffMembers();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        bindQuickActionsHeader();
    }

    @Override
    public BasePresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setHint(HINT_NO_CHANGE, HINT_NO_CHANGE, View.VISIBLE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_MANUAL_DIALER_VISIBLE, isManualDialerVisible);
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        mPresenter.presenterCleanUps();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setClickEvents() {
        binding.viewDialer.fragmentDialerViewTextView1.setOnClickListener(view -> on1Clicked());
        binding.viewDialer.fragmentDialerViewRelativeLayout2.setOnClickListener(view -> on2Clicked());
        binding.viewDialer.fragmentDialerViewRelativeLayout3.setOnClickListener(view -> on3Clicked());
        binding.viewDialer.fragmentDialerViewRelativeLayout4.setOnClickListener(view -> on4Clicked());
        binding.viewDialer.fragmentDialerViewRelativeLayout5.setOnClickListener(view -> on5Clicked());
        binding.viewDialer.fragmentDialerViewRelativeLayout6.setOnClickListener(view -> on6Clicked());
        binding.viewDialer.fragmentDialerViewRelativeLayout7.setOnClickListener(view -> on7Clicked());
        binding.viewDialer.fragmentDialerViewRelativeLayout8.setOnClickListener(view -> on8Clicked());
        binding.viewDialer.fragmentDialerViewRelativeLayout9.setOnClickListener(view -> on9Clicked());
        binding.viewDialer.fragmentDialerViewRelativeLayout0.setOnClickListener(view -> On0Clicked());
        binding.viewDialer.fragmentDialerViewTextViewStar.setOnClickListener(view -> onStarClicked());
        binding.viewDialer.fragmentDialerViewTextViewHash.setOnClickListener(view -> onHashClicked());
        binding.fragmentDialerImageViewHome.setOnClickListener(view -> onHomeClicked());
        binding.fragmentDialerImageViewBack.setOnClickListener(view -> onBackClicked());
        binding.fragmentDialerImageViewCall.setOnClickListener(view -> onCallClicked());
        binding.fragmentDialerImageViewLog.setOnClickListener(view -> onCallLogClicked());
        binding.sipBottomNav.sipBottomNavHomeButton.setOnClickListener(view -> onBottomNavHomeClicked());
        binding.sipBottomNav.sipBottomNavSpaButton.setOnClickListener(view -> onBottomNavSpaClicked());
        binding.sipBottomNav.sipBottomNavDiningButton.setOnClickListener(view -> onBottomNavDiningClicked());
        binding.sipBottomNav.sipBottomNavSettingsButton.setOnClickListener(view -> onBottomNavSettingsClicked());
    }
    void on1Clicked() {
        addIntegerText(1);
    }
    void on2Clicked() {
        addIntegerText(2);
    }
    void on3Clicked() {
        addIntegerText(3);
    }
    void on4Clicked() {
        addIntegerText(4);
    }
    void on5Clicked() {
        addIntegerText(5);
    }
    void on6Clicked() {
        addIntegerText(6);
    }
    void on7Clicked() {
        addIntegerText(7);
    }
    void on8Clicked() {
        addIntegerText(8);
    }
    void on9Clicked() {
        addIntegerText(9);
    }
    void On0Clicked() {addIntegerText(0);}

    void onStarClicked() {
        addText("*");
    }

    void onHashClicked() {
        addText("#");
    }

    void onHomeClicked() {
        if (isManualDialerVisible) {
            showQuickActions();
            return;
        }
        getParentActivity().onBackPressed();
        LoggerWrapper.d(createTag(), "home clicked");
    }

    void onBackClicked() {
        handleBackPressed();
    }

    void onCallLogClicked() {
        getParentActivity().switchFragmentUsingAdd(CallLogFragment.newInstance());
    }

    private void onBottomNavHomeClicked() {
        getParentActivity().onHomePressed();
    }

    private void onBottomNavSpaClicked() {
        getParentActivity().fragmentSwitch(getString(R.string.h_care_spa));
    }

    private void onBottomNavDiningClicked() {
        getParentActivity().fragmentSwitch(getString(R.string.in_room_dining));
    }

    private void onBottomNavSettingsClicked() {
        getParentActivity().onSettingsPressed();
    }

    void onCallClicked() {
        if(WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_FEATURE_LINPHONE, false)) {
            String dialedNumber = binding.fragmentDialerEditTextNumber.getText().toString();
            String normalizedDialedNumber = normalizeNumber(dialedNumber);
            if (normalizedDialedNumber.isEmpty()) {
                MethodUtils.showToastMessage(getContext(), getString(R.string.sip_manual_dialer_empty_error));
                return;
            }
            StaffMember matchedMember = findStaffMemberByNumber(normalizedDialedNumber);
            String displayName = matchedMember != null && matchedMember.getName() != null
                    && !matchedMember.getName().trim().isEmpty()
                    ? matchedMember.getName().trim() : normalizedDialedNumber;
            String imageUrl = matchedMember != null ? matchedMember.getImageUrl() : null;
            Log.i("XXX", "onCallClicked:: " + dialedNumber);
            SipCallActionState action = new SipCallActionState.outgoingCallInit(
                    normalizedDialedNumber,
                    displayName,
                    normalizedDialedNumber,
                    imageUrl);
            SipAccountManager.getInstance().sipCallActionSubject.onNext(action);
            //Toast.makeText(requireContext(), "Calling " + binding.fragmentDialerEditTextNumber.getText().toString(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), getString(R.string.temp_feature_unavailable), Toast.LENGTH_LONG).show();

        }


    }

    void onParentClicked() {
    }

    private void showQuickActions() {
        isManualDialerVisible = false;
        bindQuickActionsHeader();
        updateScreenState();
    }

    private void showManualDialer() {
        showManualDialer(null);
    }

    private void showManualDialer(@Nullable String phoneNumber) {
        isManualDialerVisible = true;
        if (binding != null) {
            String dialerText = TextUtils.isEmpty(phoneNumber) ? "" : getFormatedNumber(phoneNumber.trim());
            binding.fragmentDialerEditTextNumber.setText(dialerText);
            binding.fragmentDialerEditTextNumber.setSelection(
                    binding.fragmentDialerEditTextNumber.getText().length());
        }
        bindQuickActionsHeader();
        updateScreenState();
    }

    private void updateScreenState() {
        if (binding == null) {
            return;
        }
        binding.sipQuickActionsContainer.setVisibility(isManualDialerVisible ? View.GONE : View.VISIBLE);
        binding.fragmentDialerManualContainer.setVisibility(isManualDialerVisible ? View.VISIBLE : View.GONE);
        binding.fragmentDialerImageViewHome.setImageResource(
                isManualDialerVisible ? R.drawable.ic_back_arrow : R.drawable.ic_home_48dp
        );
        binding.fragmentDialerImageViewHome.setContentDescription(
                getString(isManualDialerVisible ? R.string.back : R.string.home)
        );
        boolean hasDialedNumber = hasDialedNumber();
        binding.fragmentDialerImageViewCall.setEnabled(hasDialedNumber);
        binding.fragmentDialerImageViewCall.setAlpha(hasDialedNumber ? 1f : 0.6f);
        binding.fragmentDialerImageViewBack.setVisibility(hasDialedNumber ? View.VISIBLE : View.INVISIBLE);
    }

    private void bindQuickActionsHeader() {
        if (binding == null) {
            return;
        }
        String roomLabel = getRoomLabel();
        binding.sipQuickActionsRoomLabel.setText(roomLabel);
        binding.sipManualRoomLabel.setText(roomLabel);
        binding.sipQuickActionsGreetingPrefix.setText(getGreetingPrefix());
        binding.sipQuickActionsGreetingName.setText(getGuestDisplayName());
    }

    @NonNull
    private String getRoomLabel() {
        String deviceName = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DEVICE_NAME, "");
        String deviceId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");
        String label = !TextUtils.isEmpty(deviceName) ? deviceName : deviceId;
        if (TextUtils.isEmpty(label)) {
            return getString(R.string.sip_room_fallback);
        }

        String trimmedLabel = label.trim();
        if (trimmedLabel.matches("\\d+[A-Za-z]?")) {
            return ("ROOM" + trimmedLabel).toUpperCase(Locale.US);
        }
        if (trimmedLabel.toUpperCase(Locale.US).startsWith("ROOM")) {
            return trimmedLabel.toUpperCase(Locale.US);
        }
        if (trimmedLabel.length() <= 6 && trimmedLabel.matches("[A-Za-z0-9]+")) {
            return ("ROOM" + trimmedLabel).toUpperCase(Locale.US);
        }
        return trimmedLabel.toUpperCase(Locale.US);
    }

    @NonNull
    private String getGreetingPrefix() {
        int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hourOfDay < 12) {
            return getString(R.string.sip_greeting_morning);
        }
        if (hourOfDay < 17) {
            return getString(R.string.sip_greeting_afternoon);
        }
        return getString(R.string.sip_greeting_evening);
    }

    @NonNull
    private String getGuestDisplayName() {
        String firstName = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_FIRST_NAME, "");
        String lastName = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_LAST_NAME, "");
        String fullName = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_NAME, "");

        String displayName;
        if (!TextUtils.isEmpty(firstName) || !TextUtils.isEmpty(lastName)) {
            displayName = (firstName + " " + lastName).trim();
        } else {
            displayName = fullName != null ? fullName.trim() : "";
        }

        if (TextUtils.isEmpty(displayName)) {
            return getString(R.string.sip_guest_fallback);
        }

        displayName = displayName.substring(0, 1).toUpperCase(Locale.US) + displayName.substring(1);
        return displayName.endsWith(".") ? displayName : displayName + ".";
    }

    /**
     * Adds Integer to EditText
     *
     * @param text Integer to be added.
     */
    private void addIntegerText(int text) {
        addText(String.valueOf(text));
    }

    /**
     * Adds a Text at the end of Input from EditText.
     *
     * @param text Text to be added
     */
    private void addText(String text) {
        String etText = binding.fragmentDialerEditTextNumber.getText().toString();
        // TODO : Validate the text obtained from this EditText
        if (etText.length() < 12 || etText.equals("")) {
            etText = etText + text;
            binding.fragmentDialerEditTextNumber.setText(getFormatedNumber(etText));
            binding.fragmentDialerEditTextNumber.setSelection(etText.length());
            updateScreenState();
        }
    }

    /**
     * Handles the click event of Back Button.
     */
    private void handleBackPressed() {
        // TODO : Logic not properly implemented. Implement it.
        int cursorStartPosition = binding.fragmentDialerEditTextNumber.getSelectionStart();
        int cursorEndPosition = binding.fragmentDialerEditTextNumber.getSelectionEnd();
        String text = binding.fragmentDialerEditTextNumber.getText().toString();
        StringBuilder builder = new StringBuilder(text);
        if (cursorStartPosition == cursorEndPosition) { // Cursor is only moved, no text selected.
            if (cursorStartPosition == 0)
                return;
            builder.deleteCharAt(cursorStartPosition - 1);
            text = builder.toString();
        } else {
            builder.delete(cursorStartPosition, cursorEndPosition);
            text = builder.toString();
        }

        binding.fragmentDialerEditTextNumber.setText(getFormatedNumber(text));
        binding.fragmentDialerEditTextNumber.setSelection(binding.fragmentDialerEditTextNumber.getText().length());
        updateScreenState();

    }

    String getFormatedNumber(String number) {
        // TODO : Implementing number formating
        if (number.contains("*") || number.contains("#"))
            return number;

        number = number.replace("-", "");
        int numberLength = number.length();

        if (numberLength <= 4) {
            return number;
        } else if (numberLength <= 7) {
            return number.substring(0, numberLength - 4) + number.substring(numberLength - 4).replaceFirst("(\\d{4})", "-$1");
        } else {
            return number.substring(0, numberLength - 7) + number.substring(numberLength - 7).replaceFirst("(\\d{3})(\\d{4})", "-$1-$2");
        }
    }

    boolean isSimSupport(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);  //gets the current TelephonyManager
        Boolean status = !(tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT ||
                tm.getSimState() == TelephonyManager.SIM_STATE_UNKNOWN);
        return status;
    }

    @Override
    public void onFamilyMemberClicked(StaffMember member) {
        if (member.getPhoneNumber() != null && !member.getPhoneNumber().trim().isEmpty()) {
            showManualDialer(member.getPhoneNumber().trim());
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_CONTACT_INFO, convertToContactInfo(member));
        getParentActivity().switchFragmentUsingAdd(ContactsProfileFragment.newInstance(bundle));

    }

    @Override
    public void onQuickContactClicked(StaffMember member) {
        onFamilyMemberClicked(member);
    }

    @Override
    public void onInternalCallClicked() {
        showManualDialer();
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
    public void onContactClicked(ContactsData contact) {

    }

    @Override
    public void onAllContactsClicked() {

        getParentActivity().closeSlidingTray();
        ContactsFragment fragment = new ContactsFragment();
        getParentActivity().switchFragmentUsingAdd(fragment);
    }

    @Override
    public void onBackPressed(BackCallback backCallback) {
        if (isManualDialerVisible) {
            showQuickActions();
            backCallback.backHandled();
            return;
        }
        backCallback.backCompleted();
    }

    @Nullable
    private StaffMember findStaffMemberByNumber(String dialedNumber) {
        if (dialedNumber == null || dialedNumber.isEmpty()) {
            return null;
        }
        for (StaffMember staffMember : staffMembers) {
            if (staffMember == null) {
                continue;
            }
            String memberNumber = normalizeNumber(staffMember.getPhoneNumber());
            if (!memberNumber.isEmpty() && memberNumber.equals(dialedNumber)) {
                return staffMember;
            }
        }
        return null;
    }

    @NonNull
    private String normalizeNumber(String number) {
        if (number == null) {
            return "";
        }
        return number.replace("-", "")
                .replace(" ", "")
                .trim();
    }

    private boolean hasDialedNumber() {
        return binding != null && !normalizeNumber(binding.fragmentDialerEditTextNumber.getText().toString()).isEmpty();
    }
}
