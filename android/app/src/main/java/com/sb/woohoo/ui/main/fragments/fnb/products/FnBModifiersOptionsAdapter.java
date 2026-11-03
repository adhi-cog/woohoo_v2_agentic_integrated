package com.sb.woohoo.ui.main.fragments.fnb.products;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.ui.main.fragments.fnb.products.pojo.Modifiers;

import java.util.ArrayList;
import java.util.List;

public class FnBModifiersOptionsAdapter extends RecyclerView.Adapter<FnBModifiersOptionsAdapter.MyViewHolder> {

    private List<Modifiers.ModifiersOptions> optionsList;
    private Activity context;
    private ProductRateUpdateListener productRateUpdateListener;
    private OptionsClickListener optionsClickListener;
    private String currency;
    private Modifiers modifiers;

    public FnBModifiersOptionsAdapter(Activity context, Modifiers modifiers, String currency,
                                      ProductRateUpdateListener productRateUpdateListener,OptionsClickListener optionsClickListener) {
        this.context = context;
        this.optionsList = modifiers.getOptions();
        this.modifiers = modifiers;
        this.currency = currency;
        this.productRateUpdateListener = productRateUpdateListener;
        this.optionsClickListener = optionsClickListener;
    }

    public void updateAdapter(List<Modifiers.ModifiersOptions> list){
        optionsList =  list;
        notifyDataSetChanged();
    }

    public List<Modifiers.ModifiersOptions> getAdapterList() {
        return optionsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_modifier_option, parent, false);
        return new MyViewHolder(view);
    }
    int numberOfCheckboxesChecked = 0;

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Modifiers.ModifiersOptions option = optionsList.get(position);

        holder.tvOptName.setText(option.getName());
        holder.tvOptRate.setText(currency + option.getPrice());

        modifiers.setNumberOfCheckboxesChecked(0);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked && numberOfCheckboxesChecked >= modifiers.maxSelection) {
                holder.checkBox.setChecked(false);
                optionsClickListener.onItemClick(modifiers);
            } else {
                if (isChecked) {
                    numberOfCheckboxesChecked++;
                } else {
                    numberOfCheckboxesChecked--;
                }
                modifiers.setNumberOfCheckboxesChecked(numberOfCheckboxesChecked);
                option.is_checked = isChecked;
                productRateUpdateListener.onProductRateChanged(isChecked, option.getPrice());
            }
        });

        holder.llCheckBoxContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.checkBox.setChecked(!holder.checkBox.isChecked());
            }
        });

        // Set Value from data
        holder.checkBox.setChecked(option.getIs_checked());
    }

    @Override
    public int getItemCount() {
        return optionsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvOptName, tvOptRate;
        private CheckBox checkBox;
        private LinearLayout llCheckBoxContainer;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOptName = itemView.findViewById(R.id.tvOptName);
            tvOptRate = itemView.findViewById(R.id.tvOptRate);
            checkBox = itemView.findViewById(R.id.checkBox);
            llCheckBoxContainer = itemView.findViewById(R.id.llCheckBoxContainer);
        }
    }

    public interface ProductRateUpdateListener {
        void onProductRateChanged(boolean isChecked, String price);
    }

    public interface OptionsClickListener {
        void onItemClick(Modifiers modifiers);

    }
}
