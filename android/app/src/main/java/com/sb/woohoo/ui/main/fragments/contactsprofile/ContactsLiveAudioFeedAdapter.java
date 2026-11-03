package com.sb.woohoo.ui.main.fragments.contactsprofile;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.databinding.LiveFeedItemBinding;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.Hub;
import com.sb.woohoo.utils.others.MethodUtils;

import java.util.List;

public class ContactsLiveAudioFeedAdapter extends RecyclerView.Adapter<ContactsLiveAudioFeedAdapter.LiveFeedViewHolder> {

    private List<Hub> hubList;
    private LiveFeedAudioClickListener liveFeedAudioClickListener;

    public ContactsLiveAudioFeedAdapter(List<Hub> dropInList, LiveFeedAudioClickListener liveFeedAudioClickListener) {
        this.hubList = dropInList;
        this.liveFeedAudioClickListener = liveFeedAudioClickListener;

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

    public interface LiveFeedAudioClickListener {
        void onItemClickedAudio(Hub hub);
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
            binding.tvDeviceName.setText(hub.getName() != null ? MethodUtils.setWordWithCaps(hub.getName()) : "WooHoo Hub");
            binding.rlItemLayout.setOnClickListener(view -> {
                if (liveFeedAudioClickListener != null) {
                    liveFeedAudioClickListener.onItemClickedAudio(hub);
                }
            });
        }
    }
}
