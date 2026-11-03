package com.sb.woohoo.ui.main.fragments.weather;

import static com.sb.woohoo.ui.main.fragments.weather.WeatherFragment.sdf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.ItemWeatherBinding;
import com.sb.woohoo.ui.main.fragments.weather.pojo.response.WeatherItem;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class WeatherAdapter extends PagerAdapter {

    int currentPosition = 0;

    private List<WeatherItem> weatherItemList = new ArrayList<>();
    private Picasso picasso;
    private WeatherFragment weatherFragment;
    private ItemWeatherBinding binding;

    @Inject
    public WeatherAdapter(WeatherFragment weatherFragment) {
        this.picasso = picasso;
        this.weatherFragment = weatherFragment;
    }

    void setWeatherItemList(List<WeatherItem> weatherItemList) {
        this.weatherItemList.clear();
        this.weatherItemList.addAll(weatherItemList);
    }

    void updateWeatherFragments(List<WeatherItem> weatherItemList) {
        this.weatherItemList.clear();
        this.weatherItemList.addAll(weatherItemList);

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return weatherItemList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        WeatherItem data = weatherItemList.get(position);

        // Instantiate Parent View.
        LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            binding = ItemWeatherBinding.inflate(inflater, container, false);
        }

        binding.fragmentWeatherTextViewCurrentTemp.setText(WeatherPresenter.getConvertedFToCTemp(data.getTemperature()));
        binding.fragmentWeatherTextViewCurrentTempType.setText(WeatherPresenter.UNIT_TEMPS_C);
        binding.fragmentWeatherTextViewHighTemp.setText(WeatherPresenter.getFormattedTemperatureInCel(WeatherPresenter.getConvertedFToCTemp(data.getTempMax())));
        binding.fragmentWeatherTextViewLowTemp.setText(WeatherPresenter.getFormattedTemperatureInCel(WeatherPresenter.getConvertedFToCTemp(data.getTempMin())));

        String desc = data.getDescription();
        binding.fragmentWeatherTextViewWeatherText.setText(desc);

        Calendar today = Calendar.getInstance();
        Calendar date = Calendar.getInstance();
        try {
            date.setTime(sdf.parse(data.getDate()));
            if ((today.get(Calendar.DATE) - date.get(Calendar.DATE)) == 1){
                binding.fragmentWeatherTextViewWeatherDate.setText(weatherFragment.getString(R.string.Yesterday));
            } else if (today.get(Calendar.DATE) == date.get(Calendar.DATE)){
                binding.fragmentWeatherTextViewWeatherDate.setText(weatherFragment.getString(R.string.today));
            } else if ((date.get(Calendar.DATE) - today.get(Calendar.DATE)) == 1) {
                binding.fragmentWeatherTextViewWeatherDate.setText(weatherFragment.getString(R.string.tomorrow));
            } else {
                SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMMM, yyyy", Locale.ENGLISH);
                binding.fragmentWeatherTextViewWeatherDate.setText(sdf2.format(date.getTime()));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        container.addView(binding.getRoot());
        return binding.getRoot();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
