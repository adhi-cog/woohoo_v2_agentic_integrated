package com.sb.woohoo.ui.onboarding;

import static com.sb.woohoo.ui.main.fragments.settings.engineswitch.EngineSwitchFragment.SMARTFLOW_ENGINE;
import static com.sb.woohoo.utils.others.MethodUtils.setActivityFullScreen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.sb.woohoo.BuildConfig;
import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.custom.NonSwipeableViewPager;
import com.sb.woohoo.custom.RequestPermissionHandler;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.ActivityOnboardingBinding;
import com.sb.woohoo.ui.base.BaseActivity;
import com.sb.woohoo.ui.enrollment.EnrollmentActivity;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.settings.wifi.WifiFragment;
import com.sb.woohoo.ui.onboarding.di.DaggerOnBoardingComponent;
import com.sb.woohoo.ui.onboarding.di.OnBoardingComponent;
import com.sb.woohoo.ui.onboarding.di.OnBoardingModule;
import com.sb.woohoo.ui.onboarding.pojo.RegisterHubParameters;
import com.sb.woohoo.utils.Constants;
import com.sb.woohoo.utils.LocaleHelper;
import com.sb.woohoo.utils.db.DbHandler;
import com.sb.woohoo.utils.gps.GPSLocation;
import com.sb.woohoo.utils.others.MethodUtils;
import com.sb.woohoo.utils.sensory.Enroll;
import com.sb.woohoo.utils.sensory.SensoryActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Inject;

/**
 * Shows Pages to fill information by User before user boards to Home Screen.
 */
