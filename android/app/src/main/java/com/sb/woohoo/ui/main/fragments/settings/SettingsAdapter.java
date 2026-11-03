package com.sb.woohoo.ui.main.fragments.settings;

import static com.sb.woohoo.ui.main.fragments.settings.SettingsPresenter.SETTINGS_UPDATE;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.databinding.ItemSettingsBinding;

import java.util.List;

import javax.inject.Inject;

public class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Setting> settingsList;
    private OnClickListener clickListener;

    @Inject
    SettingsAdapter(SettingsFragment settingsFragment, List<Setting> settingsList) {
        this.clickListener = settingsFragment;
        this.settingsList = settingsList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        RecyclerView.ViewHolder viewHolder;
        ItemSettingsBinding binding = ItemSettingsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        viewHolder = new ViewHeader(binding);
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Setting setting = settingsList.get(i);
        ((ViewHeader) viewHolder).onBind(setting);
    }

    @Override
    public int getItemCount() {
        return settingsList.size();
    }

    class ViewHeader extends RecyclerView.ViewHolder {


        public ItemSettingsBinding binding;

        ViewHeader(ItemSettingsBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        public void onBind(Setting setting) {
            binding.settingIcon.setImageResource(setting.getIcon());
            binding.settingName.setText(setting.getSettingsName());

            if (setting.getFragmentType() == SETTINGS_UPDATE && setting.isLoading()) {
                binding.settingContent.setVisibility(View.INVISIBLE);
                binding.progressContent.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.progressContent.setText(setting.getSettingsContent());
            } else {
                binding.settingContent.setVisibility(View.VISIBLE);
                binding.settingContent.setText(setting.getSettingsContent());
                binding.progressContent.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.INVISIBLE);
            }

            binding.itemSettings.setOnClickListener(view -> clickListener.onItemClicked(setting));
        }
    }
}
