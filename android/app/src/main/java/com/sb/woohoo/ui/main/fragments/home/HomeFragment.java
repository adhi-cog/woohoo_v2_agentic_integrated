package com.sb.woohoo.ui.main.fragments.home;

import static android.view.View.VISIBLE;
import static com.sb.woohoo.data.prefs.SharedPrefsKey.PREF_INTRO_VIDEO_KEY;
import static com.sb.woohoo.data.prefs.SharedPrefsKey.PREF_MUTE_STATE;
import static com.sb.woohoo.leia.helpers.InstalledAppHelper.LEIA_PACKAGE_NAME;
import static com.sb.woohoo.utils.Constants.CHROME_UNTRUSTED_URL;
import static com.sb.woohoo.utils.Constants.HOLIDAY_INN_DOMAIN_ID;
import static com.sb.woohoo.utils.Constants.POSTILLION_DOMAIN_ID;
import static com.sb.woohoo.utils.LocaleHelper.getLanguage;
import static com.sb.woohoo.utils.others.MethodUtils.getVersionCode;
import static com.sb.woohoo.utils.others.MethodUtils.setActivityFullScreen;
import static com.sb.woohoo.utils.others.MethodUtils.showToastMessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.util.Pair;
import androidx.core.widget.ImageViewCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.sb.woohoo.HomeFragmentViewModel;
import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.CustomEvents;
import com.sb.woohoo.analytics.FnbEvent;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.custom.CircleImageTransform;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.FragmentHomeBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.base.IBackPressInterceptor;
import com.sb.woohoo.ui.main.GridRVAdapter;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.contacts.ContactsFragment;
import com.sb.woohoo.ui.main.fragments.contacts.ContactsPresenter;
import com.sb.woohoo.ui.main.fragments.contacts.ContactsRecyclerViewListener;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.ContactsData;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.StaffMember;
import com.sb.woohoo.ui.main.fragments.contactsprofile.ContactsProfileFragment;
import com.sb.woohoo.ui.main.fragments.contactsprofile.pojo.ContactInfo;
import com.sb.woohoo.ui.main.fragments.diningrestaurants.DiningRestaurantsFragment;
import com.sb.woohoo.ui.main.fragments.feedback.FeedbackFragment;
import com.sb.woohoo.ui.main.fragments.fnb.categories.FnBCategoriesFragment;
import com.sb.woohoo.ui.main.fragments.fnb.leftpane.FnBLeftPaneFragment;
import com.sb.woohoo.ui.main.fragments.fnb.sub_categories.FnBSubCategoriesFragment;
import com.sb.woohoo.ui.main.fragments.home.date.DateAlarmManager;
import com.sb.woohoo.ui.main.fragments.home.date.DateAlarmReceiver;
import com.sb.woohoo.ui.main.fragments.home.di.DaggerHomeComponent;
import com.sb.woohoo.ui.main.fragments.home.di.HomeComponent;
import com.sb.woohoo.ui.main.fragments.home.di.HomeModule;
import com.sb.woohoo.ui.main.fragments.home.pojo.HotelDetailsData;
import com.sb.woohoo.ui.main.fragments.newstext.headline.NewsHeadlineFragment;
import com.sb.woohoo.ui.main.fragments.overview.categories.OverviewCategoriesFragment;
import com.sb.woohoo.ui.main.fragments.overview.saudi.SaudiTourismFragment;
import com.sb.woohoo.ui.main.fragments.overview.subcategories.OverviewSubCategoriesFragment;
import com.sb.woohoo.ui.main.fragments.qrcode.QRCodeFragment;
import com.sb.woohoo.ui.main.fragments.reservation.booking.ReservationFragment;
import com.sb.woohoo.ui.main.fragments.settings.language.LanguageChangeFragment;
import com.sb.woohoo.ui.main.fragments.weather.WeatherFragment;
import com.sb.woohoo.ui.main.fragments.webview.WebViewFragment;
import com.sb.woohoo.ui.main.fragments.webviewdefault.WebViewDefaultFragment;
import com.sb.woohoo.ui.main.fragments.dialer.DialerFragment;
import com.sb.woohoo.ui.main.pojo.DrawerGridItems;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.pojo.StartSession;
import com.sb.woohoo.utils.Constants;
import com.sb.woohoo.utils.others.LangUtils;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class HomeFragment
        extends FeatureFragment<MainActivity>
        implements IBackPressInterceptor,
        HomeContract.View,
        ContactsRecyclerViewListener,
        AppItemClickListener {
    public static int DAILY_CHANGER_DATE_REQUEST_CODE = 1000;
    // Constant.
    private static final String HOME_TAG = HomeFragment.class.getSimpleName();
    private static String KEY_CONTACT_INFO = "contactInfo";


    private GridRVAdapter bottomDrawerAdapter;

    // Adapters.
    @Inject
    FamilyRecyclerAdapter mRAFamily;
    // Presenter.
    @Inject
    HomePresenter mPresenter;

    @Inject
    Picasso picasso;

    @Inject
    AnalyticsHelper helper;

    private FragmentHomeBinding binding;

    private final Pair<String, String> localApp = new Pair(LEIA_PACKAGE_NAME, "Leia");

    private DraweeController controller;

    private HomeLeftPaneFragment leftPaneFragment;
    HomeFragmentViewModel model;

    private String domainId;
    private List<StaffMember> staffMemberList;

//    private RVAppsAdapter rvAppsAdapter;

    public HomeFragment() { /*Required empty public constructor*/ }

    // TODO : Move this to BroadcastHelper.
    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                    int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                    changeBatteryIcon(level);
                    int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
                    batteryCharging(plugged);
                }
            }
        }
    };


    private Handler handler = new Handler();
    private Runnable dateThreadRunnable = this::setDate;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    private void changeBatteryIcon(int level) {
        if (level > 0 && level < 20) {
            binding.imgBattery.setImageResource(R.drawable.ic_battery_0_20);
        } else if (level > 20 && level < 50) {
            binding.imgBattery.setImageResource(R.drawable.ic_battery_20_50);
        } else if (level > 50 && level < 90) {
            binding.imgBattery.setImageResource(R.drawable.ic_battery_50_90);
        } else {
            binding.imgBattery.setImageResource(R.drawable.ic_battery_90_100);
        }
    }

    private void changeBluetooth(Boolean isBluetoothCheck) {
        if (isBluetoothCheck) {
            binding.imgBluetooth.setVisibility(View.VISIBLE);
            binding.imgBluetooth.setImageResource(R.drawable.ic_bluetooth);
            ImageViewCompat.setImageTintList(binding.imgBluetooth, ColorStateList.valueOf(getResources().getColor(R.color.white)));
        } else {
            binding.imgBluetooth.setVisibility(View.GONE);
        }
    }

    private void batteryCharging(int plugged) {
        if (plugged == 1) {
            binding.imgBattery.setImageResource(R.drawable.ic_battery_charging);
        }
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        HomeComponent component =
                DaggerHomeComponent
                        .builder()
                        .homeModule(new HomeModule(HomeFragment.this))
                        .build();
        component.injectHomeFragment(HomeFragment.this);
    }

    @Override
    public void bindViews(View view) {
        binding.relaivelayout.setContext(getParentActivity());
        applyScreenColor(WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_SCREEN_COLOR, ""));
        domainId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "");
        initViews();
        setClickEvents();
        setUpOrbAnimation();

        binding.hotelDetails.setVisibility(View.GONE);
        binding.orbAnimation.setVisibility(View.VISIBLE);
        playOrbAnimation();

        getHotelStaffMembers();
        if (isQuickLinkOnDomainEnable()) {
            binding.llQuickLinks.setVisibility(View.VISIBLE);
        } else {
            binding.llQuickLinks.setVisibility(View.GONE);
        }

        // Skip unstable hotel-details startup fetch; keep home responsive with existing fallback UI.
        initSlidingBottomTray();
        mPresenter.getAppStoreList();
        mPresenter.getDeviceName();
        initIntroVideo();

        mPresenter.getSIPConfiguration();
    }

    private void initIntroVideo() {
        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            String mediaURI = WooHooApp.getSharedPrefsHelper().get(PREF_INTRO_VIDEO_KEY, null);
            if (mediaURI != null && !mediaURI.isEmpty()) {
                binding.btnIntro.show();
            } else {
                binding.btnIntro.hide();
            }
            binding.btnIntro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playIntroVideoFromRaw();
                }
            });
        }
    }

    private void playIntroVideoFromRaw() {
        IntroVideoPlayerDialog dialog = new IntroVideoPlayerDialog(getParentActivity(), getParentActivity());
        dialog.show();
    }


    boolean isQuickLinkOnDomainEnable() {
        return domainId.equals(Constants.GBTB_DOMAIN_ID)
                || domainId.equals(Constants.REGENT_DOMAIN_ID);
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public String createTag() {
        return HomeFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        helper.track(new Screen.Home(), null);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onError(@StringRes int msgId) {
        MethodUtils.showToastMessage(getParentActivity(), getString(msgId));
    }

    @Override
    public void updateDeviceName(String name) {
        binding.tvDeviceName.setText(name);
        WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_DEVICE_NAME, name);
    }


    void onLangClick() {
        getParentActivity().switchFragmentUsingAdd(LanguageChangeFragment.newInstance());
    }


    @Override
    public void sessionStarted(StartSession appstoreRes) {
        WebViewFragment fragment = WebViewFragment.newInstance(appstoreRes);
        getParentActivity().switchFragmentUsingAdd(fragment);

    }

    private Drawable getAppIconIfInstalled(String packageName) {
        PackageManager packageManager = getParentActivity().getPackageManager();
        try {
            packageManager.getPackageInfo(packageName, 0);
            return getParentActivity().getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private void initSlidingBottomTray() {
        List<DrawerGridItems> itemsList = mPresenter.initListOfDrawerItems();
        if (localApp != null) {
            String appPackage = localApp.first;
            Drawable appIcon = getAppIconIfInstalled(appPackage);
            if (appIcon != null) {
                itemsList.add(2, new DrawerGridItems(null, localApp.second, appPackage, appIcon));
            }
        }
        showDrawerAsPerDomain(itemsList);
    }

    private GridRVAdapter.GridItemClickListener gridItemClickListener = new GridRVAdapter.GridItemClickListener() {
        @Override
        public void onGridItemSelected(DrawerGridItems drawerGridItems) {
            if (drawerGridItems.getData() != null && drawerGridItems.getData().getId() != 0) {
                mPresenter.callSessionStart(drawerGridItems);
            } else {
                if (localApp != null && drawerGridItems.getTitle().equalsIgnoreCase(localApp.second)) {
                    Intent launchIntent = getParentActivity().getPackageManager().getLaunchIntentForPackage(drawerGridItems.getAppPackage());
                    if (launchIntent != null) {
                        startActivity(launchIntent);
                    } else {
                        //TODO - log error as this is unexpected situation which ideally should not happen
                    }
                } else {
                    getParentActivity().fragmentSwitch(drawerGridItems.getTitle());
                }
            }
        }

        @Override
        public void onGridItemMoved(List<DrawerGridItems> drawerGridItemsList) {
        }
    };

    public boolean hideBottomSliderForSpecificDomain() {
        if (WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "").equalsIgnoreCase(Constants.RC_DOMAIN_ID) ||
                WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "").equalsIgnoreCase(Constants.GBTB_DOMAIN_ID)) {
            return true;
        }
        return false;
    }

    public void showDrawerAsPerDomain(List<DrawerGridItems> itemsList) {

        if (hideBottomSliderForSpecificDomain()) {
            binding.homeBottomSheet.bottomSheet.setVisibility(View.GONE);
            binding.fragmentHomeViewLineBottom.setVisibility(View.VISIBLE);
            initHomeTilesRV(itemsList);
        } else {
            binding.fragmentHomeViewLineBottom.setVisibility(View.GONE);
            binding.homeBottomSheet.bottomSheet.setVisibility(View.VISIBLE);
            initBottomRV(itemsList);
        }
    }

    public void initHomeTilesRV(List<DrawerGridItems> itemsList) {
        // Show every configured app when the bottom drawer is hidden.
        bottomDrawerAdapter = new GridRVAdapter(getParentActivity(), gridItemClickListener,
                itemsList, WooHooApp.get(getParentActivity()).getWooHooAppComponent().getPicasso());
        binding.rvTopGridOne.setAdapter(bottomDrawerAdapter);
        binding.rvTopGridOne.setLayoutManager(new GridLayoutManager(getParentActivity(), 3));
    }

    public void initBottomRV(List<DrawerGridItems> itemsList) {
        bottomDrawerAdapter = new GridRVAdapter(getParentActivity(), gridItemClickListener,
                itemsList, WooHooApp.get(getParentActivity()).getWooHooAppComponent().getPicasso());
        binding.homeBottomSheet.rvBottomGrid.setAdapter(bottomDrawerAdapter);
        binding.homeBottomSheet.rvBottomGrid.setLayoutManager(new GridLayoutManager(getParentActivity(), 3));
    }

    private void initBroadcast() {
        registerBroadcasts();
        DateAlarmManager.updateDate(getActivity(), DateAlarmReceiver.class, DAILY_CHANGER_DATE_REQUEST_CODE);
    }

    private void initViews() {
        // binding.tvLangCode.setText(R.string.home_screen_lang);
        String lang = getLanguage(getActivity());
        binding.tvLangCode.setText(LangUtils.getLanguageName(lang));

        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(binding.homeBottomSheet.bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        binding.homeBottomSheet.bottomArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                } else {
                    binding.homeBottomSheet.rvBottomGrid.smoothScrollToPosition(0);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.homeBottomSheet.rvBottomGrid.smoothScrollToPosition(0);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                binding.homeBottomSheet.bottomArrow.setRotation(slideOffset * 180);
            }
        });
        //Porting Changes - Changed from LinearLayoutManager to GridLayoutManager
        GridLayoutManager familyLLM = new GridLayoutManager(getActivity(), 5, GridLayoutManager.VERTICAL, false);
        binding.contactsRecyclerView.setLayoutManager(familyLLM);
        //binding.contactsRecyclerView.setEmptyView ( mTvNoFamilyMembers );
        binding.contactsRecyclerView.setAdapter(mRAFamily);
        //scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        binding.contactsRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        // setActivityFullScreen(getParentActivity());

        domainIdBasedUI();
        setMute();
        setDate();
        model = new ViewModelProvider(getActivity()).get(HomeFragmentViewModel.class);
        model.isBluetoothCheck.observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isBluetoothCheck) {
                LoggerWrapper.e("Bluetooth is :", isBluetoothCheck.toString());
                changeBluetooth(isBluetoothCheck);
            }
        });

        binding.versionName.setText(getVersionCode());
    }

    private void domainIdBasedUI() {
        if (domainId.equalsIgnoreCase(Constants.RC_DOMAIN_ID)) {
            Picasso.get().load(R.drawable.icon_info).resize(100, 100).centerCrop().into(binding.ivFirstLink);
            binding.txtFirstLink.setText(getString(R.string.overview));
            Picasso.get().load(R.drawable.bakery).resize(100, 100).centerCrop().into(binding.ivSecondLink);
            binding.txtSecondLink.setText(getString(R.string.bakery));
            Picasso.get().load(R.drawable.honor_bar).resize(100, 100).centerCrop().into(binding.imgThirdLink);
            binding.txtThirdLink.setText(getString(R.string.honor_bar));
            Picasso.get().load(R.drawable.icon_weather_new).resize(100, 100).centerCrop().into(binding.imgFifthLink);
            binding.txtFifthLink.setText(getString(R.string.weather));
        } else if (domainId.equals(Constants.GBTB_DOMAIN_ID)) {
            Picasso.get().load(R.drawable.icon_whatson).resize(100, 100).centerCrop().into(binding.ivFirstLink);
//            ivFnb.setImageResource(R.drawable.icon_whatson);
            binding.txtFirstLink.setText(getString(R.string.whatson));
            Picasso.get().load(R.drawable.getting_around).resize(100, 100).centerCrop().into(binding.ivSecondLink);
//            ivFeedback.setImageResource(R.drawable.getting_around);
            binding.txtSecondLink.setText(getString(R.string.getting_around));
            Picasso.get().load(R.drawable.icon_dining).resize(100, 100).centerCrop().into(binding.imgThirdLink);
//            imgQrCode.setImageResource(R.drawable.icon_dining);
            binding.txtThirdLink.setText(getString(R.string.dining));
            Picasso.get().load(R.drawable.icon_weather_gbtb).resize(100, 100).centerCrop().into(binding.imgForthLink);
//            imgNightMode.setImageResource(R.drawable.icon_weather_gbtb);
            binding.txtForthLink.setText(getString(R.string.weather));
            Picasso.get().load(R.drawable.icon_feedback_gbtb).resize(100, 100).centerCrop().into(binding.imgFifthLink);
//            ivReception.setImageResource(R.drawable.icon_feedback_gbtb);
            binding.txtFifthLink.setText(getString(R.string.feedback));
        } else if (domainId.equals(Constants.CKS_DOMAIN_MOCK_ROOM_ID) ||
                domainId.equals(Constants.REGENT_DOMAIN_ID) ||
                domainId.equals(Constants.CKS_DOMAIN_USER_ID)) {
            Picasso.get().load(R.drawable.feedback).resize(100, 100).centerCrop().into(binding.ivFirstLink);
            binding.txtFirstLink.setText(getString(R.string.feedback));
            binding.ivSecondLink.setImageResource(R.drawable.info);
            binding.txtSecondLink.setText(getString(R.string.info));
            if (domainId.equals(Constants.REGENT_DOMAIN_ID)) {
                Picasso.get().load(R.drawable.bookings_regent).resize(100, 100).centerCrop().into(binding.imgThirdLink);
                binding.txtThirdLink.setText(getString(R.string.your_bookings));
                Picasso.get().load(R.drawable.newspaper).resize(100, 100).centerCrop().into(binding.imgForthLink);
                binding.txtForthLink.setText(getString(R.string.fragment_news_text_view_title));
                binding.imgFifthLink.setImageResource(R.drawable.icon_weather);
                binding.txtFifthLink.setText(getString(R.string.weather));
            } else {
                Picasso.get().load(R.drawable.reservation).resize(100, 100).centerCrop().into(binding.imgThirdLink);
                binding.txtThirdLink.setText(getString(R.string.reservation));
                Picasso.get().load(R.drawable.ic_night_mode).resize(100, 100).centerCrop().into(binding.imgForthLink);
                binding.txtForthLink.setText(getString(R.string.night_mode));
                setMuteIconForQuickLink();

            }
        } else if (domainId.equalsIgnoreCase(Constants.PARK_ROYAL_DOMAIN_ID)) {
            Picasso.get().load(R.drawable.feedback).resize(100, 100).centerCrop().into(binding.ivSecondLink);
            binding.txtSecondLink.setText(getString(R.string.feedback));
            Picasso.get().load(R.drawable.reservation).resize(100, 100).centerCrop().into(binding.imgThirdLink);
            binding.txtThirdLink.setText(getString(R.string.reservation));
            Picasso.get().load(R.drawable.info).resize(100, 100).centerCrop().into(binding.imgForthLink);
            binding.txtForthLink.setText(getString(R.string.compendium_park_royal));
            Picasso.get().load(R.drawable.screen_cast).resize(100, 100).centerCrop().into(binding.imgFifthLink);
            binding.txtFifthLink.setText(getString(R.string.screen_cast));
        } else if (domainId.equalsIgnoreCase(POSTILLION_DOMAIN_ID)) {
            binding.ivFirstLink.setImageResource(R.drawable.ic_feedback_ql);
            binding.txtFirstLink.setText(getString(R.string.feedback));
            binding.ivSecondLink.setImageResource(R.drawable.ic_hotel_info_ql);
            binding.txtSecondLink.setText(getString(R.string.info));
            binding.imgThirdLink.setImageResource(R.drawable.ic_bluetooth_ql);
            binding.txtThirdLink.setText(getString(R.string.bluetooth));
            binding.imgForthLink.setImageResource(R.drawable.ic_night_mode_ql);
            binding.txtForthLink.setText(getString(R.string.sleep_mode));
            setMuteIconForQuickLink();
        }
    }

    public void updateList(List<DrawerGridItems> itemsList) {
        initHomeTilesRV(itemsList);
    }

    private void runDateThread() {
        // Fall back wake-word thread runner, in case if something times out
        handler.removeCallbacksAndMessages(dateThreadRunnable);
        handler.postDelayed(dateThreadRunnable, 60 * 1000);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        attachParent((MainActivity) getActivity());
        setLeftPaneFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBroadcast();
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    @Override
    public void onResume() {
        setActivityFullScreen(getParentActivity());
        super.onResume();
        runDateThread();
    }

    /**
     * Tells the {@link ContactsPresenter} to make API call to fetch list of Family Members.
     */
    public void getHotelStaffMembers() {
        mPresenter.fetchStaffMembers();
    }

    public void callHotelDetailsAPI() {
        mPresenter.fetchHotelDetails();
    }


    @Override
    public void onFamilyMemberClicked(StaffMember member) {

        String callLabel = getString(R.string.call);
        if (member.getName() != null && member.getName().equalsIgnoreCase(callLabel)) {
            getParentActivity().switchFragmentUsingAdd(DialerFragment.newInstance());
            return;
        }

        if (member.getId() == 0) {
            showToastMessage(getParentActivity(),getString(R.string.quicky_link_id_missing));
            return;
        }

        LoggerWrapper.d(getTag(), " onFamilyMemberClicked : Clicked on : member : "+member.getName());
       // Admin Domain 90

        if (member.getId() == 236 ) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(KEY_CONTACT_INFO, convertToContactInfo(member));
            getParentActivity().switchFragmentUsingAdd(ContactsProfileFragment.newInstance(bundle));
        }else if (member.getId() == 232) {
            getParentActivity().switchFragmentUsingAdd(new FeedbackFragment());
        }else if (member.getId() == 233) {
            getParentActivity().darkModeOptions();
        }else if (member.getId() == 234) {
            getParentActivity().goToBluetooth();
        }else if (member.getId() == 235) {
            OverviewCategoriesFragment fragment = OverviewCategoriesFragment.newInstance();
            getParentActivity().switchFragmentUsingAdd(fragment);
        }

        //Holiday inn 122

        if (member.getId() == 702) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(KEY_CONTACT_INFO, convertToContactInfo(member));
            getParentActivity().switchFragmentUsingAdd(ContactsProfileFragment.newInstance(bundle));
        } else if (member.getId() == 703) {
            getParentActivity().switchFragmentUsingAdd(new FeedbackFragment());
        } else if (member.getId() == 706) {
            getParentActivity().switchFragmentUsingAdd(new SaudiTourismFragment());
        } else if (member.getId() == 705) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(KEY_CONTACT_INFO, convertToContactInfo(member));
            getParentActivity().switchFragmentUsingAdd(ContactsProfileFragment.newInstance(bundle));
        } else if (member.getId() == 704) {
            getParentActivity().darkModeOptions();
        }

        //RC 100

        if (member.getId() == 397) {
            getParentActivity().switchFragmentUsingAdd(OverviewCategoriesFragment.newInstance());
        } else if (member.getId() == 398) {
            helper.track(CustomEvents.QUICK_LINKS_FEEDBACK, null);
            Bundle bundle = new Bundle();
            bundle.putString("category_id", "72");
            bundle.putBoolean("category_sub_type", true);
            FnBSubCategoriesFragment fnBSubCategoriesFragment = FnBSubCategoriesFragment.newInstance();
            fnBSubCategoriesFragment.setArguments(bundle);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                FnBLeftPaneFragment fnBLeftPaneFragment = FnBLeftPaneFragment.getInstance(FnBSubCategoriesFragment.class.getSimpleName());
                getParentActivity().switchFragmentUsingAdd(fnBLeftPaneFragment, fnBSubCategoriesFragment);
            } else {
                getParentActivity().switchFragmentUsingReplace(fnBSubCategoriesFragment);
            }
        } else if (member.getId() == 399) {
            Bundle bundle = new Bundle();
            bundle.putString("category_id", "71");
            bundle.putBoolean("category_sub_type", true);
            FnBSubCategoriesFragment fnBSubCategoriesFragment = FnBSubCategoriesFragment.newInstance();
            fnBSubCategoriesFragment.setArguments(bundle);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                FnBLeftPaneFragment fnBLeftPaneFragment = FnBLeftPaneFragment.getInstance(FnBSubCategoriesFragment.class.getSimpleName());
                getParentActivity().switchFragmentUsingAdd(fnBLeftPaneFragment, fnBSubCategoriesFragment);
            } else {
                getParentActivity().switchFragmentUsingAdd(fnBSubCategoriesFragment);
            }
        } else if (member.getId() == 400) {
            getParentActivity().darkModeOptions();
        } else if (member.getId() == 401) {
            getParentActivity().switchFragmentUsingAdd(WeatherFragment.newInstance(null));
        }


        //cks 72

        if (member.getId() == 166) {
            FeedbackFragment fragment = new FeedbackFragment();
            getParentActivity().switchFragmentUsingAdd(fragment);
        } else if (member.getId() == 167) {
            OverviewCategoriesFragment fragment = OverviewCategoriesFragment.newInstance();
            getParentActivity().switchFragmentUsingAdd(fragment);
        } else if (member.getId() == 168) {
            getParentActivity().switchFragmentUsingAdd(DiningRestaurantsFragment.newInstance());
        } else if (member.getId() == 169) {
            getParentActivity().darkModeOptions();
        } else if (member.getId() == 170) {
            onMutePressedAction();
            setMute();
            setMuteIconForQuickLink();
        }


        //postillion 104 ok

        if (member.getId() == 517) {
            FeedbackFragment fragment = new FeedbackFragment();
            getParentActivity().switchFragmentUsingAdd(fragment);
        } else if (member.getId() == 518) {
            OverviewCategoriesFragment fragment = OverviewCategoriesFragment.newInstance();
            getParentActivity().switchFragmentUsingAdd(fragment);
        } else if (member.getId() == 519) {
            getParentActivity().goToBluetooth();
        } else if (member.getId() == 520) {
            getParentActivity().darkModeOptions();
        } else if (member.getId() == 521) {
            onMutePressedAction();
            setMute();
            setMuteIconForQuickLink();
        }


        //park royal 112

        if (member.getId() == 587) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(KEY_CONTACT_INFO, convertToContactInfo(staffMemberList.get(3)));
            getParentActivity().switchFragmentUsingAdd(ContactsProfileFragment.newInstance(bundle));
        } else if (member.getId() == 588) {
            FeedbackFragment fragment = new FeedbackFragment();
            getParentActivity().switchFragmentUsingAdd(fragment);
        } else if (member.getId() == 589) {
            getParentActivity().darkModeOptions();
        } else if (member.getId() == 590) {
            getParentActivity().switchFragmentUsingAdd(OverviewCategoriesFragment.newInstance());
        } else if (member.getId() == 591) {
            getParentActivity().switchFragmentUsingAdd(WebViewDefaultFragment.newInstance(CHROME_UNTRUSTED_URL, WebViewDefaultFragment.WebviewLaunchMode.Default));
        }

        //park royal pickering  127

        if (member.getId() == 735) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(KEY_CONTACT_INFO, convertToContactInfo(staffMemberList.get(3)));
            getParentActivity().switchFragmentUsingAdd(ContactsProfileFragment.newInstance(bundle));
        } else if (member.getId() == 733) {
            FeedbackFragment fragment = new FeedbackFragment();
            getParentActivity().switchFragmentUsingAdd(fragment);
        } else if (member.getId() == 734) {
            getParentActivity().darkModeOptions();
        } else if (member.getId() == 736) {
            getParentActivity().switchFragmentUsingAdd(OverviewCategoriesFragment.newInstance());
        } else if (member.getId() == 732) {
            getParentActivity().switchFragmentUsingAdd(WebViewDefaultFragment.newInstance(CHROME_UNTRUSTED_URL, WebViewDefaultFragment.WebviewLaunchMode.Default));
        }

        // Khalidiya  palace 125

        if (member.getId() == 730) {
            FeedbackFragment fragment = new FeedbackFragment();
            getParentActivity().switchFragmentUsingAdd(fragment);
        } else if (member.getId() == 731) {
            OverviewCategoriesFragment fragment = OverviewCategoriesFragment.newInstance();
            getParentActivity().switchFragmentUsingAdd(fragment);
        } else if (member.getId() == 727) {
            getParentActivity().switchFragmentUsingAdd(ReservationFragment.newInstance(null));
        } else if (member.getId() == 728) {
            getParentActivity().darkModeOptions();
        } else if (member.getId() == 729) {
            onMutePressedAction();
            setMute();
            setMuteIconForQuickLink();
        }

    }


    /**
     * Builds POJO Model {@link ContactInfo} to be fed to {@link com.sb.woohoo.ui.main.fragments.contactsprofile.ContactsProfileFragment}
     * for showing details of a Contact.
     *
     * @param member {@link StaffMember}.
     * @return Instance of {@link ContactInfo}.
     */
    private ContactInfo convertToContactInfo(StaffMember member) {
        ContactInfo info = new ContactInfo();
        info.setId(String.valueOf(member.getId()));
        info.setEmail(member.getEmail());
        info.setImageURL(member.getImageUrl());
        info.setPhoneNumber(member.getPhoneNumber());
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
    public void setFamilyMembersToRecyclerView(List<StaffMember> staffMembers) {
        staffMemberList = staffMembers;
        if (!isQuickLinkOnDomainEnable()) {
            toggleFamilyMembersVisibility(true);
            ((GridLayoutManager) binding.contactsRecyclerView.getLayoutManager()).setSpanCount(staffMembers.size());
            mRAFamily.updateFamilyMembers(staffMembers);
        } else if (domainId.equalsIgnoreCase(Constants.PARK_ROYAL_DOMAIN_ID) && staffMembers != null && !staffMembers.isEmpty()) {
//            Picasso.get().load(staffMembers.get(0).getImageUrl()).centerCrop().into(binding.ivFirstLink);
            picasso
                    .load(staffMembers.get(0).getImageUrl())
                    .placeholder(R.drawable.ic_contact_default_48dp)
                    .fit()
                    .transform(new CircleImageTransform())
                    .into(binding.ivFirstLink);
            binding.txtFirstLink.setText(staffMembers.get(0).getName());
        }
    }

    HotelDetailsData mHotelDetailsData = null;

    @Override
    public void onSIPCredentialFetch() {
        boolean isLinphoneEnabled = WooHooApp.getSharedPrefsHelper()
                .get(SharedPrefsKey.PREF_KEY_FEATURE_LINPHONE, false);
        if (isLinphoneEnabled) {
            getParentActivity().initSipRegistration();
        } else {
            LoggerWrapper.i(HOME_TAG, "Skipping SIP auto-registration because Linphone feature is disabled");
        }
    }

    @Override
    public void setHotelDetails(HotelDetailsData hotelDetails) {

        mHotelDetailsData = hotelDetails;

        if (hotelDetails != null && hotelDetails.getLogoUrl() != null && !hotelDetails.getLogoUrl().equals("")) {
            applyScreenColor(hotelDetails.getScreenColor());
            if (hotelDetails.getLogoUrl() != null && !hotelDetails.getLogoUrl().equals("")) {
                String lang = getLanguage(getActivity());
                if (domainId.equalsIgnoreCase(HOLIDAY_INN_DOMAIN_ID) && Constants.LANGUAGE.ARABIC.equalsIgnoreCase(lang)) {
                    picasso.load(R.drawable.img_visit_saudi_logo_lang_ar)
                            .into(binding.hotelLogo);
                } else {
                    picasso.load(hotelDetails.getLogoUrl())
                            .into(binding.hotelLogo);
                }
                ((MainActivity) requireActivity()).setHotelLogo(hotelDetails.getLogoUrl());
            }
            if (hotelDetails.getBackgroundImageUrl() != null && !hotelDetails.getBackgroundImageUrl().equals("")) {
                if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                    picasso.load(hotelDetails.getBackgroundImageUrl())
                                .into(binding.hotelImage);

                } else {
                    leftPaneFragment = HomeLeftPaneFragment.newInstance(hotelDetails.getBackgroundImageUrl());
                    setLeftPaneFragment();
                }
            }
            binding.hotelLocation.setText(toTitleCase(hotelDetails.getLocation()));
            binding.hotelName.setText(hotelDetails.getName());
            if (domainId.equalsIgnoreCase(Constants.RC_DOMAIN_ID)) {
                binding.hotelName.setVisibility(View.GONE);
                binding.hotelLocation.setVisibility(View.GONE);
            }

            binding.orbAnimation.setVisibility(View.GONE);
            pauseOrbAnimation();
            binding.hotelDetails.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(hotelDetails.getTimeZone())) {
                String[] timezoneSplitArray = hotelDetails.getTimeZone().split(" ");
                if (timezoneSplitArray.length > 0) {
                    String timezoneId = timezoneSplitArray[0];
//                    TimeZoneUtils.getInstance(getActivity()).setTimeZoneToDefault(TimeZoneUtils.getInstance(getActivity()).getTimeZoneFromId(timezoneId.trim()));
//                    WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_TIME_ZONE, timezoneId.trim());
                    setDate();
                }
            }
            WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_DARK_MODE_START_POINT, hotelDetails.getNightModeStartTime());
            WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_DARK_MODE_END_POINT, hotelDetails.getNightModeEndTime());

        } else {
            applyScreenColor("");
            binding.hotelDetails.setVisibility(View.GONE);
            binding.orbAnimation.setVisibility(View.VISIBLE);
            playOrbAnimation();
        }
    }

    private void applyScreenColor(String screenColor) {
        if (binding == null || binding.relaivelayout == null || getParentActivity() == null) {
            return;
        }
        if (TextUtils.isEmpty(screenColor)) {
            WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_SCREEN_COLOR, "");
            binding.relaivelayout.setBackgroundColor(MethodUtils.getColorFromTheme(getParentActivity(), R.attr.color_primary));
            return;
        }
        try {
            String normalizedColor = screenColor.trim();
            binding.relaivelayout.setBackgroundColor(Color.parseColor(normalizedColor));
            WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_SCREEN_COLOR, normalizedColor);
        } catch (IllegalArgumentException e) {
            LoggerWrapper.e(HOME_TAG, "Invalid screen color: " + screenColor);
        }
    }

    public void setLeftPaneFragment() {
        if (leftPaneFragment != null) {
            getParentActivity().setLeftPane(leftPaneFragment);
        }
    }

    private String toTitleCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < arr.length; i++) {
            if (arr[i].length() <= 1) {
                sb.append(arr[i]);
            } else {
                sb.append(Character.toUpperCase(arr[i].charAt(0))).append(arr[i].substring(1)).append(" ");
            }
        }

        return sb.toString().trim();
    }

    @Override
    public void showFamilyMemberNotFound() {
//        toggleFamilyMembersVisibility(false);
        mRAFamily.updateFamilyMembers(null);
    }

    private void toggleFamilyMembersVisibility(boolean isFamilyMemberToBeVisible) {
        if (isFamilyMemberToBeVisible) {
            binding.contactsRecyclerView.setVisibility(View.VISIBLE);
            binding.fragmentContactsTextViewNoFamilyMembers.setVisibility(View.GONE);
        } else {
            binding.contactsRecyclerView.setVisibility(View.GONE);
            binding.fragmentContactsTextViewNoFamilyMembers.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAPIError(String errorMessage) {
        String message = errorMessage;
        if (message == null || message.trim().isEmpty() || message.toLowerCase().contains("timeout")) {
            message = getString(R.string.try_again);
        }
        MethodUtils.showToastMessage(getActivity(), message);
    }
    private void setUpOrbAnimation() {
        int resourceId = R.drawable.orb_animation_home;

        controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse("res:///" + resourceId))
                .setAutoPlayAnimations(true)
                .build();
        binding.orbAnimation.setController(controller);


    }

    @Override
    public void onBackPressed(BackCallback backCallback) {
    }

    private void setClickEvents() {
        binding.llFirstLink.setOnClickListener(view -> quickLinkFirstItemClick());
        binding.llSecondLink.setOnClickListener(view -> quickLinkSecondItemClick());
        binding.llThirdLink.setOnClickListener(view -> quickLinkThirdItemClick());
        binding.llThirdLink.setOnLongClickListener(view -> quickLinkThirdItemLongClick());
        binding.llForthLink.setOnClickListener(view -> quickLinkFourthItemClick());
        binding.llFifthLink.setOnClickListener(view -> quickLinkFifthItemClick());
        binding.relaivelayout.setOnClickListener(view -> onViewGroupClicked());
        binding.llLangChange.setOnClickListener(view -> onLangClick());
    }

    void quickLinkFirstItemClick() {
        helper.track(CustomEvents.QUICK_LINKS_FNB, null);
        if (domainId.equalsIgnoreCase(Constants.RC_DOMAIN_ID)) {
            getParentActivity().switchFragmentUsingAdd(OverviewCategoriesFragment.newInstance());
        } else if (domainId.equalsIgnoreCase(Constants.GBTB_DOMAIN_ID)) {
            getParentActivity().switchFragmentUsingAdd(QRCodeFragment.newInstance(getString(R.string.whatson), getString(R.string.scan_qr_for_whats_on), R.drawable.whatson_qr_new));
        } else if (domainId.equalsIgnoreCase(Constants.CKS_DOMAIN_MOCK_ROOM_ID) ||
                domainId.equalsIgnoreCase(Constants.REGENT_DOMAIN_ID) ||
                domainId.equalsIgnoreCase(Constants.CKS_DOMAIN_USER_ID) ||
                domainId.equalsIgnoreCase(POSTILLION_DOMAIN_ID)) {
            FeedbackFragment fragment = new FeedbackFragment();
            getParentActivity().switchFragmentUsingAdd(fragment);
        } else if (domainId.equalsIgnoreCase(Constants.PARK_ROYAL_DOMAIN_ID)) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(KEY_CONTACT_INFO, convertToContactInfo(staffMemberList.get(0)));
            getParentActivity().switchFragmentUsingAdd(ContactsProfileFragment.newInstance(bundle));
        } else {
            helper.trackFnb(new FnbEvent.FnbClicked());
            FnBCategoriesFragment fragment = new FnBCategoriesFragment();
            getParentActivity().switchFragmentUsingAdd(fragment);
        }
    }

    void quickLinkSecondItemClick() {
        if (domainId.equalsIgnoreCase(Constants.GBTB_DOMAIN_ID)) {
            getParentActivity().switchFragmentUsingAdd(QRCodeFragment.newInstance(getString(R.string.getting_around_quote), getString(R.string.scan_qr_for_getting_around), R.drawable.getting_around_qr_new));
        } else if (domainId.equalsIgnoreCase(Constants.CKS_DOMAIN_MOCK_ROOM_ID) ||
                domainId.equalsIgnoreCase(Constants.REGENT_DOMAIN_ID) ||
                domainId.equalsIgnoreCase(Constants.CKS_DOMAIN_USER_ID) ||
                domainId.equalsIgnoreCase(POSTILLION_DOMAIN_ID)) {
            OverviewCategoriesFragment fragment = OverviewCategoriesFragment.newInstance();
            getParentActivity().switchFragmentUsingAdd(fragment);
        } else if (domainId.equalsIgnoreCase(Constants.PARK_ROYAL_DOMAIN_ID)) {
            FeedbackFragment fragment = new FeedbackFragment();
            getParentActivity().switchFragmentUsingAdd(fragment);
        } else {
            helper.track(CustomEvents.QUICK_LINKS_FEEDBACK, null);
            FeedbackFragment fragment = new FeedbackFragment();
            getParentActivity().switchFragmentUsingAdd(fragment);
        }
    }

    boolean quickLinkThirdItemLongClick() {
        if (domainId.equalsIgnoreCase(POSTILLION_DOMAIN_ID)) {
            getParentActivity().goToBluetooth();
        }
        return true;
    }

    void quickLinkThirdItemClick() {
        helper.track(CustomEvents.QUICK_LINKS_QRCODE, null);
        if (domainId.equalsIgnoreCase(Constants.GBTB_DOMAIN_ID)) {
            Bundle bundle = new Bundle();
            bundle.putString("category_id", "7");
            bundle.putString("category_name", "Dining");
            bundle.putString("from_where", "Dining");
            OverviewSubCategoriesFragment overviewSubCategoriesFragment = OverviewSubCategoriesFragment.newInstance();
            overviewSubCategoriesFragment.setArguments(bundle);
            getParentActivity().switchFragmentUsingAdd(overviewSubCategoriesFragment);
        } else if (domainId.equalsIgnoreCase(Constants.CKS_DOMAIN_MOCK_ROOM_ID)) {
            getParentActivity().switchFragmentUsingAdd(DiningRestaurantsFragment.newInstance());
        } else if (domainId.equalsIgnoreCase(Constants.CKS_DOMAIN_USER_ID) ||
                domainId.equalsIgnoreCase(Constants.PARK_ROYAL_DOMAIN_ID)) {
            getParentActivity().switchFragmentUsingAdd(ReservationFragment.newInstance(null));
        } else if (domainId.equalsIgnoreCase(Constants.REGENT_DOMAIN_ID)) {
            getParentActivity().switchFragmentUsingAdd(ReservationFragment.newInstance(null));
        } else if (domainId.equalsIgnoreCase(Constants.RC_DOMAIN_ID)) {
            Bundle bundle = new Bundle();
            bundle.putString("category_id", "71");
            bundle.putBoolean("category_sub_type", true);
            FnBSubCategoriesFragment fnBSubCategoriesFragment = FnBSubCategoriesFragment.newInstance();
            fnBSubCategoriesFragment.setArguments(bundle);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                FnBLeftPaneFragment fnBLeftPaneFragment = FnBLeftPaneFragment.getInstance(FnBSubCategoriesFragment.class.getSimpleName());
                getParentActivity().switchFragmentUsingAdd(fnBLeftPaneFragment, fnBSubCategoriesFragment);
            } else {
                getParentActivity().switchFragmentUsingAdd(fnBSubCategoriesFragment);
            }
        } else if (domainId.equalsIgnoreCase(POSTILLION_DOMAIN_ID)) {
            getParentActivity().goToBluetooth();
        } else {
            Toast.makeText(getParentActivity(), getString(R.string.book_service_not_available), Toast.LENGTH_SHORT).show();
        }
    }

    void quickLinkFourthItemClick() {
        helper.track(CustomEvents.QUICK_LINKS_NIGHT_MODE, null);
        if (domainId.equalsIgnoreCase(Constants.GBTB_DOMAIN_ID)) {
            getParentActivity().switchFragmentUsingAdd(WeatherFragment.newInstance(null));
        } else if (domainId.equalsIgnoreCase(Constants.REGENT_DOMAIN_ID)) {
            getParentActivity().switchFragmentUsingAdd(NewsHeadlineFragment.newInstance(null));
        } else if (domainId.equalsIgnoreCase(Constants.CKS_DOMAIN_MOCK_ROOM_ID) ||
                domainId.equalsIgnoreCase(Constants.CKS_DOMAIN_USER_ID) ||
                domainId.equalsIgnoreCase(POSTILLION_DOMAIN_ID)) {
            getParentActivity().darkModeOptions();
        } else if (domainId.equalsIgnoreCase(Constants.PARK_ROYAL_DOMAIN_ID)) {
            getParentActivity().switchFragmentUsingAdd(OverviewCategoriesFragment.newInstance());
        } else {
            getParentActivity().darkModeOptions();
        }
    }

    void quickLinkFifthItemClick() {
        helper.track(CustomEvents.QUICK_LINKS_RECEPTION, null);
        if (domainId.equalsIgnoreCase(Constants.REGENT_DOMAIN_ID)) {
            getParentActivity().switchFragmentUsingAdd(WeatherFragment.newInstance(null));
        } else if (domainId.equalsIgnoreCase(Constants.CKS_DOMAIN_MOCK_ROOM_ID) ||
                domainId.equalsIgnoreCase(Constants.CKS_DOMAIN_USER_ID) ||
                domainId.equalsIgnoreCase(POSTILLION_DOMAIN_ID)) {
            onMutePressedAction();
            setMute();
            setMuteIconForQuickLink();

        } else if (domainId.equalsIgnoreCase(Constants.PARK_ROYAL_DOMAIN_ID)) {
            getParentActivity().switchFragmentUsingAdd(WebViewDefaultFragment.newInstance(CHROME_UNTRUSTED_URL, WebViewDefaultFragment.WebviewLaunchMode.Default));
        } else if (domainId.equalsIgnoreCase(Constants.RC_DOMAIN_ID)) {
            getParentActivity().switchFragmentUsingAdd(WeatherFragment.newInstance(null));
        } else {
            String receptionData = "{\"id\": 232,\"name\": \"Reception\",\"email\": \"hello\",\"phoneNumber\": \"919114895b573d80\",\"imageUrl\": \"https://s3-us-west-2.amazonaws.com/woohoo-enterprise/propertyContacts/amadeus_reception.jpeg\",\"userId\": 344,\"hub\": {\"id\": \"919114895b573d80\",\"name\": \"Room 101\",\"userId\": 344,\"connected\": false,\"isBusy\": \"false\",\"surveillanceMode\": false,\"liveFeedAccess\": true}}";
            StaffMember member = new Gson().fromJson(receptionData, StaffMember.class);
            onFamilyMemberClicked(member);
        }
    }

    // Sets quick links mute icon
    public void setMuteForQuickLink() {
        if (domainId.equalsIgnoreCase(Constants.CKS_DOMAIN_MOCK_ROOM_ID) ||
                domainId.equalsIgnoreCase(Constants.CKS_DOMAIN_USER_ID) ||
                domainId.equalsIgnoreCase(POSTILLION_DOMAIN_ID) ||
                domainId.equals(Constants.REGENT_DOMAIN_ID)) {
            setMuteIconForQuickLink();
        }
    }

    private void setMuteIconForQuickLink() {
        binding.txtFifthLink.setTextColor(getResources().getColor(R.color.white));
        if (WooHooApp.getSharedPrefsHelper().getMuteMode(PREF_MUTE_STATE)) {
            binding.imgFifthLink.setImageResource(R.drawable.ic_drawable_quick_link_unmute);
            binding.txtFifthLink.setText(getString(R.string.unmute));
        } else {
            binding.imgFifthLink.setImageResource(R.drawable.ic_drawable_quick_link_mute);
            binding.txtFifthLink.setText(getString(R.string.mute));
        }
    }

