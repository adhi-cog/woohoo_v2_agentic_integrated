package com.sb.woohoo.ui.main.fragments.weather;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.databinding.ItemWeatherForecastBinding;
import com.sb.woohoo.ui.main.fragments.weather.pojo.response.WeatherItem;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class WeatherForecastViewHolder extends RecyclerView.ViewHolder {

    public ItemWeatherForecastBinding binding;

    WeatherForecastViewHolder(ItemWeatherForecastBinding itemRowBinding) {
        super(itemRowBinding.getRoot());
        this.binding = itemRowBinding;
    }

    void onBind(WeatherItem forecastData, Context context) {
        String temp;
        //if (WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "").equalsIgnoreCase(Constants.RC_DOMAIN_ID)){
            temp = WeatherPresenter.getFormattedTemperatureInCel(WeatherPresenter.getConvertedFToCTemp(forecastData.getTemperature()));
        /*}else {
            temp = WeatherPresenter.getFormattedTemperatureInFah(forecastData.getTemperature());
        }*/
        binding.itemWeatherForecastTemp.setText(temp);
        String dateTime = forecastData.getDate();
        String time = getFormattedTime(dateTime);
        if (time != null) {
            binding.itemWeatherForecastTime.setText(time);
        }
        String iconUrl = forecastData.getIcon();
        if (iconUrl != null && !iconUrl.isEmpty()) {
            Picasso.get().load(iconUrl).into( binding.itemWeatherForecastIcon);
        }
    }

    private String getFormattedTime(String dateTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat newSimpleDateFormat = new SimpleDateFormat("hh:mm aa");
        String output;
        try {
            Date date = simpleDateFormat.parse(dateTime);
            output = newSimpleDateFormat.format(date);
            return output;
        } catch (ParseException e) {
            LoggerWrapper.e(WeatherForecastViewHolder.class.getSimpleName(), e.getMessage());
            return null;
        }
    }
}
