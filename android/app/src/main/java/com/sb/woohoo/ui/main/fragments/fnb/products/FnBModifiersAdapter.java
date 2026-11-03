package com.sb.woohoo.ui.main.fragments.fnb.products;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.ui.main.fragments.fnb.products.pojo.Modifiers;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FnBModifiersAdapter extends RecyclerView.Adapter<FnBModifiersAdapter.MyViewHolder> implements FnBModifiersOptionsAdapter.ProductRateUpdateListener,
        FnBModifiersOptionsAdapter.OptionsClickListener {

    private List<Modifiers> modifiersList;
    private Activity context;
    private ProductRateUpdateListener productClickedListener;
    private String currency;

    private OptionsClickListener optionsClickListener;

    public FnBModifiersAdapter(Activity context, List<Modifiers> modifiersList, String currency, ProductRateUpdateListener productClickedListener,
                               OptionsClickListener optionsClickListener){
        this.context = context;
        this.modifiersList = modifiersList;
        this.productClickedListener = productClickedListener;
        this.optionsClickListener= optionsClickListener;
        this.currency = currency;
    }

    public void updateAdapter(List<Modifiers> list){
        modifiersList =  list;
        notifyDataSetChanged();
    }

    public List<Modifiers> getAdapterList(){
        return modifiersList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_modifier, parent, false);
        return new MyViewHolder(view);
    }
    FnBModifiersOptionsAdapter fnBModifiersOptionsAdapter;

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Modifiers modifier = modifiersList.get(position);

        if (modifier.getName() != null) {
            if (modifier.isMandatory())
                holder.title.setText(modifier.getName());
            else
                holder.title.setText(modifier.getName() + context.getString(R.string.str_optional));
        }

        holder.rvOptions.setLayoutManager(new GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false));
        fnBModifiersOptionsAdapter =  new  FnBModifiersOptionsAdapter(context, modifier, currency, this,this);
        holder.rvOptions.setAdapter(fnBModifiersOptionsAdapter);
    }

    @Override
    public int getItemCount() {
        return modifiersList.size();
    }

    @Override
    public void onProductRateChanged(boolean isChecked, String price) {
        productClickedListener.onProductRateChanged(isChecked, price);
    }

    @Override
    public void onItemClick(Modifiers modifiers) {
        optionsClickListener.onItemClick(modifiers);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView title;
        private RecyclerView rvOptions;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            rvOptions = itemView.findViewById(R.id.rvOptions);
        }
    }

    public interface ProductRateUpdateListener {
        void onProductRateChanged(boolean isChecked, String price);
    }
    public interface OptionsClickListener {
        void onItemClick(Modifiers modifiers);
    }
}
