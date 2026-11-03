package com.sb.woohoo.ui.main.fragments.alarm.alarm;

import static com.sb.woohoo.ui.main.fragments.alarm.alarm.AlarmPresenter.DATE_PATTERN;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.ItemAlarmBinding;
import com.sb.woohoo.ui.main.fragments.alarm.pojo.Alarm;
import com.sb.woohoo.utils.others.SingleClickListener;

import org.joda.time.DateTimeComparator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHeader> {

    private List<Alarm> alarmList;
    private AlarmFragment alarmFragment;

    @Inject
    AlarmAdapter(AlarmFragment alarmFragment,List<Alarm> alarmList) {
        this.alarmList = alarmList;
        this.alarmFragment = alarmFragment;
    }

    public static String getTime(String time) {
        if (time != null && !time.isEmpty()) {
            SimpleDateFormat df = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
            Date date;
            String outputTime;
            try {
                date = df.parse(time);
                outputTime = outputFormat.format(date);
                return outputTime.replace("AM", "am").replace("PM", "pm");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @NonNull
    @Override
    public ViewHeader onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemAlarmBinding binding = ItemAlarmBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHeader(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHeader holder, int position) {
        holder.setIsRecyclable(false);
        Alarm alarm = alarmList.get(position);
        holder.onBind(alarm, position);

    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    private String getDay(Alarm alarm) {
        if (alarm.isEveryday()) {
            return "Everyday";
        }
        Calendar calendar = Calendar.getInstance();
        Calendar currentDate = Calendar.getInstance();
        DateTimeComparator comparator = DateTimeComparator.getTimeOnlyInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);
        try {
            if (alarm.getDateTime() != null && !alarm.getDateTime().isEmpty()) {
                calendar.setTime(dateFormat.parse(alarm.getDateTime()));
            } else {
                return null;
            }
            if (comparator.compare(calendar, currentDate) > 0) {
                return alarmFragment.getString(R.string.today);
            } else {
                return alarmFragment.getString(R.string.tomorrow);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    class ViewHeader extends RecyclerView.ViewHolder {

        public ItemAlarmBinding binding;

        ViewHeader(ItemAlarmBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        public void onBind(Alarm alarm, int position) {
            binding.alarmSRNo.setText(String.valueOf(position + 1));

            String day = getDay(alarm);
            binding.alarmDay.setText(day != null ? day : alarmFragment.getString(R.string.null_object));

            String time = getTime(alarm.getDateTime());
            binding.alarmTime.setText(time != null ? time : alarmFragment.getString(R.string.null_object));

            if (position == getItemCount() - 1) {
                binding.viewDivider.setVisibility(View.GONE);
            }

            binding.btnDelete.setOnClickListener(new SingleClickListener()
            {
                @Override
                public void onSingleClick(@NonNull View v) {
                    alarmFragment.onDeleteAlarmClick(alarm,position+1);
                }
            });

            binding.btnUpdate.setOnClickListener(new SingleClickListener()
            {
                @Override
                public void onSingleClick(@NonNull View v) {
                    alarmFragment.onUpdateAlarmClick(alarm, position);
                }
            });
        }
    }
}
