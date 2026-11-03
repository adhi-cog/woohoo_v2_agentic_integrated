package com.sb.woohoo.ui.main.fragments.livefeedowndevice;

import static com.sileria.android.Resource.getResources;

import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.custom.CircleImageTransform;
import com.sb.woohoo.databinding.HeaderLayoutBinding;
import com.sb.woohoo.databinding.HubItemBinding;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.Header;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.Hub;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.OtherFamilyData;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class OwnerHubListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int HEADER_VIEW = 0, ITEM_VIEW = 1;
    private final Picasso picasso;
    private List hubList = new ArrayList<>(0);
    private OwnerHubListAdapter.HubClickListener hubListClickListener;

    @Inject
    public OwnerHubListAdapter(OwnerHubListAdapter.HubClickListener hubClickListener, Picasso picasso) {
        this.hubListClickListener = hubClickListener;
        this.picasso = picasso;
    }

    public void updateList(List listOfHubs) {
        hubList.clear();
        hubList.addAll(listOfHubs);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEADER_VIEW) {
            HeaderLayoutBinding binding = HeaderLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new HeaderViewHolder(binding);
        } else {
            HubItemBinding binding = HubItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new HubItemViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).onBind((Header) hubList.get(position));
        } else {
            if (hubList.get(position) instanceof OtherFamilyData) {
                ((HubItemViewHolder) holder).onBindViews((OtherFamilyData) hubList.get(position));
            } else {
                ((HubItemViewHolder) holder).onBindViews((Hub) hubList.get(position));
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (hubList.get(position) instanceof Header) {
            return HEADER_VIEW;
        } else {
            return ITEM_VIEW;
        }
    }

    @Override
    public int getItemCount() {
        return hubList != null ? hubList.size() : 0;
    }

    private void setImage(ImageView ivDeviceIcon, String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            picasso.load("https://app.mywoohoo.io" + imageUrl)
                    .fit()
                    .transform(new CircleImageTransform())
                    .error(R.drawable.contact_no_image)
                    .placeholder(R.drawable.contact_no_image)
                    .into(ivDeviceIcon);
        } else {
            picasso.load(R.drawable.contact_no_image)
                    .fit()
                    .transform(new CircleImageTransform())
                    .error(R.drawable.contact_no_image)
                    .placeholder(R.drawable.contact_no_image)
                    .into(ivDeviceIcon);
        }
    }

    private void setImage(View ivDeviceIcon) {
        TypedArray images = getResources().obtainTypedArray(R.array.icon_list);
        int choice = (int) (Math.random() * images.length());
        ivDeviceIcon.setBackgroundResource(images.getResourceId(choice, R.drawable.ic_device_1));
        images.recycle();
    }

    /*-----------------------Hub Click Listener-----------------------------------*/
    public interface HubClickListener {
        void onItemClicked(Hub hub);

        void onOtherHubItemClicked(OtherFamilyData otherFamilyData);
    }

    /*---------------------------ITEM VIEW HOLDER-----------------------------*/
    public class HubItemViewHolder extends RecyclerView.ViewHolder {

        public HubItemBinding binding;

        HubItemViewHolder(HubItemBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        public void onBindViews(final Hub hub) {
            binding.tvDeviceName.setText(MethodUtils.setWordWithCaps(hub.getName()));
            setImage(binding.ivDeviceIcon);
            binding.ivDeviceIcon.setPadding(15, 15, 15, 15);
            if (hub.getConnected() != null && hub.getConnected()) {
                binding.tvConnectionStatus.setText(binding.tvConnectionStatus.getContext().getString(R.string.connected));
                binding.tvConnectionStatus.setTextColor(MethodUtils.getColorFromTheme(itemView.getContext(), R.attr.text_color_secondary));
                binding.ivBlip.setVisibility(View.GONE);
            } else {
                binding.tvConnectionStatus.setText(binding.tvConnectionStatus.getContext().getString(R.string.not_connected));
                binding.tvConnectionStatus.setTextColor(MethodUtils.getColorFromTheme(itemView.getContext(), R.attr.text_color_secondary));
                binding.ivBlip.setVisibility(View.VISIBLE);
            }
            binding.rlItemLayout.setOnClickListener(view -> {
                if (hubListClickListener != null) {
                    hubListClickListener.onItemClicked(hub);
                }
            });
        }

        public void onBindViews(OtherFamilyData otherFamilyData) {

            binding.tvDeviceName.setText(otherFamilyData.getFirstName() + " " + otherFamilyData.getLastName());
            binding.tvDeviceName.setTextSize(24);
            binding.tvConnectionStatus.setVisibility(View.GONE);
            binding.ivBlip.setVisibility(View.GONE);

            setImage(binding.ivDeviceIcon, otherFamilyData.getImage());

            binding.rlItemLayout.setOnClickListener(v -> {
                if (hubListClickListener != null) {
                    hubListClickListener.onOtherHubItemClicked(otherFamilyData);
                }
            });
        }
    }

    /*******************************HEADER VIEW HOLDER****************************/
    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderLayoutBinding binding;

        HeaderViewHolder(HeaderLayoutBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        public void onBind(Header header) {
            binding.tvHeader.setText(header.getHeader());
        }
    }

}
