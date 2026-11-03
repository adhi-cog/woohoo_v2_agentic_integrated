package com.sb.woohoo.ui.main.fragments.darkMode;

import static android.view.View.GONE;

import static com.sb.woohoo.utils.Constants.DARK_MODE;
import static com.sb.woohoo.utils.others.MethodUtils.setActivityFullScreen;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.databinding.FragmentDarkModeBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.darkMode.di.DaggerDarkModeComponent;
import com.sb.woohoo.ui.main.fragments.darkMode.di.DarkModeComponent;
import com.sb.woohoo.ui.main.fragments.darkMode.di.DarkModeModule;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.ui.InputInterceptorRelativeLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;

public class DarkModeFragment
        extends FeatureFragment<MainActivity> {

    @Inject
    AnalyticsHelper helper;

    TextView timeTextView;

    TextView dateTextView;

    InputInterceptorRelativeLayout backgroundLayout;

    RelativeLayout relMain;

    private Handler handler = new Handler();

    @Inject
    DarkModePresenter mSurveillancePresenter;
    private FragmentDarkModeBinding binding;

    public DarkModeFragment() { /*Required empty constructor*/ }

    public static DarkModeFragment newInstance() {
        return new DarkModeFragment();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ((MainActivity)context).enableFullWidthMode();
        }
    }

    @Override
    public void initDagger() {
        DarkModeComponent surveillanceComponent = DaggerDarkModeComponent.builder()
                .darkModeModule(new DarkModeModule(this))
                .build();

        surveillanceComponent.injectPrivateLiveFeedComponent(this);
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
        return mSurveillancePresenter;
    }

    private Boolean darkMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.DarkMode(),  null);
        binding = FragmentDarkModeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    private int originalBrightness;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setHint(HINT_NO_CHANGE, HINT_NO_CHANGE, GONE);
        intView(view);

        if(getArguments() != null) {
            if(getArguments().getBoolean(DARK_MODE)){
                darkMode = true;
            }
        }
        if(darkMode){
            relMain.setVisibility(GONE);
            originalBrightness = getScreenBrightness();
            setScreenBrightness(0);
        }else {
            relMain.setVisibility(View.VISIBLE);
            showTime();
            showDate();
        }
    }

    private void intView(View v) {
        Button turnOffSurveillance = v.findViewById(R.id.turn_off_dark_mode);
        turnOffSurveillance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentActivity().startSurveillanceMode(false);
                getParentActivity().onBackPressed();
            }
        });

        timeTextView=v.findViewById(R.id.tv_time);
        dateTextView=v.findViewById(R.id.tv_date);
        backgroundLayout= v.findViewById(R.id.background_layout);
        relMain= v.findViewById(R.id.rel_main);

        backgroundLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentActivity().startSurveillanceMode(false);
                getParentActivity().onBackPressed();
            }
        });
        turnOffSurveillance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentActivity().startSurveillanceMode(false);
                getParentActivity().onBackPressed();
            }
        });
        getParentActivity().startSurveillanceMode(true);
    }

    private void showTime() {
        Calendar today = Calendar.getInstance();

        String format = "hh:mm aa";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        timeTextView.setText(""+simpleDateFormat.format(today.getTime()));
        runTimeThread();
    }

    private void runTimeThread() {
        LoggerWrapper.i(DarkModeFragment.class.getSimpleName(), "This time thread should be closed if the screensaver isn't in the view.");
        handler.postDelayed(this::showTime, 60 * 1000);
    }

    private void runDateThread() {
        LoggerWrapper.i(DarkModeFragment.class.getSimpleName(), "This date thread should be closed if the screensaver isn't in the view.");
        handler.postDelayed(this::showDate, 24 * 60 * 60 * 1000);
    }

    private void showDate() {
        Calendar today = Calendar.getInstance();
        int date = today.get(Calendar.DATE);

        String weekDay;
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);

        weekDay = dayFormat.format(today.getTime());
        dateTextView.setText(String.valueOf(weekDay+", "+date + " " +
                String.format(Locale.US, "%tB", today) + " · 25° C"));
        runDateThread();
    }

    @Override
    public void onDestroyView() {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getParentActivity().disableFullWidthMode();
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if(darkMode) {
            setScreenBrightness(originalBrightness);
        }
    }

    private int getScreenBrightness() {
        try {
            ContentResolver contentResolver = getActivity().getContentResolver();
            return Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void setScreenBrightness(int brightness) {
        try {
            ContentResolver contentResolver = getActivity().getContentResolver();
            Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        setActivityFullScreen(getParentActivity());
        super.onResume();
    }
}
