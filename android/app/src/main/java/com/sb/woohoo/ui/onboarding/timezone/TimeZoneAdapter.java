package com.sb.woohoo.ui.onboarding.timezone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sb.woohoo.R;
import com.sb.woohoo.custom.RecyclerViewEmptySupport;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by admin on 1/30/18.
 */

public class TimeZoneAdapter extends RecyclerViewEmptySupport.Adapter<TimeZoneAdapter.TimeZoneViewHolder> {

    private List<TimeZone> timeZoneList = new ArrayList<>(0);
    private TimeZoneSelectionFragment clickListener;

    public TimeZoneAdapter(TimeZoneSelectionFragment clickListener) {
        this.clickListener = clickListener;
    }

    public static String getTimeZone(TimeZone tz) {
        long hours = TimeUnit.MILLISECONDS.toHours(tz.getRawOffset());
        long minutes = TimeUnit.MILLISECONDS.toMinutes(tz.getRawOffset())
                - TimeUnit.HOURS.toMinutes(hours);
        minutes = Math.abs(minutes);
        String result;
        if (hours > 0) {
            result = String.format("GMT+%02d:%02d", hours, minutes);
        } else if (hours < 0) {
            result = String.format("GMT-%02d:%02d", hours, minutes);
        } else {
            result = String.format("GMT+00:00");
        }
        return result;
    }

    public void updateList(List<TimeZone> timeZoneList) {
        if (timeZoneList != null) {
            if (this.timeZoneList != null) {
                this.timeZoneList.clear();
                this.timeZoneList.addAll(timeZoneList);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public TimeZoneViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_time_zone, parent, false);
        return new TimeZoneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TimeZoneViewHolder holder, int position) {
        String timeZoneValue = getTimeZone(timeZoneList.get(position));
        String timeZonePlace = timeZoneList.get(position).getID();
        holder.gmtText.setText(timeZoneValue);
        holder.countryText.setText(timeZonePlace);
    }

    @Override
    public int getItemCount() {
        return timeZoneList == null ? 0 : timeZoneList.size();
    }

    public class TimeZoneViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView gmtText, countryText;

        public TimeZoneViewHolder(View itemView) {
            super(itemView);
            gmtText = itemView.findViewById(R.id.text_gmt);
            countryText = itemView.findViewById(R.id.text_country_standard);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            clickListener.onTimeZoneClicked(timeZoneList.get(clickedPosition));
        }
    }
}
