package com.sb.woohoo.ui.main.fragments.bluetoothSigned;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.bluetooth.BluetoothAdapter.ACTION_SCAN_MODE_CHANGED;
import static android.bluetooth.BluetoothDevice.ACTION_BOND_STATE_CHANGED;
import static android.bluetooth.BluetoothDevice.ACTION_FOUND;
import static android.bluetooth.BluetoothDevice.BOND_BONDED;
import static android.bluetooth.BluetoothDevice.BOND_BONDING;
import static android.bluetooth.BluetoothDevice.BOND_NONE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.BuildConfig;
import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.custom.RequestPermissionHandler;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.FragmentBluetoothSettingsBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.NonPopFragment;
import com.sb.woohoo.ui.main.fragments.bluetoothSigned.di.BluetoothSignedSettingsComponent;
import com.sb.woohoo.ui.main.fragments.bluetoothSigned.di.BluetoothSignedSettingsModule;
import com.sb.woohoo.ui.main.fragments.bluetoothSigned.di.DaggerBluetoothSignedSettingsComponent;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.Header;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;
import com.sb.woohoo.utils.woohoblutooth.BluetoothBroadcastReceiver;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;


public class BluetoothSignedSettingsFragment extends FeatureFragment<MainActivity> implements RVBluetoothSignedAdapter.BluetoothDeviceClickListener, NonPopFragment, BluetoothBroadcastReceiver.Callback {

    private final static int TURN_ON_BLUETOOTH = 1;
    private final static int DISCOVERABLE_BLUETOOTH_DURATION = 60; // In seconds
    private static final long AUTO_SCAN_DELAY = 1000 * 10;
    private String TAG = BluetoothSignedSettingsFragment.class.getSimpleName();
    private CompoundButton.OnCheckedChangeListener blueListener;

    private static final int BLUETOOTH_VISIBILITY_DURATION = 60; // Duration in seconds

    private static CountDownTimer visibilityTimer;
    @Inject
    RVBluetoothSignedAdapter rvBluetoothSignedAdapter;
    @Inject
    AnalyticsHelper helper;

    private FragmentBluetoothSettingsBinding binding;

    private List listOfBluetoothDevices = new ArrayList(0);

    private Handler handler = new Handler();
    private BluetoothBroadcastReceiver mBluetoothBroadcastReceiver;

    private static final int REQUEST_PERMISSION = 6001;

