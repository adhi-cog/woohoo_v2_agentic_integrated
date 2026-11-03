package com.sb.woohoo.ui.main.fragments.weather;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.FragmentWeatherBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.NonPopFragment;
import com.sb.woohoo.ui.main.fragments.weather.di.DaggerWeatherComponent;
import com.sb.woohoo.ui.main.fragments.weather.di.WeatherComponent;
import com.sb.woohoo.ui.main.fragments.weather.di.WeatherModule;
import com.sb.woohoo.ui.main.fragments.weather.pojo.response.WeatherItem;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.Constants;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

public class WeatherFragment extends FeatureFragment<MainActivity> implements WeatherContract.View,NonPopFragment {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_WEATHER;

    @Inject
    WeatherPresenter weatherPresenter;
    @Inject
    AnalyticsHelper helper;
    @Inject
    WeatherForecastAdapter weatherForecastAdapter;
    private WeatherAdapter weatherAdapter;

    private HashMap<String, List<WeatherItem>> weatherListDateMap;
    private List<WeatherItem> currentWeatherList;
    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    private String currentWeatherDateInString;
    private FragmentWeatherBinding binding;

    public WeatherFragment() {
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageSelected(int position) {
            String selectedPageDate = currentWeatherList.get(position).getDate();
            Calendar date = Calendar.getInstance();
            try {
                date.setTime(sdf.parse(selectedPageDate));
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                currentWeatherDateInString = sdf2.format(date.getTime());
                if (weatherListDateMap.containsKey(currentWeatherDateInString))
                    weatherForecastAdapter.updateList(weatherListDateMap.get(currentWeatherDateInString));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (position == 0){
                changeLeftRightArrowVisibility(View.GONE, View.VISIBLE);
            } else if (position == currentWeatherList.size() - 1){
                changeLeftRightArrowVisibility(View.VISIBLE, View.GONE);
            } else {
                changeLeftRightArrowVisibility(View.VISIBLE, View.VISIBLE);
            }
        }
    };

    private void changeLeftRightArrowVisibility(int leftArrowVisibility, int rightArrowVisibility) {
        binding.imgLeft.setVisibility(leftArrowVisibility);
        binding.imgRight.setVisibility(rightArrowVisibility);
    }

    public static WeatherFragment newInstance(IntentInfo intentInfo) {
        WeatherFragment fragment = new WeatherFragment();
        if (intentInfo != null) {
            fragment.processData(intentInfo, null);
        }
        return fragment;
    }

    public static WeatherFragment newInstance() {
        return new WeatherFragment();
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public String createTag() {
        return WeatherFragment.class.getSimpleName();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.Weather(),  null);
        binding = FragmentWeatherBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "-1").equals(Constants.GBTB_DOMAIN_ID))
            binding.relativeSearch.setVisibility(View.GONE);
//        setHint(R.string.screen_saver_hint_text, R.color.black, View.GONE);
        binding.editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onSearchClick();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public BasePresenter getPresenter() {
        return weatherPresenter;
    }

