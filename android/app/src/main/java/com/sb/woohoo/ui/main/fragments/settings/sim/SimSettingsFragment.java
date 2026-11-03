package com.sb.woohoo.ui.main.fragments.settings.sim;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.databinding.FragmentSimBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.settings.SettingsFragment;
import com.sb.woohoo.ui.main.fragments.settings.sim.di.DaggerSimSettingsComponent;
import com.sb.woohoo.ui.main.fragments.settings.sim.di.SimSettingsComponent;
import com.sb.woohoo.ui.main.fragments.settings.sim.di.SimSettingsModule;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.SIMUtils;

import java.io.IOException;

import javax.inject.Inject;

public class SimSettingsFragment extends FeatureFragment<MainActivity> {


    @Inject
    SIMUtils simUtils;

    @Inject
    AnalyticsHelper helper;

    private AsyncTask<Void, Void, Boolean> asyncTask;
    private FragmentSimBinding binding;

    public static SimSettingsFragment newInstance() {
        return new SimSettingsFragment();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        SimSettingsComponent component =
                DaggerSimSettingsComponent
                        .builder()
                        .simSettingsModule(new SimSettingsModule(SimSettingsFragment.this))
                        .build();
        component.injectSimSettings(SimSettingsFragment.this);
    }

    @Override
    public void bindViews(View view) {
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.sim_settings));
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view1 -> onBackPressed());
    }

    @Override
    public String getCategory() {
        return SimSettingsFragment.class.getSimpleName();
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.SimSettings(),  null);
        binding = FragmentSimBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();
    }

    private void initViews() {

        boolean value = simUtils.isMobileDataOn();

        binding.cellularSwitch.setChecked(value);

        binding.cellularSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setSwitch(isChecked);
                updateSettingsUI(isChecked);
            }
        });
    }

    private void setSwitch(boolean isChecked) {
        asyncTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    simUtils.toggleMobileData(isChecked);
                } catch (IOException | SecurityException e) {
                    LoggerWrapper.e(SimSettingsFragment.class.getSimpleName(), e.getMessage());
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aVoid) {
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    private void updateSettingsUI(boolean isChecked) {
        FragmentManager fragmentManager = getParentActivity().getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentByTag("SettingsFragment");
        if (currentFragment instanceof SettingsFragment) {
            ((SettingsFragment) currentFragment).onMobileNetworkChanged(isChecked);
        }
    }

    public void onRootClicked() {

    }


    void onBackPressed() {
        getParentActivity().onBackPressed();
        if (asyncTask != null) {
            asyncTask.cancel(true);
        }
    }
}
