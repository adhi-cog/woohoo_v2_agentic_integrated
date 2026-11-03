package com.sb.woohoo.ui.main.fragments.alarm.alarm;

import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.databinding.FragmentAlarmsBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.alarm.addalarm.AddAlarmFragment;
import com.sb.woohoo.ui.main.fragments.alarm.alarm.di.AlarmComponent;
import com.sb.woohoo.ui.main.fragments.alarm.alarm.di.AlarmModule;
import com.sb.woohoo.ui.main.fragments.alarm.alarm.di.DaggerAlarmComponent;
import com.sb.woohoo.ui.main.fragments.alarm.pojo.Alarm;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.SingleClickListener;

import javax.inject.Inject;

public class AlarmFragment extends FeatureFragment<MainActivity> implements AlarmContract.View, ClickListener {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_ALARM;


    @Inject
    AlarmPresenter alarmPresenter;
    @Inject
    AlarmAdapter alarmAdapter;
    @Inject
    AnalyticsHelper helper;

    private FragmentAlarmsBinding binding;

    public AlarmFragment() {
    }

    public static AlarmFragment newInstance(IntentInfo intentInfo) {
        AlarmFragment fragment = new AlarmFragment();

        if (intentInfo != null) {
            fragment.processData(intentInfo, null);
        }
        return fragment;
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public String createTag() {
        return AlarmFragment.class.getSimpleName();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //LoggerWrapper.d(TAG, "Setting icon clicked  onCreate");
        showAlarm();  //Calling get alarms for the first time.
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.Alarm(),  null);
        binding = FragmentAlarmsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void initDagger() {
        AlarmComponent alarmComponent = DaggerAlarmComponent.builder()
                .alarmModule(new AlarmModule(this))
                .build();

        alarmComponent.injectAlarmFragment(this);
    }

    @Override
    public void bindViews(View view) {
        binding.interceptorLayout.setContext(getParentActivity());
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(R.string.alarms);
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarRightIcon.setVisibility(VISIBLE);
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarRightIcon.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_add));

        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarRightIcon.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(@NonNull View v) {
                launchAddAlarmFragment(null, null);
            }
        });

        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(@NonNull View v) {
                onBackPressed();
            }
        });

        binding.btnAddAlarm.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(@NonNull View v) {
                addAlarmOnce();
            }
        });
        initViews();
        showProgress();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public BasePresenter getPresenter() {
        return alarmPresenter;
    }

    private void initViews() {
        setHint(HINT_RESET, HINT_RESET, VISIBLE);
        binding.alarmRecyclerView.setHasFixedSize(true);
        binding.alarmRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.alarmRecyclerView.setAdapter(alarmAdapter);
        binding.alarmRecyclerView.setEmptyView(binding.alarmEmptyView);
        binding.alarmRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    public void showAlarm() {
        alarmPresenter.showAlarm(Categories.Skills.Alarm.ALARM_SHOW);
    }

    public void showProgress() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    public void removeProgress() {
        binding.progressBar.setVisibility(View.GONE);
    }

    /**
     * Different methods have been created because more operations will be added in future
     */
    @Override
    public void alarmAdded() {
        removeProgress();
        alarmAdapter.notifyDataSetChanged();
    }

    @Override
    public void alarmDeleted() {
        removeProgress();
        alarmAdapter.notifyDataSetChanged();
    }

    @Override
    public void alarmsDeleted() {
        removeProgress();
        alarmAdapter.notifyDataSetChanged();
    }

    @Override
    public void alarmUpdated() {
        removeProgress();
        alarmAdapter.notifyDataSetChanged();
    }

    @Override
    public void displayAlarms() {
        removeProgress();
        alarmAdapter.notifyDataSetChanged();
        alarmPresenter.insertAllAlarmIdsInPref();
        alarmPresenter.deleteAllAlarmsFromAlarmManager();
        alarmPresenter.setAllAlarmsInAlarmManager();
        if(getParentActivity() != null){
            respondToUser(String.format(getParentActivity().getString(R.string.you_have_alrams_set), alarmPresenter.getAlarmList().size()));
        }
    }

    void onRootClicked() {
    }

    void addAlarmOnce() {
        /*addAlarmHelper.showTimePickerDialog(getParentActivity(), (hourOfDay, minute, c) -> {
            Alarm alarm = addAlarmHelper.setAlarm(hourOfDay,minute,c);
            alarmPresenter.addAlarm(Categories.Skills.Alarm.ALARM_ADD,alarm);
        });*/

        launchAddAlarmFragment(null, null);
    }

    private void launchAddAlarmFragment(String alarmPosition, String dateTime)
    {
        Bundle bundle = null;
        if (!TextUtils.isEmpty(alarmPosition)){
            bundle = new Bundle();
            bundle.putString("alarm_position",alarmPosition);
            bundle.putString("dateTime",dateTime);
        }
        AddAlarmFragment fragment = new AddAlarmFragment();
        fragment.setArguments(bundle);
        getParentActivity().switchFragmentUsingReplace(fragment);
    }

    /*@OnClick(R.id.btn_add_alarm_everyday)
    void addAlarmEveryday() {
        addAlarmHelper.showTimePickerDialog(getParentActivity(), (hourOfDay, minute, c) -> {
            Alarm alarm = addAlarmHelper.setAlarm(hourOfDay,minute,c);
            alarm.setEveryday(true);
            alarmPresenter.addAlarm(Categories.Skills.Alarm.ALARM_ADD,alarm);
        });
    }

    @OnClick(R.id.btn_delete_all)
    void deleteAllAlarm() {
        alarmPresenter.deleteAll();
    }*/

    void onBackPressed() {
        getParentActivity().onBackPressed();
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        alarmPresenter.presenterCleanUps();
    }

    @Override
    public void onDeleteAlarmClick(Alarm alarm, int position) {
        showProgress();
        alarmPresenter.deleteAlarm(0, position);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onUpdateAlarmClick(Alarm alarm, int position) {
//        2020-05-07T00:55 "yyyy-MM-dd'T'HH:mm"
        launchAddAlarmFragment(String.valueOf(position),alarm.getDateTime());
        /*addAlarmHelper.showTimePickerDialog(getParentActivity(), (hourOfDay, minute, c) -> {
         *//*Alarm updatedAlarm = addAlarmHelper.updateAlarm(hourOfDay,minute,c);
            alarmPresenter.updateAlarmFromList(updatedAlarm,alarm.isEveryday(),position);*//*
            launchAddAlarmFragment(String.valueOf(position));
        });*/
    }
}
