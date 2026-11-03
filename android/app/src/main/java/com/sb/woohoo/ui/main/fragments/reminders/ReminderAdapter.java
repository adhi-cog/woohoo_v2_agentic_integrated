package com.sb.woohoo.ui.main.fragments.reminders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.HeaderDateBinding;
import com.sb.woohoo.databinding.ItemReminderListBinding;
import com.sb.woohoo.ui.main.fragments.reminders.pojo.Reminder;

import java.util.List;

import javax.inject.Inject;

class ReminderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_HEADER = 0;
    private final int TYPE_SINGLE = 1;
    private final int TYPE_MIDDLE = 2;
    private final int TYPE_TOP = 3;
    private final int TYPE_BOTTOM = 4;
    private List<Reminder> remindersList;


    @Inject
    ReminderAdapter(List<Reminder> remindersList) {
        this.remindersList = remindersList;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position) == 1) {
            return TYPE_SINGLE;
        } else if (isPositionHeader(position) == 2) {
            return TYPE_MIDDLE;
        } else if (isPositionHeader(position) == 3) {
            return TYPE_TOP;
        } else if (isPositionHeader(position) == 4) {
            return TYPE_BOTTOM;
        }
        return TYPE_HEADER;
    }

    private int isPositionHeader(int position) {
        return remindersList.get(position).getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder reminderViewHolder;
        if (viewType == TYPE_HEADER) {
            HeaderDateBinding binding = HeaderDateBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            reminderViewHolder = new ViewHeader(binding);
        } else {
            ItemReminderListBinding binding = ItemReminderListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            reminderViewHolder = new ViewItem(binding);
        }
        return reminderViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        Reminder reminder = remindersList.get(position);

        if (holder instanceof ViewItem) {
            ((ViewItem) holder).onBind(reminder);
        } else if (holder instanceof ViewHeader) {
            ((ViewHeader) holder).onBind(reminder);
        }
    }

    @Override
    public int getItemCount() {
        return remindersList.size();
    }

    class ViewItem extends RecyclerView.ViewHolder {


        public ItemReminderListBinding binding;

        ViewItem(ItemReminderListBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        void onBind(Reminder reminder) {
            binding.itemReminderTitle.setText(reminder.getReminder());
            binding.itemReminderTime.setText(reminder.getReminderTime());

            int viewType = ReminderAdapter.this.getItemViewType(remindersList.indexOf(reminder));

            switch (viewType) {
                case TYPE_BOTTOM:
                    ((View) binding.itemReminderTitle.getParent()).setBackgroundResource(R.drawable.bg_bottom);
                    break;

                case TYPE_MIDDLE:
                    binding.itemReminderDivider.setVisibility(View.VISIBLE);
                    ((View) binding.itemReminderTitle.getParent()).setBackgroundResource(R.drawable.bg_middle);
                    break;

                case TYPE_SINGLE:
                    ((View) binding.itemReminderTitle.getParent()).setBackgroundResource(R.drawable.bg_single);
                    break;

                case TYPE_TOP:
                    binding.itemReminderDivider.setVisibility(View.VISIBLE);
                    ((View) binding.itemReminderTitle.getParent()).setBackgroundResource(R.drawable.bg_first);
                    break;
            }
        }
    }

    class ViewHeader extends RecyclerView.ViewHolder {

        public HeaderDateBinding binding;

        ViewHeader(HeaderDateBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        public void onBind(Reminder reminder) {
            binding.date.setText(reminder.getReminderDate());
        }
    }
}
