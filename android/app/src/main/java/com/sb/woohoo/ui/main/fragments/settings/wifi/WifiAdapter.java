package com.sb.woohoo.ui.main.fragments.settings.wifi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.ItemToDoBinding;
import com.sb.woohoo.databinding.ItemWifiBinding;
import com.sb.woohoo.ui.main.fragments.settings.wifi.pojo.WifiNetworkDetails;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.recyclerview.widget.RecyclerView;

public class WifiAdapter extends RecyclerView.Adapter<WifiViewHolder> {

    private List<WifiNetworkDetails> wifiList = new ArrayList<>(0);
    private LayoutInflater layoutInflater;
    private WifiFragment wifiFragment;

    @Inject
    public WifiAdapter(LayoutInflater layoutInflater, WifiFragment wifiFragment) {
        this.layoutInflater = layoutInflater;
        this.wifiFragment = wifiFragment;
    }

    public void updateList(List<WifiNetworkDetails> newList) {
        wifiList.clear();
        wifiList.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public WifiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemWifiBinding binding = ItemWifiBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new WifiViewHolder(binding, wifiFragment);
    }

    @Override
    public void onBindViewHolder(WifiViewHolder wifiViewHolder, int position) {
        wifiViewHolder.bind(wifiList.get(position), wifiList.get(position).isConnected(), position >= wifiList.size() - 1);
        wifiViewHolder.setIsRecyclable(false);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return wifiList.size();
    }

    public interface onItemClickListener {
        void onItemClick(WifiNetworkDetails selectedWifi);
    }
}