//    @OnClick(R.id.fragment_home_relative_layout_spotify)
//    void onSpotifyClicked() {
//        getParentActivity().closeSlidingTray();
//        MusicFragment fragment = new MusicFragment();
//        getParentActivity().switchFragmentUsingAdd(fragment);
//    }
//
////    @OnClick(R.id.devices)
////    void onDevicesClicked() {
////        getParentActivity().closeSlidingTray();
////        DeviceLauncherFragment fragment = new DeviceLauncherFragment();
////        getParentActivity().switchFragmentUsingAdd(fragment);
////    }
//
//    @OnClick(R.id.fragment_home_linear_layout_amadeus)
//    void onWeatherClicked() {
//        getParentActivity().closeSlidingTray();
//        getParentActivity().switchFragmentUsingAdd(AmadeusServicesFragment.newInstance());
//
//    }
//
//
//    @OnClick(R.id.fragment_home_linear_interel)
//    void onInterelClicked() {
//        getParentActivity().closeSlidingTray();
//        getParentActivity().switchFragmentUsingAdd(InterelUtilityFragment.newInstance());
////        NewsHeadlineFragment fragment=new NewsHeadlineFragment();
////        getParentActivity().switchFragmentUsingAdd(fragment);
//
//    }

    void onViewGroupClicked() {
        getParentActivity().closeSlidingTray();
    }

    public void registerBroadcasts() {
        unRegisterBroadcasts();
//        mPresenter.registerBroadcastReceivers();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED), Context.RECEIVER_NOT_EXPORTED);
        } else {
            requireActivity().registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }

    }

    public void unRegisterBroadcasts() {
//        mPresenter.unRegisterBroadcastReceivers();
        /*getParentActivity().unregisterReceiver(batteryReceiver);*/

        try {
            if (batteryReceiver != null && batteryReceiver.isOrderedBroadcast()) {
                batteryReceiver.abortBroadcast();
                batteryReceiver.clearAbortBroadcast();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }


    public void setDate() {

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a, dd MMM yyyy", Locale.getDefault());
        String formattedDateTime = dateFormat.format(currentDate);
        binding.fragmentHomeTextViewDate.setText(formattedDateTime);
        getParentActivity().checkDateRangeForDarkMode(currentDate);
        runDateThread();
    }

    public void setMute() {
        getParentActivity().setMute(VISIBLE);
    }

    public void onMutePressedAction() {
        getParentActivity().onMutePressedAction();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterBroadcasts();
        featureFragmentCleanUps();
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        mPresenter.presenterCleanUps();
    }

    public void playOrbAnimation() {
        if (controller != null && controller.getAnimatable() != null) {
            controller.getAnimatable().start();
        }
    }

    public void pauseOrbAnimation() {
        if (controller != null && controller.getAnimatable() != null) {
            controller.getAnimatable().stop();
        }
    }

    @Override
    public void onAppSelected(DrawerGridItems drawerGridItems) {
        getParentActivity().closeSlidingTray();
        getParentActivity().fragmentSwitch(drawerGridItems.getTitle());
    }

    @Override
    public void onPause() {
        handler.removeCallbacks(dateThreadRunnable);
        super.onPause();
    }

//    @Override
//    public void onTopListChange(List<DrawerGridItems> listSource) {
//        updateAppList(listSource);
//    }
//
//    @Override
//    public void onBottomListChange(List<DrawerGridItems> listSource) {
//        favAppsHomeAdapter.notifyDataSetChanged();
//    }
//
//    public void updateAppList(List<DrawerGridItems> itemsList) {
//        updateList(itemsList);
//    }
}