    private static final String[] ANDROID_12_BLE_PERMISSIONS = new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT};

    /**
     * Broadcast Receiver that detects when other devices are available and checks for pairing status
     */
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case ACTION_FOUND:
                        onDeviceFound(intent);
                        break;
                    case ACTION_BOND_STATE_CHANGED:
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        String pin = Integer.toString(
                                intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", 0));
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                handlePairingCode(device, pin);
                            }
                        }, 5000);
                        onDeviceStateChanged(intent);
                        break;
                    case ACTION_SCAN_MODE_CHANGED:
                        LoggerWrapper.d(TAG, "Discoverable off: Your phone is not visible to nearby devices...");
                        break;

                    case BluetoothDevice.ACTION_PAIRING_REQUEST:
                        LoggerWrapper.d(TAG, "ACTION_PAIRING_REQUEST");
                        break;

                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        LoggerWrapper.d(TAG, "ACTION_ACL_DISCONNECTED");
                        break;
                }
            }
        }
    };

    private void onDeviceFound(Intent intent) {
        LoggerWrapper.d(TAG, " onDevice Found : ");
        /**
         * Stop Scan Animation
         */
        setStopScanAnimation();
        /**
         * retrieve the BluetoothDevice object and
         * its EXTRA_DEVICE field, which contains information about the device’s characteristics and capabilities
         */
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        /**
         * You’ll usually want to display information about any devices you discover, and show it in a list
         */
        updateAdapter(device);
    }

    private void requestPermissions() {
        LoggerWrapper.d(TAG, "Inside requestPermissions : called");
        RequestPermissionHandler mRequestPermissionHandler = new RequestPermissionHandler();
        mRequestPermissionHandler.requestPermission(requireActivity(), ANDROID_12_BLE_PERMISSIONS, REQUEST_PERMISSION, mRequestPermissionListener);
    }

    private boolean isPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            int lBLUETOOTH_SCAN = ActivityCompat.checkSelfPermission(getParentActivity(), Manifest.permission.BLUETOOTH_SCAN);
            int lBLUETOOTH_CONNECT = ActivityCompat.checkSelfPermission(getParentActivity(), Manifest.permission.BLUETOOTH_CONNECT);
            return lBLUETOOTH_SCAN == PackageManager.PERMISSION_GRANTED && lBLUETOOTH_CONNECT == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }


    private void onDeviceStateChanged(Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (ActivityCompat.checkSelfPermission(getParentActivity(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            switch (device.getBondState()) {
                case BOND_BONDED:
                case BOND_BONDING:
                case BOND_NONE:
                    //rvBluetoothSignedAdapter.updateDeviceStatus(device);
                    scanForDevices();
                    break;
            }
        }
    }


    public static BluetoothSignedSettingsFragment newInstance() {
        return new BluetoothSignedSettingsFragment();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        BluetoothSignedSettingsComponent bluetoothSettingsComponent = DaggerBluetoothSignedSettingsComponent.builder().bluetoothSignedSettingsModule(new BluetoothSignedSettingsModule(this)).build();
        bluetoothSettingsComponent.injectBluetoothSignedSettingsFragment(this);
    }

    @Override
    public void bindViews(View view) {
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.bluetooth));
        setHint(HINT_NO_CHANGE, HINT_NO_CHANGE, View.GONE);
        String lDeviceName = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DEVICE_NAME, "");
        if (lDeviceName.isEmpty()) {
            if (ActivityCompat.checkSelfPermission(getParentActivity(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                binding.deviceNameText.setText(getParentActivity().bluetoothAdapter != null ? getParentActivity().bluetoothAdapter.getName() : "");
            } else {
                if (getParentActivity().bluetoothAdapter != null) {
                    getParentActivity().bluetoothAdapter.setName("Woohoo Device");
                    binding.deviceNameText.setText(getParentActivity().bluetoothAdapter != null ? getParentActivity().bluetoothAdapter.getName() : "Woohoo Device");
                }
            }
        } else {
            if (ActivityCompat.checkSelfPermission(getParentActivity(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                if (getParentActivity().bluetoothAdapter != null) {
                    getParentActivity().bluetoothAdapter.setName(lDeviceName);
                }
            }
            binding.deviceNameText.setText(lDeviceName);
        }
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, 989);
    }

    private void setBluetoothAdapter() {
        BluetoothManager lBluetoothManager = (BluetoothManager) getParentActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        if (lBluetoothManager != null) {
            getParentActivity().bluetoothAdapter = lBluetoothManager.getAdapter();
        }

        if (getParentActivity().bluetoothAdapter == null) {
            getParentActivity().bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.Bluetooth(), null);
        binding = FragmentBluetoothSettingsBinding.inflate(inflater, container, false);
        requestPermissions();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUIViews();
        registerListener();

        if (isPermission()) {
            scanForDevices();
        }
    }

    private void requestBluetoothPrivPer() {
        if (ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.BLUETOOTH_PRIVILEGED)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(),
                    "No BLUETOOTH_PRIVILEGED Permission", Toast.LENGTH_LONG).show();
            RequestPermissionHandler lRequestHandler = new RequestPermissionHandler();
            RequestPermissionHandler.RequestPermissionListener lRequestPermissionListener =
                    new RequestPermissionHandler.RequestPermissionListener() {
                        @Override
                        public void onSuccess() {
                            LoggerWrapper.d(TAG, "Inside requestResult : onSuccess");
                        }

                        @Override
                        public void onFailed() {
                            LoggerWrapper.e(TAG, "Inside requestResult : onFailed");
                        }
                    };

            lRequestHandler.requestPermission(requireActivity(),
                    new String[]{Manifest.permission.BLUETOOTH_PRIVILEGED},
                    REQUEST_PERMISSION, lRequestPermissionListener);
        } else {
            Toast.makeText(requireContext(),
                    "YES BLUETOOTH_PRIVILEGED Permission Granted", Toast.LENGTH_LONG).show();
        }
    }

    private void registerListener() {
        registerReceiver();
        registerBluetoothReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRequestPermissionListener = null;
        listOfBluetoothDevices = null;
//        getParentActivity().bluetoothAdapter = null;
        mBluetoothBroadcastReceiver = null;
        unRegisterListener();

    }

    private void unRegisterListener() {
        try {
            if (broadcastReceiver != null) {
                getParentActivity().unregisterReceiver(broadcastReceiver);
            }
            if (mBluetoothBroadcastReceiver != null) {
                getParentActivity().unregisterReceiver(mBluetoothBroadcastReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private RequestPermissionHandler.RequestPermissionListener mRequestPermissionListener = new RequestPermissionHandler.RequestPermissionListener() {
        @Override
        public void onSuccess() {
            LoggerWrapper.d(TAG, "Inside requestResult : onSuccess");
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                requestBluetoothPrivPer();
            }
            enableScanningCheck(true);
        }

        @Override
        public void onFailed() {
            LoggerWrapper.e(TAG, "Inside requestResult : onFailed");
            if (BuildConfig.DEBUG) {
                MethodUtils.showToastMessage(getParentActivity(), getString(R.string.request_permission_failed));
            } else {
                MethodUtils.showToastMessage(getParentActivity(), getString(R.string.warning_missing_app_permissions));
            }
            onBackPressed();
        }
    };

    private void registerBluetoothReceiver() {
        LoggerWrapper.d(TAG, "Inside registerBluetoothReceiver");
        if (mBluetoothBroadcastReceiver == null) {
            mBluetoothBroadcastReceiver = new BluetoothBroadcastReceiver(this);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        intentFilter.addAction("android.bluetooth.device.action.ACL_CONNECTED");
        intentFilter.addAction("android.bluetooth.device.action.ACL_DISCONNECT_REQUESTED");
        intentFilter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.getParentActivity().registerReceiver(mBluetoothBroadcastReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            this.getParentActivity().registerReceiver(mBluetoothBroadcastReceiver, intentFilter);
        }
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_FOUND);
        intentFilter.addAction(ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(ACTION_SCAN_MODE_CHANGED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.getParentActivity().registerReceiver(broadcastReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            this.getParentActivity().registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    private Runnable stopScanAnimation = new Runnable() {
        @Override
        public void run() {
            LoggerWrapper.e(TAG, "Inside stopScanAnimation : called");
            if (ActivityCompat.checkSelfPermission(getParentActivity().getApplicationContext(),
                    Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                if (getParentActivity().bluetoothAdapter != null) {
                    getParentActivity().bluetoothAdapter.cancelDiscovery();
                }
                if (binding.reload != null) {
                    binding.reload.clearAnimation();
                }
            }
        }
    };

    private void setStopScanAnimation() {
        handler.removeCallbacks(stopScanAnimation);
        handler.postDelayed(stopScanAnimation, AUTO_SCAN_DELAY);
    }

    private void initUIViews() {
        if (visibilityTimer != null) {
            visibilityTimer.cancel();
        }

        binding.bluetoothDiscoverableText.setVisibility(View.INVISIBLE);


        enableScanningCheck(getParentActivity().bluetoothAdapter.isEnabled());


        binding.listBluetooth.setLayoutManager(new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false));
        binding.listBluetooth.setAdapter(rvBluetoothSignedAdapter);
        setClickEvents();
    }


    void onBluetoothSwitchToggle(boolean isChecked) {
        toggleBluetoothSwitch(isChecked);
    }

    // Constants for visibility modes
    public static final int VISIBILITY_MODE_DISCOVERABLE = BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE;
    public static final int VISIBILITY_MODE_CONNECTABLE = BluetoothAdapter.SCAN_MODE_CONNECTABLE;
    public static final int VISIBILITY_MODE_NONE = BluetoothAdapter.SCAN_MODE_NONE;

    public boolean setBluetoothDiscoverableMax() {

        if (getParentActivity().bluetoothAdapter != null) {
            // Use reflection to set Bluetooth visibility programmatically
            try {
                Method setScanModeMethod = BluetoothAdapter.class.getMethod("setScanMode", int.class, int.class);
                return (boolean) setScanModeMethod.invoke(getParentActivity().bluetoothAdapter, VISIBILITY_MODE_DISCOVERABLE, 300);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public boolean enableBluetooth() {

        if (getParentActivity().bluetoothAdapter != null) {
            // Check if Bluetooth is already enabled
            if (!getParentActivity().bluetoothAdapter.isEnabled()) {
                // Use reflection to enable Bluetooth programmatically
                try {
                    Method enableMethod = BluetoothAdapter.class.getMethod("enable");
                    return (boolean) enableMethod.invoke(getParentActivity().bluetoothAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    public boolean disableBluetooth() {

        if (getParentActivity().bluetoothAdapter != null) {
            // Check if Bluetooth is already enabled
            if (getParentActivity().bluetoothAdapter.isEnabled()) {
                // Use reflection to enable Bluetooth programmatically
                try {
                    Method enableMethod = BluetoothAdapter.class.getMethod("disable");
                    return (boolean) enableMethod.invoke(getParentActivity().bluetoothAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    public static boolean pairWithDevice(BluetoothDevice device) {
        try {
            // Ensure Bluetooth is enabled
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                Log.e("BluetoothPairing", "Bluetooth is not enabled");
                return false;
            }

            // Use reflection to get the createBond method of BluetoothDevice
            Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
            return (boolean) createBondMethod.invoke(device);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void handlePairingCode(BluetoothDevice device, String pairingCode) {
        try {
            // Use reflection to get the setPin and setPairingConfirmation methods of BluetoothDevice
            Method setPinMethod = BluetoothDevice.class.getMethod("setPin", byte[].class);
            Method setPairingConfirmationMethod = BluetoothDevice.class.getMethod("setPairingConfirmation", boolean.class);

            // Convert pairingCode to bytes
            byte[] pinBytes = pairingCode.getBytes();

            // Invoke setPin method
            setPinMethod.invoke(device, pinBytes);

            // Invoke setPairingConfirmation method
            setPairingConfirmationMethod.invoke(device, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toggleBluetoothSwitch(boolean isEnabled) {
        if (isEnabled) {

            registerListener();
            boolean bluetoothEnableStatus = enableBluetooth();
            setBluetoothDiscoverableMax();
            getParentActivity().updateBluetoothIconInHomePage(true);
            getParentActivity().drawerItemEnable();
            enableScanningCheck(bluetoothEnableStatus);
            scanForDevices();

        } else {
            if (visibilityTimer != null) {
                visibilityTimer.cancel();
            }
            getParentActivity().setTimeLeft(0);
            binding.bluetoothDiscoverableText.setVisibility(View.INVISIBLE);

            Toast.makeText(getParentActivity(), "Bluetooth disabled!", Toast.LENGTH_SHORT).show();
            getParentActivity().updateBluetoothIconInHomePage(false);
            disableBluetooth();
            getParentActivity().drawerItemDisEnable();
            unRegisterListener();
            listOfBluetoothDevices.clear();
            rvBluetoothSignedAdapter.updateList(listOfBluetoothDevices);
            enableScanningCheck(false);

        }
    }

    private void enableScanningCheck(boolean bluetoothStatus) {
        binding.rlScan.setEnabled(bluetoothStatus);
        binding.availableTextView.setTextColor(bluetoothStatus ? MethodUtils.getColorFromTheme(getParentActivity(), R.attr.text_color_tertiary) : getResources().getColor(R.color.hintTextColor));
        if (bluetoothStatus) {
            binding.bluetoothSwitch.setChecked(true);
            binding.deviceNameHeader.setVisibility(View.VISIBLE);
            binding.deviceNameText.setVisibility(View.VISIBLE);
            binding.rlScan.setVisibility(View.VISIBLE);
            startReloadAnimation();
            long timeLeft = getParentActivity().getTimeLeft();

            if (timeLeft > 0) {
                startVisibilityTimer();
            }

        } else {
            getParentActivity().setTimeLeft(0);
            binding.bluetoothSwitch.setChecked(false);
            binding.deviceNameHeader.setVisibility(View.INVISIBLE);
            binding.deviceNameText.setVisibility(View.INVISIBLE);
            binding.rlScan.setVisibility(View.INVISIBLE);
            setStopScanAnimation();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == TURN_ON_BLUETOOTH) {
                enableScanningCheck(true);
                scanForDevices();
                getParentActivity().drawerItemEnable();
                getParentActivity().updateBluetoothIconInHomePage(true);
            }
        } else if (resultCode == DISCOVERABLE_BLUETOOTH_DURATION) {
            LoggerWrapper.d(TAG, "onActivityResult: Your phone is visible to nearby devices...");
            //mDiscoverableImg.setImageResource(R.drawable.ic_baseline_bluetooth_searching_24);
        } else {
            binding.bluetoothSwitch.setChecked(false);
            getParentActivity().updateBluetoothIconInHomePage(false);
            getParentActivity().drawerItemDisEnable();
            MethodUtils.showToastMessage(requireActivity(), getString(R.string.error_occured_enable_bluetooth));
        }
    }

    void onBackPressed() {
        getParentFragmentManager().popBackStack();
    }

    private void scanForDevices() {
        LoggerWrapper.d(TAG, " Inside scanForDevices : ");
         LoggerWrapper.d(TAG, " Inside scanForDevices : BLUETOOTH_CONNECT == true");

            listOfBluetoothDevices.clear();
            if (getParentActivity().bluetoothAdapter != null) {
                LoggerWrapper.d(TAG, " Inside scanForDevices : BLUETOOTH_CONNECT true : getParentActivity().bluetoothAdapter != null");
                getParentActivity().bluetoothAdapter.startDiscovery();
                Set<BluetoothDevice> pairedDevices = getParentActivity().bluetoothAdapter.getBondedDevices();
                // If there’s 1 or more paired devices...//
                if (pairedDevices.size() > 0) {
                    Header header = new Header();
                    header.setHeader(getString(R.string.paired_devices));
                    listOfBluetoothDevices.add(header);
                    // Retrieve each device’s public identifier and MAC address.
                    // Add each device’s name and address to an ArrayAdapter, ready to incorporate into a
                    //ListView
                    listOfBluetoothDevices.addAll(pairedDevices);
                }
                Header header = new Header();
                header.setHeader(getString(R.string.available_devices));
                listOfBluetoothDevices.add(header);
            } else {
                LoggerWrapper.d(TAG, " Inside scanForDevices : BLUETOOTH_CONNECT true : getParentActivity().bluetoothAdapter == null");
            }
    }

    private void updateAdapter(BluetoothDevice device) {
        LoggerWrapper.d(TAG, " Inside updateAdapter device : " + device);
            LoggerWrapper.d(TAG, " Inside checkSelfPermission  : true");
            LoggerWrapper.d(TAG, " Inside updateAdapter device : " + device.getName());
            if (listOfBluetoothDevices != null && listOfBluetoothDevices.size() > 0) {
                if (!listOfBluetoothDevices.contains(device) && device != null && device.getName() != null && !device.getName().isEmpty() && device.getAddress() != null && !device.getAddress().isEmpty()) {
                    LoggerWrapper.d(TAG, " Not contains :  " + device.getName());
                    listOfBluetoothDevices.add(device);
                }
            } else {
                LoggerWrapper.d(TAG, " availableDeviceList == null else ");
                if (device.getBondState() != BOND_BONDED) {
                    if (!listOfBluetoothDevices.contains(device) && device.getName() != null && !device.getName().isEmpty() && device.getAddress() != null && !device.getAddress().isEmpty()) {
                        LoggerWrapper.d(TAG, " Not contains :  " + device.getName());
                        listOfBluetoothDevices.add(device);
                    }
                }
            }
        rvBluetoothSignedAdapter.updateList(listOfBluetoothDevices);
    }

    private void setClickEvents() {
        binding.rlScan.setOnClickListener(view -> onRefreshClicked());
        //binding.deviceNameText.setOnClickListener(view -> onDeviceNameClicked());
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view -> onBackPressed());
        blueListener = (buttonView, isChecked) -> onBluetoothSwitchToggle(isChecked);
        binding.bluetoothSwitch.setOnCheckedChangeListener(blueListener);
    }

    void onRefreshClicked() {
        startReloadAnimation();
        scanForDevices();
    }

    void onDeviceNameClicked() {
        showBottomSheetChangeName();
    }

    private void showBottomSheetChangeName() {
        DialogInterface lListener = new DialogInterface() {
            @Override
            public void cancel() {
            }

            @Override
            public void dismiss() {
                if (ActivityCompat.checkSelfPermission(getParentActivity(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    binding.deviceNameText.setText(getParentActivity().bluetoothAdapter != null ? getParentActivity().bluetoothAdapter.getName() : "");
                }
            }
        };
//        ChangeNameBottomSheet lDialog = new ChangeNameBottomSheet();
//        lDialog.setListener(lListener);
//        lDialog.show(getChildFragmentManager(), "ChangeNameBottomSheet");
    }

    private void startReloadAnimation() {
        binding.reload.startAnimation(AnimationUtils.loadAnimation(getParentActivity(), R.anim.rotate));
    }

    private void resetRVAdapter() {
        rvBluetoothSignedAdapter.updateList(new ArrayList(0));
        setStopScanAnimation();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onClicked(BluetoothDevice bluetoothDevice) {

        Method createBondMethod = null;
        try {
            createBondMethod = BluetoothDevice.class.getMethod("createBond");
            createBondMethod.invoke(bluetoothDevice);

            Intent pairingIntent = new Intent(BluetoothDevice.ACTION_PAIRING_REQUEST);

            // Add the BluetoothDevice.EXTRA_DEVICE extra with the target device
            pairingIntent.putExtra(BluetoothDevice.EXTRA_DEVICE, bluetoothDevice);

            // Add extra to set Bluetooth pairing method. You can use BluetoothDevice.PAIRING_VARIANT_PIN
            // or other variants based on your requirements.
            pairingIntent.putExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.PAIRING_VARIANT_PIN);

            // Add extra to set Bluetooth pairing pin if required (e.g., for BluetoothDevice.PAIRING_VARIANT_PIN)
//             pairingIntent.putExtra(BluetoothDevice.EXTRA_PAIRING_KEY, "1234");

            // Send the broadcast to initiate the pairing request
            getParentActivity().sendBroadcast(pairingIntent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUnPairClicked(BluetoothDevice bluetoothDevice) {
        if (ActivityCompat.checkSelfPermission(getParentActivity(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            if (bluetoothDevice.getBondState() == BOND_BONDED) {
                try {
                    Method removeBondMethod = BluetoothDevice.class.getMethod("removeBond");
                    removeBondMethod.invoke(bluetoothDevice);
                } catch (Exception e) {

                }
            }
        }
    }

    public boolean unpairAllDevice() {
        boolean isSuccess = false;
        if (ActivityCompat.checkSelfPermission(getParentActivity(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            Set<BluetoothDevice> pairedDevices = getParentActivity().bluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    try {
                        Method m = device.getClass()
                                .getMethod("removeBond", (Class[]) null);
                        m.invoke(device, (Object[]) null);
                        isSuccess = true;
                    } catch (Exception e) {
                        isSuccess = false;
                        e.printStackTrace();
                    }
                }
            }
        }
        return isSuccess;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onBluetoothError() {
        LoggerWrapper.e(TAG, "There was an error enabling the Bluetooth Adapter.");
    }

    @Override
    public void onBluetoothConnected(BluetoothDevice device) {
            if (device != null && (device.getBondState() == BOND_BONDED || device.getBondState() == BOND_BONDING)) {
                MethodUtils.showToastMessage(getParentActivity(), device.getName() + getParentActivity().getString(R.string.bt_error_connected));
                rvBluetoothSignedAdapter.updateDeviceStatus(device);
                playConnectedAudio();
            } else {
                MethodUtils.showToastMessage(getParentActivity(), getParentActivity().getString(R.string.bt_error_disable));
            }
        }

    private void playConnectedAudio() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getParentActivity(), notification);
        r.stop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            r.setHapticGeneratorEnabled(true);
        }
        r.play();
    }

    @Override
    public void onBluetoothDisconnected(BluetoothDevice device) {
        if (ActivityCompat.checkSelfPermission(getParentActivity(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            if (device != null) {
                rvBluetoothSignedAdapter.updateDeviceStatus(device);
                playConnectedAudio();
                setStopScanAnimation();
            }
        }
    }

    private void makeDeviceDiscoverable() {
        Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, BLUETOOTH_VISIBILITY_DURATION);
        someActivityResultDiscovery.launch(i);
    }


    private void startVisibilityTimer() {
        long timeLeft = getParentActivity().getTimeLeft();
        if (timeLeft <= 0)
            timeLeft = BLUETOOTH_VISIBILITY_DURATION * 1000;
        if (visibilityTimer != null)
            visibilityTimer.cancel();
        visibilityTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                getParentActivity().setTimeLeft(millisUntilFinished);
                if (binding.bluetoothDiscoverableText != null) {
                    binding.bluetoothDiscoverableText.setVisibility(View.VISIBLE);
                    binding.bluetoothDiscoverableText.setText(getParentActivity().getString(R.string.bt_discovery_text) + " " + (millisUntilFinished / 1000) + " " + getParentActivity().getString(R.string.bluetooth_sec));
                }
            }

            @Override
            public void onFinish() {
                getParentActivity().setTimeLeft(0);
                if (binding.bluetoothDiscoverableText != null)
                    binding.bluetoothDiscoverableText.setVisibility(View.INVISIBLE);

            }
        };
        visibilityTimer.start();
    }


    ActivityResultLauncher<Intent> someActivityResultDiscovery = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_CANCELED) {
                binding.bluetoothDiscoverableText.setVisibility(View.INVISIBLE);
            } else {
                startVisibilityTimer();
            }
        }
    });

}
