package com.sb.woohoo.ui.main.fragments.remote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.databinding.ItemTvRemoteBinding;
import com.sb.woohoo.ui.main.fragments.remote.pojo.TVRemoteChannelData;

import java.util.List;

public class TVRemoteAdapter extends RecyclerView.Adapter<TVRemoteAdapter.TVRemoteViewHolder> {

    private Context context;
    private List<TVRemoteChannelData> mList;
    private TVRemoteItemClickListener tVRemoteItemClickListener;

    public TVRemoteAdapter(Context context, List<TVRemoteChannelData> list, TVRemoteItemClickListener tVRemoteItemClickListener){
        this.context = context;
        this.mList = list;
        this.tVRemoteItemClickListener = tVRemoteItemClickListener;
    }

    public void updateTVRemoteChannelDataList(List<TVRemoteChannelData> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TVRemoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTvRemoteBinding binding = ItemTvRemoteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TVRemoteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TVRemoteViewHolder holder, int position) {
        holder.bindViews(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class TVRemoteViewHolder extends RecyclerView.ViewHolder{
        public ItemTvRemoteBinding binding;
        TVRemoteViewHolder(ItemTvRemoteBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        public void bindViews(TVRemoteChannelData tVRemoteChannelData) {
            binding.txtName.setText(tVRemoteChannelData.getName());
            binding.layoutTvRemote.setOnClickListener(view ->
                    tVRemoteItemClickListener.onTVRemoteItemClick(tVRemoteChannelData));
        }
    }

    public interface TVRemoteItemClickListener{
        void onTVRemoteItemClick(TVRemoteChannelData tVRemoteChannelData);
    }
}
