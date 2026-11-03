package com.sb.woohoo.ui.main.fragments.interel;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.LayoutInterelUtilityItemBinding;
import com.sb.woohoo.ui.main.fragments.interel.pojo.DynamicUtility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class InterelUtilityRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<DynamicUtility> itemList = new ArrayList<>(0);
    private InterelUtilityCallBackListener interelUtilityCallBackListener;
    private int DndIndex = -1;
    private int MurIndex = -1;
    private View view;

    @Inject
    InterelUtilityRVAdapter(InterelUtilityFragment interelUtilityFragment) {
        this.interelUtilityCallBackListener = interelUtilityFragment;
    }

    public void updateList(Activity context, List<DynamicUtility> itemList) {
        this.itemList.clear();
        this.itemList.addAll(itemList);
//        for(int i=0;i<itemList.size();i++){
//            if(itemList.get(i).getUtilityName().equals(context.getString(R.string.do_not_disturb))){
//                DndIndex = i;
//            }
//            if(itemList.get(i).getUtilityName().equals(context.getString(R.string.make_up_room))){
//                MurIndex = i;
//            }
//        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInterelUtilityItemBinding binding = LayoutInterelUtilityItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new InterelUtilityItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        InterelUtilityItemViewHolder interelUtilityItemViewHolder = (InterelUtilityItemViewHolder) holder;
        interelUtilityItemViewHolder.bindViews(itemList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    /**********************************{@INTEREL_ITEM_VIEW_HOLDER}***********************************************/

    class InterelUtilityItemViewHolder extends RecyclerView.ViewHolder {

        private DynamicUtility dynamicUtility;
        private int currentStateId;

        public LayoutInterelUtilityItemBinding binding;

        InterelUtilityItemViewHolder(LayoutInterelUtilityItemBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
            binding.rlItemLayout.setOnClickListener(view -> onItemClicked());
        }

        public void bindViews(DynamicUtility dynamicUtility, int position) {
            this.dynamicUtility = dynamicUtility;
            if (dynamicUtility.getStates() != null && !dynamicUtility.getStates().isEmpty()) {
                String stateName = "";
                for (int i = 0; i < dynamicUtility.getStates().size(); i++) {
                    if (!Boolean.parseBoolean(dynamicUtility.getCurrentState()) == Boolean.parseBoolean(dynamicUtility.getStates().get(i).getStateValue())) {
                        stateName = dynamicUtility.getStates().get(i).getStateName();
                        currentStateId = dynamicUtility.getStates().get(i).getId();
                        break;
                    }
                }
                binding.tvUtilityItemTitle.setText(stateName);
            } else
                binding.tvUtilityItemTitle.setText(dynamicUtility.getItemName());
            if (!TextUtils.isEmpty(dynamicUtility.getImageUrl())) {
                Picasso.get().load(dynamicUtility.getImageUrl()).into(binding.ivUtilityIcon);
            } else if (dynamicUtility.getDrawableImageIcon() != 0){
                binding.ivUtilityIcon.setImageResource(dynamicUtility.getDrawableImageIcon());
            } else {
                binding.ivUtilityIcon.setImageResource(R.drawable.ic_dnd);
            }
            updateUI(this.dynamicUtility);
        }

        private void updateUI(DynamicUtility dynamicUtility) {
//            if (dynamicUtility.getUtilityName().equalsIgnoreCase(view.getContext().getString(R.string.fan_mode))) {
//                tvUtilityModeStatus.setVisibility(View.VISIBLE);
//                tvUtilityModeStatus.setText(dynamicUtility.getMode());
//            } else {
//                tvUtilityModeStatus.setVisibility(View.GONE);
//            }
            if (Boolean.parseBoolean(dynamicUtility.getCurrentState())) {
                binding.ivUtilityIcon.setColorFilter(ContextCompat.getColor(binding.ivUtilityIcon.getContext(), R.color.cyan_light), PorterDuff.Mode.SRC_IN);
            } else {
                binding.ivUtilityIcon.setColorFilter(ContextCompat.getColor(binding.ivUtilityIcon.getContext(), R.color.white), PorterDuff.Mode.SRC_IN);
            }
        }
        void onItemClicked() {
            if (dynamicUtility != null) {
                if (currentStateId != 0)
                    interelUtilityCallBackListener.onUtilityItemClicked(String.valueOf(currentStateId));
                else
                    interelUtilityCallBackListener.onUtilityItemClicked(binding.tvUtilityItemTitle.getText().toString(), Boolean.parseBoolean(dynamicUtility.getCurrentState()));

                dynamicUtility.setCurrentState(String.valueOf(!Boolean.parseBoolean(dynamicUtility.getCurrentState())));
//
//                if(interelUtilityItemModel.getUtilityName().equals(view.getContext().getString(R.string.do_not_disturb)) && interelUtilityItemModel.isClicked()){
//                    itemList.get(MurIndex).setClicked(false);
//                }
//                if(interelUtilityItemModel.getUtilityName().equals(view.getContext().getString(R.string.make_up_room)) && interelUtilityItemModel.isClicked()){
//                    itemList.get(DndIndex).setClicked(false);
//                }

                notifyDataSetChanged();
            }
        }
    }
}
