package com.sb.woohoo.ui.main.fragments.calllog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.CallLogViewHolder> {

    public interface Listener {
        void onEntryClicked(CallLogEntry entry);
    }

    private final List<CallLogEntry> entries = new ArrayList<>();
    private final Listener listener;
    private final DateFormat dateFormat = new SimpleDateFormat("MMM d, h:mm a", Locale.getDefault());

    public CallLogAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<CallLogEntry> newEntries) {
        entries.clear();
        if (newEntries != null) {
            entries.addAll(newEntries);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CallLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_call_log, parent, false);
        return new CallLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CallLogViewHolder holder, int position) {
        CallLogEntry entry = entries.get(position);
        holder.number.setText(entry.getNumber());
        String details = entry.getDirection() + " \u2022 " + dateFormat.format(new Date(entry.getTimestamp()));
        holder.details.setText(details);
        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onEntryClicked(entry);
            }
        });
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    static class CallLogViewHolder extends RecyclerView.ViewHolder {
        private final TextView number;
        private final TextView details;

        CallLogViewHolder(@NonNull View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.call_log_item_number);
            details = itemView.findViewById(R.id.call_log_item_details);
        }
    }
}
