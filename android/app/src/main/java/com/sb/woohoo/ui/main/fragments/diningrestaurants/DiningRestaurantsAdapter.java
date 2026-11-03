package com.sb.woohoo.ui.main.fragments.diningrestaurants;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.ItemDiningRestaurantBinding;
import com.sb.woohoo.ui.main.fragments.diningrestaurants.pojo.DiningRestaurant;
import com.sb.woohoo.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DiningRestaurantsAdapter extends RecyclerView.Adapter<DiningRestaurantsAdapter.RestaurantsViewHolder> {

    private Context context;
    private List<DiningRestaurant> mList;
    private RestaurantItemClickListener restaurantItemClickListener;

    public DiningRestaurantsAdapter(Context context, List<DiningRestaurant> list, RestaurantItemClickListener restaurantItemClickListener){
        this.context = context;
        this.mList = list;
        this.restaurantItemClickListener = restaurantItemClickListener;
    }

    public void updateRestaurantsList(List<DiningRestaurant> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RestaurantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDiningRestaurantBinding binding = ItemDiningRestaurantBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RestaurantsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantsViewHolder holder, int position) {
        holder.bindViews(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class RestaurantsViewHolder extends RecyclerView.ViewHolder{

        public ItemDiningRestaurantBinding binding;

        RestaurantsViewHolder(ItemDiningRestaurantBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        public void bindViews(DiningRestaurant diningRestaurant) {
            if (TextUtils.isEmpty(diningRestaurant.getUrl())) {
                if (!TextUtils.isEmpty(diningRestaurant.getType()) && diningRestaurant.getType().equalsIgnoreCase(Constants.TABLE_CHECK)) {
                    binding.txtBookTable.setVisibility(View.VISIBLE);
                    binding.txtBookTable.setText(context.getString(R.string.tablecheck));
                } else
                    binding.txtBookTable.setVisibility(View.GONE);
            } else {
                binding.txtBookTable.setVisibility(View.VISIBLE);
                binding.txtBookTable.setText(context.getString(R.string.book_a_table));
            }
            binding.txtRestaurantName.setText(diningRestaurant.getName());
            binding.txtRestaurantDesc.setText(diningRestaurant.getDescription());
            if (!TextUtils.isEmpty(diningRestaurant.getImageUrl())) {
                Picasso.get().load(diningRestaurant.getImageUrl()).into(binding.imgRestaurant);
            }
            binding.txtBookTable.setOnClickListener(view -> restaurantItemClickListener.onRestaurantItemClick(diningRestaurant));

        }
    }

    public interface RestaurantItemClickListener{
        void onRestaurantItemClick(DiningRestaurant diningRestaurant);
    }
}
