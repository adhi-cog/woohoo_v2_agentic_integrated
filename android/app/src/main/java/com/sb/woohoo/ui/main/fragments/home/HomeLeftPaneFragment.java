package com.sb.woohoo.ui.main.fragments.home;

import static android.view.View.VISIBLE;
import static com.sb.woohoo.utils.Constants.HOLIDAY_INN_DOMAIN_ID;
import static com.sb.woohoo.utils.Constants.PARK_ROYAL_PICKERING_DOMAIN_ID;
import static com.sb.woohoo.utils.Constants.POSTILLION_DOMAIN_ID;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.FragmentLeftcontentBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.home.di.DaggerHomeLeftPaneComponent;
import com.sb.woohoo.ui.main.fragments.home.di.HomeLeftPaneComponent;
import com.sb.woohoo.ui.main.fragments.home.di.HomeLeftPaneModule;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.Constants;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;

public class HomeLeftPaneFragment extends FeatureFragment<MainActivity> {

    @Inject
    Picasso picasso;

    private Handler handler = new Handler();
    private Runnable dateThreadRunnable = this::setDate;
    private FragmentLeftcontentBinding binding;

    public static HomeLeftPaneFragment newInstance(String image) {
        HomeLeftPaneFragment fragment = new HomeLeftPaneFragment();
        Bundle args = new Bundle();
        args.putString("image", image);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String createTag() {
        return HomeLeftPaneFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLeftcontentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        HomeLeftPaneComponent component =
                DaggerHomeLeftPaneComponent
                        .builder()
                        .homeLeftPaneModule(new HomeLeftPaneModule(HomeLeftPaneFragment.this))
                        .build();
        component.injectHomeLeftPaneFragment(HomeLeftPaneFragment.this);
    }

    private String domainId;

    @Override
    public void bindViews(View view) {
        domainId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "");
        picasso.load(getArguments().getString("image"))
                    .fit()
                    .into(binding.hotelImage);
        setDate();
        initIntroVideo();
    }

    private void initIntroVideo() {
        if (domainId.equalsIgnoreCase(HOLIDAY_INN_DOMAIN_ID) || domainId.equalsIgnoreCase(POSTILLION_DOMAIN_ID)) {
            binding.btnIntro.setVisibility(VISIBLE);
        } else {
            binding.btnIntro.setVisibility(View.GONE);
        }
        binding.btnIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playIntroVideoFromRaw();
            }
        });
    }

    private void playIntroVideoFromRaw() {
        IntroVideoPlayerDialog dialog = new IntroVideoPlayerDialog(getParentActivity(), getParentActivity());
        dialog.show();
    }

    public void setDate() {

        String timeformat = "hh:mm aa";
        String dateformat = "dd MMM yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateformat, Locale.ENGLISH);
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat(timeformat, Locale.ENGLISH);
        Calendar today = Calendar.getInstance();
        binding.tvDate.setText(simpleDateFormat.format(today.getTime()));
        binding.tvTime.setText(simpleTimeFormat.format(today.getTime()));
        runDateThread();
    }

    private void runDateThread() {
        // Fall back wake-word thread runner, in case if something times out
        handler.removeCallbacksAndMessages(dateThreadRunnable);
        handler.postDelayed(dateThreadRunnable, 60 * 1000);
    }

    @Override
    public void onPause() {
        handler.removeCallbacks(dateThreadRunnable);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        runDateThread();

        if (WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "").equalsIgnoreCase(Constants.RC_DOMAIN_ID)) {
            binding.tvDate.setVisibility(View.GONE);
            binding.tvTime.setVisibility(View.GONE);
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
}
