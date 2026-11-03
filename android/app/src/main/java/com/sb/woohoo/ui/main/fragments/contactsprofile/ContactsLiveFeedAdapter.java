package com.sb.woohoo.ui.main.fragments.contactsprofile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.LiveFeedItemBinding;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.Hub;
import com.sb.woohoo.utils.others.MethodUtils;

import java.util.List;

public class ContactsLiveFeedAdapter extends RecyclerView.Adapter<ContactsLiveFeedAdapter.LiveFeedViewHolder> {

    private List<Hub> hubList;
    private LiveFeedClickListener liveFeedClickListener;

    public ContactsLiveFeedAdapter(List<Hub> dropInList, LiveFeedClickListener liveFeedClickListener) {
        this.hubList = dropInList;
        this.liveFeedClickListener = liveFeedClickListener;

    }

    @NonNull
    @Override
    public LiveFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LiveFeedItemBinding binding = LiveFeedItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new LiveFeedViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull LiveFeedViewHolder holder, int position) {
        holder.bindView(hubList.get(position));/*Bind Views for the item */
    }

    /*Returns RemoteAlertModel Count*/
    @Override
    public int getItemCount() {
        return hubList != null ? hubList.size() : 0;
    }

    public interface LiveFeedClickListener {
        void onItemClicked(Hub hub);
    }


    /**
     * View Holder for the RemoteAlertModel View
     **/
    public class LiveFeedViewHolder extends RecyclerView.ViewHolder {

        public LiveFeedItemBinding binding;

        LiveFeedViewHolder(LiveFeedItemBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        public void bindView(final Hub hub) {

            binding.tvDeviceName.setText(hub.getName()!=null && !hub.getName().equalsIgnoreCase("")
                    ? MethodUtils.setWordWithCaps(hub.getName()):"WooHoo Hub");
            binding.rlItemLayout.setOnClickListener(view -> {
                if (liveFeedClickListener != null) {
                    liveFeedClickListener.onItemClicked(hub);
                }
            });
        }
    }
}
