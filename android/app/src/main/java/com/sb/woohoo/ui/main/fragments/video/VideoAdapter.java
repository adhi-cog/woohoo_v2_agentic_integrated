package com.sb.woohoo.ui.main.fragments.video;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.databinding.ItemVideoListBinding;
import com.sb.woohoo.ui.main.fragments.video.pojo.Video;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private List<Video> videoList = new ArrayList<>(0);
    private VideoListFragment videoListFragment;

    @Inject
    public VideoAdapter(VideoListFragment videoListFragment) {
        this.videoListFragment = videoListFragment;
    }

    List<Video> getVideosList() {
        return videoList;
    }

    Video getVideo(int ordinal) {
        return videoList.get(ordinal);
    }

    //List can be null but the response is success...
    public void updateList(@Nullable List<Video> newVideos)
    {
        if(newVideos != null )
        {
            this.videoList.clear();
            this.videoList.addAll(newVideos);
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemVideoListBinding binding = ItemVideoListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(videoList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ItemVideoListBinding binding;

        ViewHolder(ItemVideoListBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        public void bind(Video video, int position) {

            binding.itemVideoListImageViewVideoSnapshot.setTag(position);
            binding.itemVideoListTextViewName.setSingleLine();
            binding.itemVideoListTextViewName.setSelected(true);

            binding.itemVideoListTextViewName.setEllipsize(TextUtils.TruncateAt.MARQUEE);

            binding.itemVideoListTextViewSerialNo.setText(String.valueOf(position + 1));

            if (videoList.get(position).getSnippet() == null) {

                Picasso.get()
                        .load(videoList.get(position).getThumbnails().getMedium().getUrl())
                        .into(binding.itemVideoListImageViewVideoSnapshot);
                binding.itemVideoListTextViewName.setText(video.getTitle());
            } else {
                Picasso.get()
                        .load(videoList.get(position).getSnippet().getThumbnails().getMedium().getUrl())
                        .into(binding.itemVideoListImageViewVideoSnapshot);
                binding.itemVideoListTextViewName.setText(video.getSnippet().getTitle());
            }
            binding.itemVideoListImageViewVideoSnapshot.setOnClickListener(view -> videoListFragment.onPositionClicked((int) view.getTag()));
        }
    }
}
