package com.sb.woohoo.ui.main.fragments.settings.wifi;

import static com.sileria.android.Kit.getSystemService;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.custom.WifiDialogBox;
import com.sb.woohoo.databinding.FragmentWifiSettingsBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.NonPopFragment;
import com.sb.woohoo.ui.main.fragments.settings.SettingsFragment;
import com.sb.woohoo.ui.main.fragments.settings.wifi.di.DaggerWifiComponent;
import com.sb.woohoo.ui.main.fragments.settings.wifi.di.WifiComponent;
import com.sb.woohoo.ui.main.fragments.settings.wifi.di.WifiModule;
import com.sb.woohoo.ui.main.fragments.settings.wifi.pojo.WifiNetworkDetails;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.onboarding.OnboardingActivity;
import com.sb.woohoo.ui.onboarding.OnboardingViewPagerAdapter;
import com.sb.woohoo.utils.others.MethodUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;


public class WifiFragment extends FeatureFragment<MainActivity> implements
        WifiContract.Views, WifiAdapter.onItemClickListener, NonPopFragment , CompoundButton.OnCheckedChangeListener
{

    public static final int ONBOARDING = 1;
    public static final int MAINACTIVITY = 2;
    public static final int ERROR_LOADING = 3;
    private final long AUTO_SCAN_DELAY = 1000 * 10;
    private final int AUTHENTICATION_RETRY = 10;
    private static final String ACTIVITY_TYPE = "activity_type";
    private final String TAG = WifiFragment.class.getSimpleName();

    private final int ACCESS_WIFI_STATE_PERMISSION = 100;
    private final String[] permissionsRequired = new String[]{
            Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Inject
    WifiManager mainWifi;
    @Inject
    WifiPresenter wifiPresenter;
    @Inject
    WifiAdapter wifiAdapter;
    @Inject
    AnalyticsHelper helper;
    private boolean isPermissionGranted = false;
    private ArrayList<WifiNetworkDetails> networkDetailsList = new ArrayList<>(0);

    private String selectedNetwork;

    private WifiNetworkDetails selectedWifiDetails;

    private WifiDialogBox dialog;

    private String connectedNetwork = null;

    private int retry;

    private boolean isAuthenticationFailed = false;

    private boolean isPasswordVisible = true;

    private int type;
    private FragmentWifiSettingsBinding binding;

    private String[] getRequiredPermissions() {

        ArrayList<String> list  = new ArrayList(Arrays.asList(permissionsRequired));

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(!statusOfGPS) {
            list.remove(Manifest.permission.ACCESS_FINE_LOCATION);
            list.remove(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        String[] permissionArray = new String[list.size()];
        permissionArray = list.toArray(permissionArray);
        return permissionArray;

    }

    private Handler handler = new Handler();
    private BroadcastReceiver wifiConnectionStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LoggerWrapper.d(TAG, "Inside wifiConnectionStateReceiver onReceive");
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            //android-9 onwards intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO) will return null.

            //To over come the Loop
            //Setting the CheckedChangeListener null first
            binding.wifiSwitch.setOnCheckedChangeListener(null);
            binding.wifiSwitch.setChecked(wifiManager.isWifiEnabled());
            binding.wifiSwitch.setOnCheckedChangeListener(WifiFragment.this);

            WifiInfo networkInfo =  wifiManager.getConnectionInfo();
            boolean isSelectedNetworkChange = selectedWifiDetails!= null &&
                    selectedWifiDetails.getWifiNetwork().SSID.equals(
                            wifiManager.getConnectionInfo().getSSID().replace("\"", ""));

            if(networkInfo == null ){
                LoggerWrapper.e(TAG, "Inside networkInfo == null");
                return;
            }

            if (dialog.isShowing() && isSelectedNetworkChange)
            {
                if (retry == 0) {
                    dialog.binding.connectingStatus.setText(requireContext().getString(R.string.connecting));
                }
                if (retry < AUTHENTICATION_RETRY) {
                    if (
                            networkInfo.getSupplicantState() == SupplicantState.FOUR_WAY_HANDSHAKE ||
                                    networkInfo.getSupplicantState() == SupplicantState.GROUP_HANDSHAKE
                    ) {
                        retry++;
                        dialog.binding.connectingStatus.setText(requireContext().getString(R.string.authenticating));
                    } else if (networkInfo.getSupplicantState() == SupplicantState.ASSOCIATING ||
                            networkInfo.getSupplicantState() == SupplicantState.ASSOCIATED ||
                            networkInfo.getSupplicantState() == SupplicantState.AUTHENTICATING
                    ) {
                        dialog.binding.connectingStatus.setText(requireContext().getString(R.string.connecting));
                    } else if (networkInfo.getSupplicantState() == SupplicantState.SCANNING) {
                        dialog.binding.connectingStatus.setText(requireContext().getString(R.string.scanning));
                    } else if (networkInfo.getSupplicantState() == SupplicantState.INVALID ||
                            networkInfo.getSupplicantState() == SupplicantState.DISCONNECTED ||
                            networkInfo.getSupplicantState() == SupplicantState.INTERFACE_DISABLED ||
                            networkInfo.getSupplicantState() == SupplicantState.INACTIVE) {
                        wifiPresenter.removeNetwork(selectedNetwork);
                        retryView();
                        dialog.binding.connectingStatus.setText(requireContext().getString(R.string.connection_failed));
                    } else if (networkInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                        if (type == ONBOARDING) {
                            dialog.dismiss();
                            goToNextPage();
                        } else {
                            dialog.dismiss();
                        }
                    }
                } else {
                    wifiPresenter.removeNetwork(selectedNetwork);
                    retryView();
                    dialog.binding.connectingStatus.setText(requireContext().getString(R.string.authentication_problem));
                }
            } else if (networkInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                LoggerWrapper.d(TAG, "Inside wifiConnectionStateReceiver networkInfo.getSupplicantState()");
                connectedNetwork = networkInfo.getSSID().replaceAll("\"", "");
                if (networkDetailsList != null && !networkDetailsList.isEmpty()) {
                    LoggerWrapper.e(TAG, "Inside wifiConnectionStateReceiver networkDetailsList != null");
                    updateWifiList(connectedNetwork);
                }else{
                    LoggerWrapper.e(TAG, "Inside wifiConnectionStateReceiver networkDetailsList == null");
                    //UnRegister WIFI scan receiver abd re register
                    requireActivity().unregisterReceiver(wifiScanReceiver);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        requireActivity().registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION), Context.RECEIVER_NOT_EXPORTED);
                    } else {
                        requireActivity().registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                    }
                    wifiManager.startScan();
                }
            } else if (networkInfo.getSupplicantState() == SupplicantState.DISCONNECTED) {
                LoggerWrapper.e(TAG, "Inside wifiConnectionStateReceiver DISCONNECTED ");
                if(networkDetailsList != null && !networkDetailsList.isEmpty()) {
                    networkDetailsList.get(0).setConnected(false);
                    networkDetailsList.clear();
                    wifiAdapter.updateList(networkDetailsList);
                }
                connectedNetwork = null;
            }else{
                LoggerWrapper.e(TAG, "Inside wifiConnectionStateReceiver ELSE ");
                if(!wifiManager.isWifiEnabled()){
                    connectedNetwork = null;
                    if(networkDetailsList != null){
                        networkDetailsList.clear();
                        wifiAdapter.updateList(networkDetailsList);
                    }
                }
            }
            updateSettingsUI();
        }
    };

    private final Runnable stopScanAnimation = new Runnable() {
        @Override
        public void run() {
            binding.reload.clearAnimation();
        }
    };
    private BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LoggerWrapper.d(TAG, "Inside onReceive");
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            binding.loadingPlaceHolder.shimmerViewContainer.setVisibility(View.GONE);
            binding.loadingPlaceHolder.shimmerViewContainer.stopShimmer();

            //If the WIFI is disable, no need to do the below process bcz we hide the data ..
            if(!wifiManager.isWifiEnabled()){
                if(networkDetailsList != null){
                    networkDetailsList.clear();
                }
                return;
            }

            // Update wifi list
            binding.reload.startAnimation(
                    AnimationUtils.loadAnimation(context, R.anim.rotate));
            setStopScanAnimation();
            if (Objects.equals(intent.getAction(), WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
            {
                LoggerWrapper.d(TAG, "Inside onReceive Objects.equals ");
                networkDetailsList.clear();
                networkDetailsList.addAll(wifiPresenter.configureNetworks(
                        wifiPresenter.getWifiList(), wifiPresenter.getConfiguredNetworks()));
               // LoggerWrapper.d(TAG, "Inside onReceive networkDetailsList : "+networkDetailsList.size());
                String lConnectedSSID = wifiManager.getConnectionInfo().getSSID();
                lConnectedSSID = lConnectedSSID.replace("\"", "");
                //LoggerWrapper.d(TAG, "Inside onReceive wifiManager lConnectedSSID ==  "+lConnectedSSID);
                List<WifiNetworkDetails> lEmptyList = new ArrayList<>();
                boolean lFinished = false;
                for (int i = 0; i < networkDetailsList.size(); i++)
                {
                    //Hiding the empty names from list
                    if (networkDetailsList.get(i).getWifiNetwork().SSID == null
                            || networkDetailsList.get(i).getWifiNetwork().SSID.isEmpty())
                    {
                        lEmptyList.add(networkDetailsList.get(i));
                    }

                   // LoggerWrapper.d(TAG, "Inside onReceive LOOP  : " + networkDetailsList.get(i).getWifiNetwork().SSID);
                   // LoggerWrapper.d(TAG, "Inside onReceive SSID connected : " + networkDetailsList.get(i).isConnected());

                    //Setting the connected item and moving it to top.
                    if (!lFinished && networkDetailsList.get(i).getWifiNetwork().SSID.equals(lConnectedSSID))
                    {
                        //LoggerWrapper.e(TAG, "Inside onReceive SSID == lConnectedSSID");
                        WifiNetworkDetails wifiNetworkDetails = networkDetailsList.get(i);
                        networkDetailsList.remove(i);
                        networkDetailsList.add(0, wifiNetworkDetails);
                        networkDetailsList.get(0).setConnected(true);
                        lFinished = true;
                    }
                }
                networkDetailsList.removeAll(lEmptyList);
                wifiAdapter.updateList(networkDetailsList);
            }else{
                LoggerWrapper.e(TAG, "Inside onReceive  Objects not equals ");
            }
        }
    };

    public WifiFragment() {
        // Required empty public constructor
    }

    public static WifiFragment newInstance(int activityType) {
        Bundle args = new Bundle();
        args.putInt(ACTIVITY_TYPE, activityType);
        WifiFragment fragment = new WifiFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void updateSettingsUI() {
        if (type == MAINACTIVITY) {
            FragmentManager fragmentManager = getParentActivity().getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.findFragmentByTag("SettingsFragment");
            if (currentFragment instanceof SettingsFragment) {
                ((SettingsFragment) currentFragment).onWifiNetworkChanged();
            }
        }
    }

    private void updateWifiList(String connectedWifi)
    {
        for (int i = 0; i < networkDetailsList.size(); i++) {
            networkDetailsList.get(0).setConnected(false);
            if (networkDetailsList.get(i).getWifiNetwork().SSID.equals(connectedWifi)) {
                networkDetailsList.get(i).setConnected(true);
                WifiNetworkDetails wifiNetworkDetails = networkDetailsList.get(i);
                networkDetailsList.remove(i);
                networkDetailsList.add(0, wifiNetworkDetails);
                break;
            }
        }
        wifiAdapter.updateList(networkDetailsList);
        binding.listWifi.smoothScrollToPosition(0);
    }

    private void connectView() {
        dialog.binding.connectingStatus.setVisibility(View.VISIBLE);
        dialog.binding.connectingStatus.setText(getString(R.string.enter_wifi_pass));
        dialog.binding.textPositive.setEnabled(false);
        dialog.binding.textPositive.setAlpha(0.5f);
        dialog.binding.editPassword.setBackgroundResource(R.drawable.bg_stroke_edit_text);
        dialog.binding.wrongPasswordAlert.setVisibility(View.GONE);
        dialog.binding.editPassword.setVisibility(View.VISIBLE);
        dialog.binding.textPositive.setVisibility(View.VISIBLE);
        dialog.binding.buttonCancel.setVisibility(View.VISIBLE);
        dialog.binding.forget.setVisibility(View.GONE);
        dialog.binding.buttonConnectingCancel.setVisibility(View.GONE);
        dialog.binding.passwordToggleButton.setVisibility(View.VISIBLE);
    }

    private void cancelView() {
        MethodUtils.hideSoftInputKeyboard(dialog.binding.editPassword, getContext());
        dialog.binding.editPassword.setBackgroundResource(R.drawable.bg_stroke_edit_text);
        dialog.binding.connectingStatus.setVisibility(View.VISIBLE);
        dialog.binding.editPassword.setVisibility(View.GONE);
        dialog.binding.textPositive.setVisibility(View.GONE);
        dialog.binding.buttonCancel.setVisibility(View.GONE);
        dialog.binding.forget.setVisibility(View.GONE);
        dialog.binding.buttonConnectingCancel.setVisibility(View.VISIBLE);
        dialog.binding.passwordToggleButton.setVisibility(View.GONE);
        dialog.binding.wrongPasswordAlert.setVisibility(View.GONE);
    }

    private void retryView() {

        dialog.binding.editPassword.setBackgroundResource(R.drawable.bg_wrong_password);
        dialog.binding.wrongPasswordAlert.setVisibility(View.VISIBLE);
        dialog.binding.connectingStatus.setVisibility(View.VISIBLE);
        dialog.binding.connectingStatus.setText(getString(R.string.enter_wifi_pass));
        dialog.binding.editPassword.setVisibility(View.VISIBLE);
        dialog.binding.textPositive.setVisibility(View.VISIBLE);
        dialog.binding.buttonCancel.setVisibility(View.VISIBLE);
        dialog.binding.forget.setVisibility(View.GONE);
        dialog.binding.buttonConnectingCancel.setVisibility(View.GONE);
        dialog.binding.passwordToggleButton.setVisibility(View.VISIBLE);
        isAuthenticationFailed = true;
    }

    private void forgetView() {

        dialog.binding.editPassword.setBackgroundResource(R.drawable.bg_stroke_edit_text);
        dialog.binding.connectingStatus.setVisibility(View.VISIBLE);
        dialog.binding.connectingStatus.setText(getString(R.string.forget_nwtwork));
        dialog.binding.editPassword.setVisibility(View.GONE);
        dialog.binding.textPositive.setVisibility(View.GONE);
        dialog.binding.buttonCancel.setVisibility(View.VISIBLE);
        dialog.binding.forget.setVisibility(View.VISIBLE);
        dialog.binding.buttonConnectingCancel.setVisibility(View.GONE);
        dialog.binding.passwordToggleButton.setVisibility(View.GONE);
        dialog.binding.wrongPasswordAlert.setVisibility(View.GONE);
    }

    private void openView() {
        dialog.binding.connectingStatus.setText(getString(R.string.connect_to_network));
        dialog.binding.textPositive.setEnabled(true);
        dialog.binding.textPositive.setAlpha(1.0f);
        dialog.binding.connectingStatus.setVisibility(View.VISIBLE);
        dialog.binding.editPassword.setVisibility(View.GONE);
        dialog.binding.textPositive.setVisibility(View.VISIBLE);
        dialog.binding.buttonCancel.setVisibility(View.VISIBLE);
        dialog.binding.buttonConnectingCancel.setVisibility(View.GONE);
        dialog.binding.passwordToggleButton.setVisibility(View.GONE);
        dialog.binding.wrongPasswordAlert.setVisibility(View.GONE);
        dialog.binding.forget.setVisibility(View.GONE);
    }

    public void setUpWifi() {
        // Register receiver for wifi scan
        //LoggerWrapper.e(TAG, "Inside setUpWifi");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION), Context.RECEIVER_NOT_EXPORTED);
            requireActivity().registerReceiver(wifiConnectionStateReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION), Context.RECEIVER_NOT_EXPORTED);
        } else {
            requireActivity().registerReceiver(wifiConnectionStateReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
            requireActivity().registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        helper.track(new Screen.Wifi(),  null);
        binding = FragmentWifiSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        setUpWifi();
        super.onResume();
    }

    @Override
    public void onPause() {
        unRegister();
        super.onPause();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        WifiComponent wifiComponent = DaggerWifiComponent.builder()
                .wifiModule(new WifiModule(getLayoutInflater(), getActivity(), this))
                .build();

        wifiComponent.injectWifiSelectionFragment(this);
    }

    @Override
    public void bindViews(View view) {
        binding.buttonSkip.setOnClickListener(view1 -> onButtonSkip());
        binding.reload.setOnClickListener(view1 -> onRescanClicked());
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view1 -> onBackClick());
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //LoggerWrapper.e(TAG, "Inside onViewCreated");

        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getInt(ACTIVITY_TYPE);
        }

        if(!isGpsSupported()) {
            Toast.makeText(requireContext(),
                   requireContext().getString(R.string.wifi_gps_not_supported), Toast.LENGTH_LONG).show();
            /*Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 901);*/
        }
        checkWifiPermission();
        dialog = new WifiDialogBox(requireContext(), R.style.DialogTheme);
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.wifi_toolbar));
        initViews();
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    private void initViews()
    {
        //LoggerWrapper.e(TAG, "Inside initViews");
        binding.listWifi.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.listWifi.setEmptyView(binding.emptyView);
        binding.listWifi.setAdapter(wifiAdapter);

        WifiManager wifiManager = (WifiManager) requireActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null)
        {
            if (wifiManager.isWifiEnabled()) {
                //LoggerWrapper.e(TAG, "Inside WifiEnabled");
                binding.wifiSwitch.setChecked(true);
                setView(View.VISIBLE);
                binding.loadingPlaceHolder.shimmerViewContainer.setVisibility(View.VISIBLE);
                binding.loadingPlaceHolder.shimmerViewContainer.startShimmer();
                wifiPresenter.scanWifi();
                binding.reload.startAnimation(
                        AnimationUtils.loadAnimation(requireContext(), R.anim.rotate));
            } else {
                //LoggerWrapper.e(TAG, "Inside Disabled");
                binding.loadingPlaceHolder.shimmerViewContainer.setVisibility(View.GONE);
                binding.loadingPlaceHolder.shimmerViewContainer.stopShimmer();
                binding.wifiSwitch.setChecked(false);
                setView(View.GONE);
            }
        }

        binding.wifiSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                toggleWiFi(true);
                binding.loadingPlaceHolder.shimmerViewContainer.setVisibility(View.VISIBLE);
                binding.loadingPlaceHolder.shimmerViewContainer.startShimmer();
                setView(View.VISIBLE);
                wifiPresenter.scanWifi();
            } else {
                toggleWiFi(false);
                binding.loadingPlaceHolder.shimmerViewContainer.setVisibility(View.GONE);
                binding.loadingPlaceHolder.shimmerViewContainer.stopShimmer();
                if (networkDetailsList != null) {
                    networkDetailsList.clear();
                    wifiAdapter.updateList(networkDetailsList);
                }
                setView(View.GONE);
                connectedNetwork = null;
            }
        });

        if (type == ONBOARDING) {
            binding.buttonSkip.setVisibility(View.VISIBLE);
            binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setVisibility(View.GONE);
            binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setVisibility(View.GONE);
        } else {
            binding.buttonSkip.setVisibility(View.GONE);
            binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setVisibility(View.VISIBLE);
            binding.rootView.setWeightSum(9);
        }
    }

    private void setStopScanAnimation() {
        handler.removeCallbacks(stopScanAnimation);
        handler.postDelayed(stopScanAnimation, AUTO_SCAN_DELAY);
    }

    private void setView(int view) {
        binding.listWifi.setVisibility(view);
        binding.availableTextView.setVisibility(view);
        binding.reload.setVisibility(view);
        binding.emptyView.setVisibility(view);
    }

    private void toggleWiFi(boolean status) {
        WifiManager wifiManager = (WifiManager) requireActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            if (status && !wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            } else if (!status && wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
            }
        }
    }

    @Override
    public void onItemClick(WifiNetworkDetails selectedWifi) {
        WifiManager wifiManager = (WifiManager) requireContext().getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }
        selectedWifiDetails = selectedWifi;
        wifiPresenter.connectWifi(selectedWifi);
        selectedNetwork = selectedWifi.getWifiNetwork().SSID;
        retry = 0;
    }

    @Override
    public void getWifiPassword(final ScanResult selectedWifi, final PasswordCallback passwordCallback, final String securityType) {
        dialog.show();
        dialog.binding.editPassword.setText("");
        dialog.binding.networkName.setText(selectedWifi.SSID);

        if (!wifiPresenter.isNetworkDetailsSaved(selectedWifi.SSID)) {
            if (!selectedWifiDetails.isConnected() && !securityType.equals(WifiPresenter.OPEN)) {
                connectView();
            } else if (securityType.equals(WifiPresenter.OPEN) && !selectedWifiDetails.isConnected()) {
                openView();
            } else {
                forgetView();
            }
        }


        dialog.binding.editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 8) {
                    dialog.binding.textPositive.setEnabled(true);
                    dialog.binding.textPositive.setAlpha(1.0f);
                } else {
                    dialog.binding.textPositive.setEnabled(false);
                    dialog.binding.textPositive.setAlpha(0.5f);
                }

                if (isAuthenticationFailed) {
                    isAuthenticationFailed = false;
                    dialog.binding.editPassword.setBackgroundResource(R.drawable.bg_stroke_edit_text);
                }

                if (i != 0) {
                    dialog.binding.wrongPasswordAlert.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        dialog.binding.textPositive.setOnClickListener(view -> {
            networkDetailsList.get(0).setConnected(false);
            connectedNetwork = null;
            wifiAdapter.updateList(networkDetailsList);
            retry = 0;
            if (!(securityType.equals(WifiPresenter.OPEN))) {
                cancelView();
                wifiPresenter.disconnectCurrentNetwork();
                dialog.binding.connectingStatus.setText(getString(R.string.connecting));
                passwordCallback.onPasswordEntered(selectedWifi, dialog.binding.editPassword.getText().toString(), securityType);
                MethodUtils.hideSoftInputKeyboard(dialog.binding.editPassword, getContext());
            } else {
                cancelView();
                wifiPresenter.disconnectCurrentNetwork();
                passwordCallback.onPasswordEntered(selectedWifi, "", WifiPresenter.OPEN);
            }
        });

        dialog.binding.buttonConnectingCancel.setOnClickListener(view -> {
            MethodUtils.hideSoftInputKeyboard(dialog.binding.editPassword, getContext());
            dialog.dismiss();
        });

        dialog.binding.buttonCancel.setOnClickListener(view -> {
            MethodUtils.hideSoftInputKeyboard(dialog.binding.editPassword, getContext());
            hidePasswordTextDialog();
            dialog.dismiss();
        });

        dialog.binding.passwordToggleButton.setOnClickListener(view -> {
            if (isPasswordVisible) {
                hidePasswordTextDialog();
            } else {
                showPasswordTextDialog();

            }
        });

        dialog.binding.forget.setOnClickListener(view -> {
            retry = 0;
            connectedNetwork = null;
            wifiPresenter.removeNetwork(selectedNetwork);
            networkDetailsList.get(0).setConnected(false);
            wifiAdapter.updateList(networkDetailsList);
            dialog.dismiss();
        });

        if (wifiPresenter.isNetworkDetailsSaved(selectedWifi.SSID))
            dialog.binding.textPositive.performClick();
    }

    private void showPasswordTextDialog() {
        isPasswordVisible = true;
        dialog.binding.passwordToggleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_eye_hide_password));
        dialog.binding.editPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        dialog.binding.editPassword.setSelection(dialog.binding.editPassword.getText().length());
    }

    private void unRegister()
    {
        // Unregister receiver for wifi scan
        try {
            requireActivity().unregisterReceiver(wifiScanReceiver);
            requireActivity().unregisterReceiver(wifiConnectionStateReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void hidePasswordTextDialog() {
        isPasswordVisible = false;
        dialog.binding.passwordToggleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_eye_icon_show));
        dialog.binding.editPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        dialog.binding.editPassword.setSelection(dialog.binding.editPassword.getText().length());
    }

    private void checkWifiPermission() {
        isPermissionGranted = true;
       for( String permission : getRequiredPermissions()) {
           if (ActivityCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
               isPermissionGranted = false;
           }
       }
       /* if (Build.VERSION.SDK_INT >= 23)
        {
            if (ActivityCompat.checkSelfPermission(requireContext(), permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(requireContext(), permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(requireContext(), permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissionsRequired, ACCESS_WIFI_STATE_PERMISSION);
            } else {
                isPermissionGranted = true;
            }
        } else {
            if (ActivityCompat.checkSelfPermission(requireContext(), permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(requireContext(), permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissionsRequired, ACCESS_WIFI_STATE_PERMISSION);
            } else {
                isPermissionGranted = true;
            }
        }*/
        wifiPresenter.setUpWifi();
    }

    private void goToNextPage() {
        OnboardingActivity activity = ((OnboardingActivity) getActivity());
        if (activity != null) {
            //TODO TIMEZONE IN ONBOARDING HAS BEEN COMMENTED OUT, UNCOMMENT THE CODE TO ADD TIMEZONE IN ONBOARDING
            if (activity.isFactoryReset()) {
//                if (activity.isTimeZoneSet() && activity.isIsTimeZoneSet()) {
                activity.setCurrentItem(OnboardingViewPagerAdapter.FragmentOrder.FactoryReset.LOGIN, true);
//                } else {
//                    activity.setCurrentItem(OnboardingViewPagerAdapter.FragmentOrder.FactoryReset.TIME_ZONE, true);
//                }
            } else {
                activity.setCurrentItem(OnboardingViewPagerAdapter.FragmentOrder.LogOut.LOGIN, true);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LoggerWrapper.d(TAG, " WifiFrag XXXX ---- Inside onRequestPermissionsResult called");
        if (grantResults.length > 0 && requestCode == ACCESS_WIFI_STATE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            // Check if all permissions are granted
            isPermissionGranted = true;
            // TODO Handle Permission not granted
        }
        checkWifiPermission();
    }

    @Override
    public String createTag() {
        return TAG;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public WifiPresenter getWifiPresenter() {
        return wifiPresenter;
    }

    public void onBackClick() {
        if (type == MAINACTIVITY) {
            getParentActivity().onBackPressed();
        } else if (type == ERROR_LOADING) {
            getParentActivity().onNetworkConnected();
            getParentActivity().onBackPressed();
        }
    }


    void onRootView() {
    }

    void onRescanClicked() {
        wifiPresenter.scanWifi();
    }

    void onButtonSkip() {
        goToNextPage();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        if (isChecked)
        {
            toggleWiFi(true);
            //setUpWifi();
            binding.loadingPlaceHolder.shimmerViewContainer.setVisibility(View.VISIBLE);
            binding.loadingPlaceHolder.shimmerViewContainer.startShimmer();
            setView(View.VISIBLE);
            wifiPresenter.scanWifi();
        } else {
            toggleWiFi(false);
            binding.loadingPlaceHolder.shimmerViewContainer.setVisibility(View.GONE);
            binding.loadingPlaceHolder.shimmerViewContainer.stopShimmer();
            if (networkDetailsList != null) {
                networkDetailsList.clear();
                wifiAdapter.updateList(networkDetailsList);
            }
            binding.availableTextView.setVisibility(View.GONE);
            binding.reload.setVisibility(View.GONE);
            connectedNetwork = null;
            unRegister();
        }
    }

    public interface PasswordCallback {
        void onPasswordEntered(ScanResult selectedWifi, String password, String securityType);
    }
}