public class OnboardingActivity
        extends BaseActivity
        implements OnBoardingContract.View {
    // Constants.
    private static final int REQUEST_PERMISSION = 600;
    private static final int REQUEST_MODIFY_SETTINGS = 200;
    private static final String[] mPermissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH
    };
    private int currentPermissionIndex = 0;

    private static final String[] mPermissionForAndroidSOnwards = {
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT};

    private String[] getRequiredPermissions() {

        ArrayList<String> list  = new ArrayList(Arrays.asList(mPermissions));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            list.addAll(Arrays.asList(mPermissionForAndroidSOnwards));
        }



        if(!isGpsSupported()) {
            list.remove(Manifest.permission.ACCESS_FINE_LOCATION);
            list.remove(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        String[] permissionArray = new String[list.size()];
        permissionArray = list.toArray(permissionArray);
        return permissionArray;

    }

        // Views.
    // Presenter.
    @Inject
    OnBoardingPresenter mPresenter;
    // Other Components.
    @Inject
    GPSLocation mLocation;
    // Adapters.
    private OnboardingViewPagerAdapter onboardingViewPagerAdapter;
    // Variables.
    private double[] latLng = new double[2];
    private boolean mIsTimeZoneSet;
    private boolean isGetTasksResult = false;
    private Dialog settingsRequestAlertDialog = null;
    /*************************************************** Listeners ****************************************************/

    private ViewPager.OnPageChangeListener mPageChangeListener =
            new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    setHeaderTextForEachFragment(position);
                    binding.viewpagerOnboarding.getCurrentItem();
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }

                @Override
                public void onPageSelected(int position) {
                }
            };

    private RequestPermissionHandler.RequestPermissionListener mRequestPermissionListener =
            new RequestPermissionHandler.RequestPermissionListener() {
                @Override
                public void onSuccess() {
                    requestModifySettingsPermission();
                    completeSetupWithPermissions(isGetTasksResult);
                }

                @Override
                public void onFailed() {
                    if (BuildConfig.DEBUG) {
                        MethodUtils.showToastMessage(getApplicationContext(), getString(R.string.request_permission_failed));
                    } else {
                        MethodUtils.showToastMessage(getApplicationContext(), getString(R.string.warning_missing_app_permissions));
                    }
                    finish();
                }
            };

    /************************************* {@link android.app.Activity} Callbacks *************************************/

    private ActivityOnboardingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        configureRotation();
        super.onCreate(savedInstanceState);
        LocaleHelper.setLocale(this, LocaleHelper.getLanguage(OnboardingActivity.this));
        binding = ActivityOnboardingBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        binding.wifi.setOnClickListener(this::onWifiClicked);

        binding.onboardingRoot.setOnClickListener(this::rootClicked);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNotificationPolicyPermission();
    }

    private void checkNotificationPolicyPermission() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            startActivity(intent);
        } else {
            new GetTasks().execute();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isGpsSupported() && mLocation != null)
            mLocation.cancelLocationUpdate();
    }

    /***************************************** {@link BaseActivity} Callbacks *****************************************/

    @Override
    public void onLocationChanged(double lat, double lon) {
        super.onLocationChanged(lat, lat);
        latLng[0] = lat;
        latLng[1] = lon;
    }

    /********************************************* {@code private} Methods ********************************************/

    private void requestPermissions() {
        if (currentPermissionIndex < getRequiredPermissions().length) {
            RequestPermissionHandler mRequestPermissionHandler = new RequestPermissionHandler();
            String[] permission = {getRequiredPermissions()[currentPermissionIndex]};
            mRequestPermissionHandler.requestPermission(
                    OnboardingActivity.this,
                    permission,
                    REQUEST_PERMISSION,
                    mRequestPermissionListener
            );
            currentPermissionIndex++;
        } else {
            // All permissions have been requested
            requestModifySettingsPermission();
            completeSetupWithPermissions(isGetTasksResult);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        LoggerWrapper.d("OnBoardingActivity", " Inside onRequestPermissionsResult called");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && requestCode == REQUEST_PERMISSION) {
            boolean hasAllPermissions = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    hasAllPermissions = false;
                    break;
                }
            }
            if (hasAllPermissions) {
                requestPermissions(); // Request the next permission
            } else {
                // Permission denied, handle accordingly
                if (BuildConfig.DEBUG) {
                    MethodUtils.showToastMessage(getApplicationContext(), getString(R.string.request_permission_failed));
                } else {
                    MethodUtils.showToastMessage(getApplicationContext(), getString(R.string.warning_missing_app_permissions));
                }
                finish();
            }
        }
    }

    private void completeSetupWithPermissions(boolean isGetTasksResult) {
        if (isGetTasksResult) {
            registerHub();
        }
        checkConditions();
        if (isGpsSupported() && mLocation != null)
            mLocation.requestLocationUpdate();
    }

    private void injectActivity() {
        OnBoardingComponent component =
                DaggerOnBoardingComponent
                        .builder()
                        .onBoardingModule(new OnBoardingModule(OnboardingActivity.this))
                        .build();
        component.injectOnBoardingActivity(OnboardingActivity.this);
    }

    private void initializeViews() {
        setActivityFullScreen(OnboardingActivity.this);
        setUpViewPager();
    }

    private void setUpViewPager() {
        if (onboardingViewPagerAdapter == null) {
            setUpViewPagerAdapter();
            binding.viewpagerOnboarding.setPagingEnabled(false);
            binding.viewpagerOnboarding.setAdapter(onboardingViewPagerAdapter);
            binding.viewpagerOnboarding.addOnPageChangeListener(mPageChangeListener);
        }
    }

    private void setUpViewPagerAdapter() {
        List<String> onBoardingFragments = new ArrayList<>();
        if (isFactoryReset()) {
            onBoardingFragments.add(OnboardingViewPagerAdapter.FRAGMENT_WIFI);
            onBoardingFragments.add(OnboardingViewPagerAdapter.FRAGMENT_LOGIN);
            onBoardingFragments.add(OnboardingViewPagerAdapter.FRAGMENT_WOOHOO_INTRO);
        } else {
            onBoardingFragments.add(OnboardingViewPagerAdapter.FRAGMENT_LOGIN);
            onBoardingFragments.add(OnboardingViewPagerAdapter.FRAGMENT_WIFI);
        }

        onboardingViewPagerAdapter = new OnboardingViewPagerAdapter(
                getSupportFragmentManager(), onBoardingFragments);
    }

    public boolean isFactoryReset() {
        SharedPrefsHelper helper = WooHooApp.getSharedPrefsHelper();
        return helper.get(SharedPrefsKey.PREF_KEY_IS_FACTORY_RESET_DONE, true);
    }

    private void setHeaderTextForEachFragment(int position) {
        if (isFactoryReset()) {
            switch (position) {
                case OnboardingViewPagerAdapter.FragmentOrder.FactoryReset.WIFI: {
                    binding.wifi.setVisibility(View.GONE);
                    binding.textHeader.setText(getResources().getString(R.string.set_up_wi_fi));
                }
                break;

                case OnboardingViewPagerAdapter.FragmentOrder.FactoryReset.LOGIN: {
                    binding.wifi.setVisibility(View.VISIBLE);
                    binding.textHeader.setText(getResources().getString(R.string.header_login));
                }
                break;

                case OnboardingViewPagerAdapter.FragmentOrder.FactoryReset.WOOHOO_INTRO: {
                    binding.wifi.setVisibility(View.GONE);
                    binding.textHeader.setText(getResources().getString(R.string.select_a_language));
                }
                break;
            }
        } else {
            switch (position) {
                case OnboardingViewPagerAdapter.FragmentOrder.LogOut.LOGIN: {
                    binding.wifi.setVisibility(View.VISIBLE);
                    binding.textHeader.setText(getResources().getString(R.string.header_login));
                }
                break;

                case OnboardingViewPagerAdapter.FragmentOrder.LogOut.WIFI: {
                    binding.wifi.setVisibility(View.GONE);
                    binding.textHeader.setText(getResources().getString(R.string.set_up_wi_fi));
                }
                break;
            }
        }
    }

    private void callRegisterHubAPI(RegisterHubParameters parameters) {
        mPresenter.registerHub(parameters);
    }

    /**
     * Checks the conditions to immediately open other Activity.
     */
    private void checkConditions() {
        if (!WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_EMAIL, "").isEmpty()) {
            openMainActivity(false);

        }
