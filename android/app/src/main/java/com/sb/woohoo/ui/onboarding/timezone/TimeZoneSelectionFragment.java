package com.sb.woohoo.ui.onboarding.timezone;


import android.app.AlarmManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.custom.RecyclerViewEmptySupport;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.FragmentTimeZoneSelectionBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.settings.devicesettings.DeviceSettingsFragment;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.onboarding.OnboardingActivity;
import com.sb.woohoo.ui.onboarding.OnboardingViewPagerAdapter;
import com.sb.woohoo.ui.onboarding.timezone.di.DaggerTimeZoneComponent;
import com.sb.woohoo.ui.onboarding.timezone.di.TimeZoneComponent;
import com.sb.woohoo.ui.onboarding.timezone.di.TimeZoneModule;
import com.sb.woohoo.utils.TimeZoneUtils;

import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class TimeZoneSelectionFragment extends FeatureFragment<MainActivity> implements TimeZoneContract.View, TimeZoneRecyclerListener {

    public static final int ONBOARDING = 1;
    public static final int MAINACTIVITY = 2;
    private static final String ACTIVITY_TYPE = "activity_type";

    @Inject
    TimeZonePresenter timeZonePresenter;
    @Inject
    TimeZoneAdapter timeZoneAdapter;
    boolean isOnBoarding = false;

    private String mTimeZone = "NA";
    private SharedPrefsHelper mPrefsHelper;

    private FragmentTimeZoneSelectionBinding binding;

    public static TimeZoneSelectionFragment newInstance(int activityType) {
        Bundle args = new Bundle();
        args.putInt(ACTIVITY_TYPE, activityType);
        TimeZoneSelectionFragment fragment = new TimeZoneSelectionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPrefsHelper = WooHooApp.getSharedPrefsHelper();
        binding = FragmentTimeZoneSelectionBinding.inflate(inflater,container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            int type = bundle.getInt(ACTIVITY_TYPE);
            isOnBoarding = type == ONBOARDING;
        }
        init();
    }

    private void init() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.listTimeZone.setLayoutManager(layoutManager);
        binding.listTimeZone.setAdapter(timeZoneAdapter);
        timeZonePresenter.loadTimeZones();
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText("Timezone");

        if (isOnBoarding) {
            binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setVisibility(View.GONE);
            binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setVisibility(View.GONE);
        } else {
            binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setVisibility(View.VISIBLE);
            binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setVisibility(View.VISIBLE);
        }

        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view -> onBackClicked());
    }

    @Override
    public void showTimeZones(List<TimeZone> timeZoneList, int defaultPosition) {
        timeZoneAdapter.updateList(timeZoneList);
        binding.listTimeZone.smoothScrollToPosition(defaultPosition);
    }

    @Override
    public void showEmptyView() {
        //We can an emptyView and set it here
        //Right now I have added just
        binding.listTimeZone.setVisibility(View.GONE);
        Toast.makeText(getActivity(), getString(R.string.no_timezone), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideEmptyView() {
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public String getTimeZone() {
        return mTimeZone;
    }

    @Override
    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }

    @Override
    public void onTimeZoneClicked(TimeZone timeZone) {
        AlarmManager am = (AlarmManager) Objects.requireNonNull(getContext())
                .getSystemService(Context.ALARM_SERVICE);

        String timeZoneID = TimeZoneUtils.getInstance(getActivity()).getTimeZoneId(timeZone);
        Log.e("timezone_issue","onTimeZoneClicked" + timeZoneID);
        SharedPrefsHelper helper = mPrefsHelper;
        helper.put(SharedPrefsKey.PREF_KEY_TIME_ZONE, timeZoneID);

        if (isOnBoarding) {
            ((OnboardingActivity) Objects.requireNonNull(getActivity()))
                    .setCurrentItem(OnboardingViewPagerAdapter
                            .FragmentOrder.FactoryReset.LOGIN, true);
        } else {
            FragmentManager fragmentManager = getParentActivity().getSupportFragmentManager();
            Fragment deviceSettings = fragmentManager.findFragmentByTag("DeviceSettingsFragment");
            if (deviceSettings instanceof DeviceSettingsFragment) {
                ((DeviceSettingsFragment) deviceSettings).onTimezoneChanged();
            }

            getParentActivity().onTimeZoneChanged();

            getParentActivity().onBackPressed();
        }
    }


    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        TimeZoneComponent timeZoneComponent = DaggerTimeZoneComponent.builder()
                .timeZoneModule(new TimeZoneModule(this))
                .build();

        timeZoneComponent.injectTimeZoneFragment(this);
    }

    @Override
    public void bindViews(View view) {
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    public void onBackClicked() {
        getParentActivity().onBackPressed();
    }

}
