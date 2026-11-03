package com.sb.woohoo.ui.main.fragments.settings;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.mediarouter.media.MediaControlIntent;
import androidx.mediarouter.media.MediaRouteSelector;
import androidx.mediarouter.media.MediaRouter;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.databinding.FragmentSettingsBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.enrollment.EnrollmentActivity;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.NonPopFragment;
import com.sb.woohoo.ui.main.fragments.settings.di.DaggerSettingsComponent;
import com.sb.woohoo.ui.main.fragments.settings.di.SettingsComponent;
import com.sb.woohoo.ui.main.fragments.settings.di.SettingsModule;
import com.sb.woohoo.ui.main.helpers.ConnectivityHelper;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;
import com.sb.woohoo.utils.sensory.Enroll;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

/**
 * Shows Various Settings to customize about Hub Device.
 */
public class SettingsFragment
        extends FeatureFragment<MainActivity>
        implements SettingsContract.View, OnClickListener, NonPopFragment {

    static final int SETTINGS_ACTION = 0;
    static final int SETTINGS_SWITCH = 1;
    static final int PERFORM_LOGOUT = 100;
    static final int PERFORM_ENROLLMENT = 101;
    static final int PERFORM_SCREENCAST = 102;
    private final static String LAUNCHER_APP_PACKAGE_NAME = "installer.woohoo.sb.com.woohoohubinstaller";
    public Enroll enroll;

    @Inject
    SettingsAdapter settingsAdapter;
    // Presenter.
    @Inject
    SettingsPresenter mPresenter;
    @Inject
    ConnectivityHelper connectivityHelper;
    @Inject
    AnalyticsHelper helper;


    private ProgressDialog pDialog;

    private Handler handler;
    private MediaRouter mMediaRouter;
    private MediaRouteSelector mSelector;
    private FragmentSettingsBinding binding;


    public SettingsFragment() { /*Required empty public constructor*/ }

    /**
     * Creates a new instance of {@link SettingsFragment}.
     *
     * @return Instance of {@link SettingsFragment}.
     */
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String createTag() {
        return SettingsFragment.class.getSimpleName();
    }

    /********************************************************** Fragment Callbacks ***************************************************/

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    /**
     * Injects {@link SettingsFragment} to {@link SettingsComponent}.
     */
    @Override
    public void initDagger() {
        SettingsComponent component =
                DaggerSettingsComponent
                        .builder()
                        .settingsModule(new SettingsModule(SettingsFragment.this))
                        .build();
        component.injectSettingsFragment(SettingsFragment.this);
    }

    /**
     * Initializes View components of this Fragment.
     *
     * @param view {@link View} of {@link SettingsFragment}.
     */
    @Override
    public void bindViews(View view) {
        binding.toolbar.setTitle(getString(R.string.settings));
        binding.backButton.setOnClickListener(view1 -> onBackButtonPressed());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        helper.track(new Screen.Settings(),  null);
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeSettings();
    }

    private void initializeSettings() {

        binding.settingsRecyclerView.setHasFixedSize(true);
        binding.settingsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.settingsRecyclerView.setAdapter(settingsAdapter);
        binding.settingsRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mPresenter.intializeViews();
        mPresenter.doCheckUpdate();
    }

    @Override
    public BasePresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        featureFragmentCleanUps();
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        mPresenter.presenterCleanUps();
    }

    /********************************************************** On-Click Listeners ***************************************************/

    void onBackButtonPressed() {
        getParentActivity().onBackPressed();
    }

    /****************************************************** Contracts : View Callbacks ***********************************************/

    @Override
    public void showError(String errorMessage) {
        MethodUtils.showToastMessage(getActivity(), errorMessage);
    }

    @Override
    public void closeMainActivity() {
        binding.progressBar.setVisibility(View.GONE);
        getParentActivity().openOnBoardingActivity();
    }

    @Override
    public void resetUI() {
        Intent intent = new Intent(getActivity(), EnrollmentActivity.class);
        intent.setAction("settings");
        startActivity(intent);
        Objects.requireNonNull(getActivity()).finish();
    }


    @Override
    public void onLogOutSuccess() {
        //mPresenter.deleteEnrollemntFile(getParentActivity().getApplicationContext());
    }

    @Override
    public void updateSettingsList() {
        settingsAdapter.notifyDataSetChanged();
    }

    void onRootClicked() {
    }

    @Override
    public void onItemClicked(Setting setting) {
        if (setting.getType() == SETTINGS_SWITCH && setting.getFeatureFragment() != null) {
            getParentActivity().switchFragmentUsingAdd(setting.getFeatureFragment());
        } else if (setting.getType() == SETTINGS_ACTION) {
            switch (setting.getAction()) {
                case PERFORM_LOGOUT:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    initiateLogOut();
                    break;
                case PERFORM_ENROLLMENT:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    resetUI();
                    break;
                case PERFORM_SCREENCAST:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    initiateScreenCast();
                    break;
            }
        }
    }

    private void initiateScreenCast() {
        enablingWiFiDisplay();

        handler = new Handler();
        mMediaRouter = MediaRouter.getInstance(getParentActivity());
        mSelector = new MediaRouteSelector.Builder()
                .addControlCategory(MediaControlIntent.CATEGORY_LIVE_VIDEO)
                .addControlCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)
                .build();
    }


    private void initiateLogOut() {
        if (MethodUtils.isNetworkAvailable(getParentActivity())) {
            checkForActiveInternet();
        } else {
            binding.progressBar.setVisibility(View.GONE);
            MethodUtils.showToastMessage(getParentActivity(), getString(R.string.check_internet));
        }
    }

    private void checkForActiveInternet() {

        getParentActivity().disableSkillsBeforeLogOutInitiate();

        connectivityHelper.setListener(new ConnectivityHelper.ConnectivityHandlerCallback() {
            @Override
            public void internetDown() {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), getString(R.string.wifi_no_internet), Toast.LENGTH_SHORT).show();
                getParentActivity().enableSkillsAfterLogOutError();
            }

            @Override
            public void serverDown() {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), getString(R.string.network_issues), Toast.LENGTH_SHORT).show();
                getParentActivity().enableSkillsAfterLogOutError();
            }

            @Override
            public void internetUp() {
                mPresenter.logOut();
            }
        });
        connectivityHelper.checkInternet();
    }

    public void onUpdateClicked() {
        mPresenter.onUpdateClicked();
    }

    public void onWifiNetworkChanged() {
        mPresenter.onWifiNetworkChanged();
    }

    public void onMobileNetworkChanged(boolean isChecked) {
        mPresenter.onMobileNetworkChanged(isChecked);
    }

    public void onEngineChanged(String text) {
        mPresenter.onEngineChanged(text);
    }

    public void onLangChanged(String text) {
        mPresenter.onLangChanged(text);
    }

    public void onWakeWordChanged(String text) {
        getParentActivity().onWakeWordChanged();
        mPresenter.onWakeWordChanged(text);
    }

    private MediaRouter.Callback mMRCallback = new MediaRouter.Callback() {

        @Override
        public void onRouteAdded(MediaRouter router, MediaRouter.RouteInfo route) {
            super.onRouteAdded(router, route);
            String outputText = router.getRoutes().size() + " Routes Detected\n";
            List routes = router.getRoutes();
            for (int i=0;i<routes.size(); i++) {
                outputText = outputText + routes.get(i) + "\n";
            }
            //output.setText(outputText);
        }

        @Override
        public void onRouteRemoved(MediaRouter router, MediaRouter.RouteInfo route) {
            super.onRouteRemoved(router, route);
            //output.setText("Route Removed: " + route + "\n"
            //        + router.getRoutes().size() + " routes detected.");
        }

        @Override
        public void onProviderAdded(MediaRouter router, MediaRouter.ProviderInfo provider) {
            super.onProviderAdded(router, provider);
            String outputText = router.getProviders().size() + " Providers Detected\n";
            List providers = router.getProviders();
            for (int i=0;i<providers.size(); i++) {
                outputText = outputText + providers.get(i) + "\n";
            }
            //output.setText(outputText);
        }

        @Override
        public void onProviderRemoved(MediaRouter router, MediaRouter.ProviderInfo provider) {
            super.onProviderRemoved(router, provider);
            //output.setText("Provider removed: " + provider + "\n"
            //        + router.getProviders().size() + " providers detected.");
        }

        @Override
        public void onProviderChanged(MediaRouter router, MediaRouter.ProviderInfo provider) {
            super.onProviderChanged(router, provider);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if(mMediaRouter != null)
            mMediaRouter.addCallback(mSelector, mMRCallback,
                MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mMediaRouter != null)
            mMediaRouter.removeCallback(mMRCallback);
    }

    public void enablingWiFiDisplay() {
        try {
            startActivity(new Intent("android.settings.WIFI_DISPLAY_SETTINGS"));
            return;
        } catch (ActivityNotFoundException activitynotfoundexception) {
            activitynotfoundexception.printStackTrace();
        }

        try {
            startActivity(new Intent("android.settings.CAST_SETTINGS"));
            return;
        } catch (Exception exception1) {
            Toast.makeText(getParentActivity(), "Device not supported", Toast.LENGTH_LONG).show();
        }
    }
}