//        else if (!isEnrollmentDone()) {
//            openEnrollmentActivity();
//        }
    }

    /**
     * Checks whether Sensory-v6 enrollment has been done by the user or not, by checking the .snsr file in File System.
     *
     * @return {@code true}, if enrollment is done, else {@link false}.
     */
    private boolean isEnrollmentDone() {
        File enrollOutDir = Enroll.getOutDir(this);
        File enrollOutFile = new File(enrollOutDir, SensoryActivity.EnrollOutFile);
        return enrollOutFile.exists();
    }

    /**
     * Starts {@link EnrollmentActivity}
     */
    private void openEnrollmentActivity() {
        Intent intent = new Intent(OnboardingActivity.this, EnrollmentActivity.class);
        intent.setAction("settings");
        startActivity(intent);
        finish();
    }

    public WifiFragment getWifiSelectionFragment() {
        if (onboardingViewPagerAdapter == null) {
            return null;
        } else {
            int item;
            if (isFactoryReset())
                item = OnboardingViewPagerAdapter.FragmentOrder.FactoryReset.WIFI;
            else
                item = OnboardingViewPagerAdapter.FragmentOrder.LogOut.WIFI;
            if (onboardingViewPagerAdapter.getItem(item) instanceof WifiFragment) {
                return (WifiFragment) onboardingViewPagerAdapter.getItem(item);
            } else return null;
        }
    }

    /********************************************* {@code public} Methods *********************************************/

    @SuppressWarnings("SameParameterValue")
    public void setCurrentItem(int item, boolean smoothScroll) {
        binding.viewpagerOnboarding.setCurrentItem(item, smoothScroll);
    }

    public void openMainActivity(boolean isFromLoginScreen) {

        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        mainActivityIntent.putExtra(Constants.IS_FROM_LOGIN_SCREEN, isFromLoginScreen);
        startActivity(mainActivityIntent);
        finish();
    }

    public boolean isTimeZoneSet() {
        return TimeZone.getDefault() != null;
    }

    public boolean isIsTimeZoneSet() {
        return mIsTimeZoneSet;
    }

    public void setIsTimeZoneSet(boolean isTimeZoneSet) {
        this.mIsTimeZoneSet = isTimeZoneSet;
    }

    public void registerHub() {
        BuildRegisterHubParameters parametersBuilder =
                new BuildRegisterHubParameters(
                        OnboardingActivity.this,
                        getWifiSelectionFragment(),
                        latLng
                );
        callRegisterHubAPI(parametersBuilder.build());
    }

    /*********************************** {@link OnBoardingContract.View} Callbacks ************************************/

    @Override
    public void showError(String errorMessage) {
        MethodUtils.showToastMessage(OnboardingActivity.this, errorMessage);
    }


    // on click
    public void onWifiClicked(View view) {
        try {
            MethodUtils.hideSoftInputKeyboard(OnboardingActivity.this);
        } catch (Exception e) {
            LoggerWrapper.i(OnboardingActivity.class.getSimpleName(), "Keyboard NullPointerException");
        }
        int item;
        if (isFactoryReset()) {
            item = OnboardingViewPagerAdapter.FragmentOrder.FactoryReset.WIFI;
        } else {
            item = OnboardingViewPagerAdapter.FragmentOrder.LogOut.WIFI;
        }
        setCurrentItem(item, true);
    }

    public void rootClicked(View view) {
        MethodUtils.hideSoftInputKeyboard(view, OnboardingActivity.this);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MODIFY_SETTINGS) {
            if (settingsRequestAlertDialog != null && Settings.System.canWrite(this)) {
                settingsRequestAlertDialog.dismiss();
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        }
    }

    private void requestModifySettingsPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                if (settingsRequestAlertDialog != null && settingsRequestAlertDialog.isShowing()) {
                    settingsRequestAlertDialog.dismiss();
                    settingsRequestAlertDialog = null;
                }
                settingsRequestAlertDialog = new Dialog(this, R.style.DialogTheme);
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    MethodUtils.setDialogMargins(settingsRequestAlertDialog);
                }
                settingsRequestAlertDialog.setContentView(R.layout.dialog_setting_access);

                Button cancel = settingsRequestAlertDialog.findViewById(R.id.button_negative);
                Button ok = settingsRequestAlertDialog.findViewById(R.id.button_positive);

                cancel.setOnClickListener(v -> OnboardingActivity.this.finish());

                ok.setOnClickListener(v -> {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, REQUEST_MODIFY_SETTINGS);
                });

                settingsRequestAlertDialog.setCancelable(false);
                if (settingsRequestAlertDialog != null && !settingsRequestAlertDialog.isShowing()) {
                    settingsRequestAlertDialog.show();
                }

            } else {
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        }
    }

    class GetTasks extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                DbHandler dbHandler = new DbHandler();
                String contents = dbHandler.retrive();
                SharedPrefsHelper helper = WooHooApp.getSharedPrefsHelper();
                if (WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_EMAIL, "").isEmpty()) {

                    if (!contents.equalsIgnoreCase("")) {
                        String[] items = contents.split("\n");
                        helper.put(SharedPrefsKey.PREF_KEY_FIRST_NAME, items[7]);
                        helper.put(SharedPrefsKey.PREF_KEY_LAST_NAME, items[6]);
                        helper.put(SharedPrefsKey.PREF_KEY_EMAIL, items[8]);
                        helper.put(SharedPrefsKey.PREF_KEY_SESSION_TOKEN, items[5]);
                        helper.put(SharedPrefsKey.PREF_KEY_STATUS, items[10]);
                        helper.put(SharedPrefsKey.PREF_KEY_REFRESH_TOKEN, items[3]);
                        helper.put(SharedPrefsKey.PREF_KEY_USER_ID, items[9]);
                        helper.put(SharedPrefsKey.PREF_USER_ID_TYPE, items[0]);

                        dbHandler.deleteFileIfthere();

                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            isGetTasksResult = result;
            injectActivity();
            initializeViews();
            requestPermissions();
            MethodUtils.setActivityFullScreen(OnboardingActivity.this);
            WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_ENGINE, SMARTFLOW_ENGINE);
        }
    }
}