    private void setUpRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.fragmentWeatherRecyclerView.setLayoutManager(linearLayoutManager);
        binding.fragmentWeatherRecyclerView.setAdapter(weatherForecastAdapter);
        binding.fragmentWeatherRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        WeatherComponent component =
                DaggerWeatherComponent
                        .builder()
                        .weatherModule(new WeatherModule(WeatherFragment.this))
                        .build();
        component.injectWeatherFragment(WeatherFragment.this);
    }

    @Override
    public void bindViews(View view) {
        setUpRecyclerView();
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view1 -> onBackPressed());
        binding.ivSearch.setOnClickListener(view1 -> onSearchClick());
        binding.imgLeft.setOnClickListener(view1 -> onLeftArrowClicked());
        binding.imgRight.setOnClickListener(view1 -> onRightArrowClicked());
    }

    @Override
    public void showError(String errorCause) {
        getParentActivity().onBackPressed();
        MethodUtils.showToastMessage(getParentActivity(), errorCause);
    }

    @Override
    public void showProgress() {
        if (binding.fragmentWeatherProgressBar.getVisibility() == View.GONE) {
            binding.fragmentWeatherProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideProgress() {
        if (binding.fragmentWeatherProgressBar.getVisibility() == View.VISIBLE) {
            binding.fragmentWeatherProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateCurrentWeather(WeatherItem weatherItem, String voiceResponse) {
        binding.editSearch.setText("");
        Calendar date = Calendar.getInstance();
        try {
            date.setTime(sdf.parse(weatherItem.getDate()));
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            currentWeatherDateInString = sdf2.format(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        bindData(weatherItem, voiceResponse);
    }

    @Override
    public void updateForecastWeather(List<WeatherItem> weatherItemList) {
        binding.editSearch.setText("");
//        weatherForecastAdapter.updateList(weatherItemList);
        Calendar today = Calendar.getInstance();
        if (weatherListDateMap == null)
            weatherListDateMap = new HashMap<>();
        if(currentWeatherList == null)
            currentWeatherList = new ArrayList<>();
        weatherListDateMap.clear();
        currentWeatherList.clear();
        Calendar currentAddedDate = Calendar.getInstance();
        for (WeatherItem item : weatherItemList) {
            Calendar date = Calendar.getInstance();
            try {
                date.setTime(sdf.parse(item.getDate()));
                if ((today.get(Calendar.DATE) - date.get(Calendar.DATE) == 1)) {
                    continue;
                }
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String dateInString = sdf2.format(date.getTime());
                if (weatherListDateMap.containsKey(dateInString)){
                    List<WeatherItem> list = weatherListDateMap.get(dateInString);
                    list.add(item);
                    weatherListDateMap.put(dateInString, list);
                } else {
                    List<WeatherItem> list = new ArrayList<>();
                    list.add(item);
                    weatherListDateMap.put(dateInString, list);
                }

                if (currentWeatherList.size() == 0) {
                    currentWeatherList.add(item);
                    currentAddedDate = date;
                } else {
                    if (currentAddedDate.get(Calendar.DATE) != date.get(Calendar.DATE)){
                        currentWeatherList.add(item);
                        currentAddedDate = date;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Log.e("date_check","date - " + weatherListDateMap.size());
        if (weatherListDateMap.containsKey(currentWeatherDateInString))
            weatherItemList = weatherListDateMap.get(currentWeatherDateInString);
        weatherForecastAdapter.updateList(weatherItemList);
        weatherAdapter = new WeatherAdapter(this);
        weatherAdapter.setWeatherItemList(currentWeatherList);
        binding.viewpagerWeather.setAdapter(weatherAdapter);
        binding.viewpagerWeather.addOnPageChangeListener(pageChangeListener);
    }

    void onSearchClick() {
        if(binding.editSearch.getText().toString().trim().equalsIgnoreCase("")){
            Toast.makeText(getActivity(), getString(R.string.enter_city_name), Toast.LENGTH_SHORT).show();
        }else {
            weatherPresenter.callWeatherApiByCity(binding.editSearch.getText().toString());
            MethodUtils.hideSoftInputKeyboard(binding.editSearch, getContext());
        }
    }

    void onLeftArrowClicked() {
        proceedToPreviousPage();
    }

    void onRightArrowClicked() {
        proceedToNextPage();
    }

    public void proceedToNextPage() {
        if (binding.viewpagerWeather.getCurrentItem() + 1 < weatherAdapter.getCount())
            binding.viewpagerWeather.setCurrentItem(binding.viewpagerWeather.getCurrentItem() + 1, true);
    }

    void proceedToPreviousPage() {
        if (binding.viewpagerWeather.getCurrentItem() - 1 >= 0)
            binding.viewpagerWeather.setCurrentItem(binding.viewpagerWeather.getCurrentItem() - 1, true);
    }

    private void bindData(WeatherItem data, String response) {
        String location = WeatherPresenter.getFormattedLocation(data.getCity());
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(location);

        String bgUrl = data.getBackgroundImage();
        if (bgUrl != null && !bgUrl.isEmpty()) {
            Picasso.get().load(bgUrl).into(binding.wallpaper);
        }
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(response);
        String tempInF = null;
        while(matcher.find())
        {
            tempInF = matcher.group(1);
            break;
        }
        if (!TextUtils.isEmpty(tempInF)) {
            response = response.replace(tempInF, WeatherPresenter.getConvertedFToCTemp(Double.parseDouble(tempInF)));
        }
        if (!WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "-1").equals(Constants.RC_DOMAIN_ID))
            getParentActivity().onRespondToUser(response);
    }

    void onRootClicked() {

    }

    void onBackPressed() {
        MethodUtils.hideSoftInputKeyboard(binding.editSearch, getContext());
        getParentActivity().onBackPressed();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        MethodUtils.hideSoftInputKeyboard(binding.editSearch, getContext());
        featureFragmentCleanUps();
        getParentActivity().stopTextToSpeech(true);
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        weatherPresenter.presenterCleanUps();
    }
}
