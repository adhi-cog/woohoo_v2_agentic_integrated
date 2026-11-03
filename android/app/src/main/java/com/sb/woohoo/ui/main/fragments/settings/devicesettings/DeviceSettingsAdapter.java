package com.sb.woohoo.ui.main.fragments.settings.devicesettings;

import static com.sb.woohoo.ui.main.fragments.settings.devicesettings.DeviceSettingsFragment.DEVICE_NAME;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.databinding.ItemDeviceSettingsBinding;

import java.util.List;

import javax.inject.Inject;

public class DeviceSettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<DeviceSetting> deviceSettingsList;
    private OnClickListener clickListener;

    @Inject
    DeviceSettingsAdapter(DeviceSettingsFragment deviceSettingsFragment, List<DeviceSetting> deviceSettingsList) {
        this.clickListener = deviceSettingsFragment;
        this.deviceSettingsList = deviceSettingsList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        RecyclerView.ViewHolder viewHolder;
        ItemDeviceSettingsBinding binding = ItemDeviceSettingsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        viewHolder = new DeviceSettingsAdapter.ViewHeader(binding);
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        DeviceSetting setting = deviceSettingsList.get(i);
        ((DeviceSettingsAdapter.ViewHeader) viewHolder).onBind(setting);
    }

    @Override
    public int getItemCount() {
        return deviceSettingsList.size();
    }

    class ViewHeader extends RecyclerView.ViewHolder {

        public ItemDeviceSettingsBinding binding;

        ViewHeader(ItemDeviceSettingsBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        public void onBind(DeviceSetting setting) {
            binding.deviceSettingName.setText(setting.getDeviceSettingName());

            if (setting.getAction() == DEVICE_NAME && setting.isLoading()) {
                binding.deviceSettingsContent.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.progressContent.setVisibility(View.VISIBLE);
                binding.progressContent.setText(setting.getDeviceSettingContent());
                binding.progressContent.setTextColor(Color.parseColor("#50e3c2"));
            } else {
                binding.deviceSettingsContent.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
                binding.progressContent.setVisibility(View.GONE);
                binding.deviceSettingsContent.setText(setting.getDeviceSettingContent());
            }

            binding.rootItem.setOnClickListener(view -> clickListener.onItemClicked(setting));
        }
    }
}
