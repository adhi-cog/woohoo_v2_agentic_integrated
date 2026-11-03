package com.sb.woohoo.ui.main.fragments.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.ItemToDoBinding;
import com.sb.woohoo.databinding.ItemWeatherForecastBinding;
import com.sb.woohoo.ui.main.fragments.weather.pojo.response.WeatherItem;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastViewHolder> {

    private List<WeatherItem> forecastDataList = new ArrayList<>(0);
    private Context context;

    public void updateList(List<WeatherItem> newforecastDataList) {
        forecastDataList.clear();
        forecastDataList.addAll(newforecastDataList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WeatherForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        ItemWeatherForecastBinding binding = ItemWeatherForecastBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new WeatherForecastViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherForecastViewHolder holder, int position) {
        holder.onBind(forecastDataList.get(position), context);
    }

    @Override
    public int getItemCount() {
        return forecastDataList == null ? 0 : forecastDataList.size();
    }
}
