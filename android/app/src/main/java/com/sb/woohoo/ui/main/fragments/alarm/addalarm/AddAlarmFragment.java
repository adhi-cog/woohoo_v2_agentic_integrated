package com.sb.woohoo.ui.main.fragments.alarm.addalarm;

import static com.sb.woohoo.utils.others.MethodUtils.flagForPendingIntent;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.databinding.FragmentAddAlarmBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.alarm.addalarm.di.AddAlarmComponent;
import com.sb.woohoo.ui.main.fragments.alarm.addalarm.di.AddAlarmModule;
import com.sb.woohoo.ui.main.fragments.alarm.addalarm.di.DaggerAddAlarmComponent;
import com.sb.woohoo.ui.main.fragments.alarm.alarm.AddAlarmHelper;
import com.sb.woohoo.ui.main.fragments.alarm.alarm.AlarmContract;
import com.sb.woohoo.ui.main.fragments.alarm.broadcastreceiver.AlarmReceiver;
import com.sb.woohoo.ui.main.fragments.alarm.pojo.Alarm;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.SingleClickListener;

import java.util.Calendar;

import javax.inject.Inject;

public class AddAlarmFragment extends FeatureFragment<MainActivity> implements AlarmContract.View {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_ALARM;

    @Inject
    AddAlarmPresenter addAlarmPresenter;
    @Inject
    AnalyticsHelper helper;

    private int mHourOfDay, mMinute;
    private AddAlarmHelper addAlarmHelper = new AddAlarmHelper();
    private String alarmPosition;
    private String dateTime;

    private FragmentAddAlarmBinding binding;

    public AddAlarmFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        AddAlarmComponent addAlarmComponent = DaggerAddAlarmComponent.builder()
                .addAlarmModule(new AddAlarmModule(this))
                .build();

        addAlarmComponent.injectAddAlarmFragment(this);
    }

    @Override
    public void bindViews(View view) {
        binding.interceptorLayout.setContext(getParentActivity());
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(R.string.add_alarm);
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    @Override
    public void displayAlarms() {
        Log.e("alarm_lifecycle", "displayAlarms");
    }

    @Override
    public void alarmAdded() {
        Log.e("alarm_lifecycle", "alarmAdded");
        Toast.makeText(getContext(),getString(R.string.alarm_added),Toast.LENGTH_SHORT).show();
        getParentActivity().onBackPressed();
        getParentActivity().showAlarmList();  //To load the data after adding successfully...
    }

    @Override
    public void alarmDeleted() {
        Log.e("alarm_lifecycle", "alarmDeleted");
    }

    @Override
    public void alarmsDeleted() {
        Log.e("alarm_lifecycle", "alarmsDeleted");
    }

    @Override
    public void alarmUpdated() {
        Log.e("alarm_lifecycle", "alarmsUpdated");
        Toast.makeText(getParentActivity(),getString(R.string.alarm_updated),Toast.LENGTH_SHORT).show();
        getParentActivity().onBackPressed();
        getParentActivity().showAlarmList();  //To load the data after update successfully...
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.AddAlarm(),  null);
        binding = FragmentAddAlarmBinding.inflate(inflater, container, false);
        addAlarmPresenter.showAlarm(Categories.Skills.Alarm.ALARM_SHOW);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            alarmPosition = bundle.getString("alarm_position");
            String lDateTime = bundle.getString("dateTime");
            String hour =
                    addAlarmHelper.convertDateFormat(addAlarmHelper.convertStringToDate(lDateTime
                            ,"yyyy-MM-dd'T'HH:mm"),"HH", false);
            String minute =
                    addAlarmHelper.convertDateFormat(addAlarmHelper.convertStringToDate(lDateTime
                            ,"yyyy-MM-dd'T'HH:mm"),"mm", false);
            binding.timePicker.setCurrentHour(Integer.parseInt(hour));
            binding.timePicker.setCurrentMinute(Integer.parseInt(minute));
            mHourOfDay = binding.timePicker.getCurrentHour();
            mMinute = binding.timePicker.getCurrentMinute();
        }

        if(alarmPosition == null){
            binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(R.string.add_alarm);
        }else{
            binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(R.string.update_alarm);
        }

        binding.timePicker.setOnTimeChangedListener((timePicker, hourOfDay, minute) -> {
            Log.e("onTimeChanged" , "time - " + hourOfDay + ":" + minute);
            mHourOfDay = hourOfDay;
            mMinute = minute;
        });

        setClickEvents();
    }

    private void setAlarm(int hourOfDay, int minute)
    {
        AlarmManager am =
                (AlarmManager) getParentActivity().getSystemService(Context.ALARM_SERVICE);

        Intent i= new Intent(getParentActivity(), AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(getParentActivity(), 0, i,
                flagForPendingIntent());

        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
    }

    private void setClickEvents() {

        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(new SingleClickListener()
        {
            @Override
            public void onSingleClick(@NonNull View v) {
                onBackPressed();
            }
        });


        binding.tvSave.setOnClickListener(new SingleClickListener()
        {
            @Override
            public void onSingleClick(@NonNull View v) {
                onSaveClicked();
            }
        });


        binding.tvCancel.setOnClickListener(new SingleClickListener()
        {
            @Override
            public void onSingleClick(@NonNull View v) {
                onCancelClicked();
            }
        });
    }

    void onBackPressed() {
        getParentActivity().onBackPressed();
        getParentActivity().showAlarmList();
    }

    void onSaveClicked(){

        if (TextUtils.isEmpty(alarmPosition)) {
            Alarm alarm = addAlarmHelper.setAlarm(binding.timePicker.getCurrentHour(),
                    binding.timePicker.getCurrentMinute(), Calendar.getInstance());
            addAlarmPresenter.addAlarm(Categories.Skills.Alarm.ALARM_ADD, alarm);
        } else {
            Alarm updatedAlarm = addAlarmHelper.updateAlarm(binding.timePicker.getCurrentHour(),
                    binding.timePicker.getCurrentMinute(), Calendar.getInstance());
            addAlarmPresenter.updateAlarmFromList(updatedAlarm, false,
                    Integer.parseInt(alarmPosition));
        }
    }

    void onCancelClicked(){
        getParentActivity().onBackPressed();
        getParentActivity().showAlarmList();  //To load the data after back press successfully...
    }
}

