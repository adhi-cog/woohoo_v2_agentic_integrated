package com.sb.woohoo.ui.main.fragments.settings.wifi;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.ItemWifiBinding;
import com.sb.woohoo.ui.main.fragments.settings.wifi.pojo.WifiNetworkDetails;

/**
 * Created by Shrey on 05/03/18.
 */

public class WifiViewHolder extends RecyclerView.ViewHolder {

    private WifiFragment wifiFragment;

    public ItemWifiBinding binding;

    WifiViewHolder(ItemWifiBinding itemRowBinding,  WifiFragment wifiFragment) {
        super(itemRowBinding.getRoot());
        this.binding = itemRowBinding;
        this.wifiFragment = wifiFragment;
    }

    public void bind(WifiNetworkDetails wifi, boolean connected, boolean isEnd) {

        binding.title.setText(wifi.getWifiNetwork().SSID);
        if (connected) {
            binding.wifiStatus.setVisibility(View.VISIBLE);
            binding.wifiStatus.setText(wifiFragment.getString(R.string.connected));
        } else {
            binding.wifiStatus.setVisibility(View.GONE);
        }
        // Wifi security icon
        if (wifiFragment.getWifiPresenter().isSecure(wifi.getWifiNetwork())) {
            binding.securityIcon.setImageResource(R.drawable.ic_wifi_secure);
        } else {
            binding.securityIcon.setImageResource(R.drawable.ic_wifi_open);
        }
        // Hide the separator for the last item
        if (isEnd) {
            binding.listSeparator.setVisibility(View.INVISIBLE);
        } else {
            binding.listSeparator.setVisibility(View.VISIBLE);
        }
        // Set up on item click
        View listItem = ((View) binding.title.getParent());
        listItem.setTag(wifi);
        listItem.setOnClickListener(view -> wifiFragment.onItemClick((WifiNetworkDetails) view.getTag()));
    }

}
