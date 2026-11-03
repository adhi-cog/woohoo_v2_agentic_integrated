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
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.ui.main.fragments.fnb.categories.pojo.FnBCategoriesResponse;
import com.sb.woohoo.ui.main.fragments.fnb.products.pojo.FnBProductsResponse;
import com.sb.woohoo.ui.main.fragments.fnb.products.pojo.Product;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FnBProductsAdapter extends RecyclerView.Adapter<FnBProductsAdapter.MyViewHolder> {

    private final ArrayList<Product> productCartList;
    private List<Product> categoriesList;
    private Activity context;
    private ProductClickedListener productClickedListener;
    private int mHeight = 0;

    public FnBProductsAdapter(Activity context, List<Product> categoriesList, ArrayList<Product> productCartList, ProductClickedListener productClickedListener){
        this.context = context;
        this.categoriesList = categoriesList;
        this.productClickedListener = productClickedListener;
        this.productCartList = productCartList;
    }

    public void updateAdapter(List<Product> list){
        categoriesList =  list;
        notifyDataSetChanged();
    }

    public List<Product> getAdapterList(){
        return categoriesList;
    }

    public void updateLayoutItemHeight(int height){
        this.mHeight = height;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fnb_products, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Product product= categoriesList.get(position);

        if (product.getName()!=null)
            holder.txtCategory.setText(product.getName());

        if (product.getImages() !=null && product.getImages().size() > 0){
            Picasso.get().load((String) product.getImages().get(0)).into(holder.imgCategory);
            holder.imgCategory.setBackgroundColor(MethodUtils.getColorFromTheme(context, R.attr.color_primary));
        } else{
            holder.imgCategory.setBackgroundColor(MethodUtils.getColorFromTheme(context, R.attr.color_primary));
        }

        holder.imgCategory.setOnClickListener(view -> {
            productClickedListener.onProductClicked(view,product, position);
        });

        if (getProductCountFromSelectedList(product) == 0){
            holder.imgAddZeroCart.setVisibility(View.VISIBLE);
            holder.layoutQuantity.setVisibility(View.GONE);
        } else {
            holder.txtQuantity.setText("" + getProductCountFromSelectedList(product));
            holder.imgAddZeroCart.setVisibility(View.GONE);
            holder.layoutQuantity.setVisibility(View.VISIBLE);
        }

        if (product.getDescription()!=null && !product.getDescription().isEmpty()){
            holder.txt_details.setVisibility(View.VISIBLE);
        }else{
            holder.txt_details.setVisibility(View.GONE);
        }

        holder.imgAddZeroCart.setOnClickListener(view -> productClickedListener.onProductClicked(view,product, position));

        holder.imgMinus.setOnClickListener(view -> productClickedListener.onProductClicked(view,product, position));

        holder.imgAdd.setOnClickListener(view -> productClickedListener.onProductClicked(view,product, position));

        holder.txt_details.setOnClickListener(view -> productClickedListener.onProductClicked(view,product, position));

        if (mHeight != 0) {
            Log.e("item_category","mHeight - " + mHeight);
            ViewGroup.LayoutParams params = holder.item_category.getLayoutParams();
            params.height = mHeight;
//            params.width = holder.item_category.getWidth();
            holder.item_category.setLayoutParams(params);
        }

        if(product.getStock() == 0) {
            holder.outOfStockTextView.setVisibility(View.VISIBLE);
            holder.imgAddZeroCart.setVisibility(View.GONE);
            holder.imgCategory.setAlpha(0.6f);
        } else {
            holder.outOfStockTextView.setVisibility(View.GONE);
            holder.imgAddZeroCart.setVisibility(View.VISIBLE);
            holder.imgCategory.setAlpha(1.0f);
        }

        if(getProductCountFromSelectedList(product) < 1)
            holder.imgAddZeroCart.setVisibility(View.VISIBLE);
        else
            holder.imgAddZeroCart.setVisibility(View.GONE);
    }

    private int getProductCountFromSelectedList(Product product) {
        int count = 0;
        for(Product p : productCartList)
            if(p.getId() == product.getId())
                count += p.getSelectedQuantity();
        return count;
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgCategory;
        private TextView outOfStockTextView;
        private TextView txtCategory;
        private TextView txtQuantity, txt_details;
        private ImageView imgAdd, imgMinus, imgAddZeroCart;
        private LinearLayout layoutQuantity;
        private RelativeLayout item_category;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategory = itemView.findViewById(R.id.imgCategory);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            imgAdd = itemView.findViewById(R.id.imgAdd);
            imgMinus = itemView.findViewById(R.id.imgMinus);
            imgAddZeroCart = itemView.findViewById(R.id.imgAddZeroCart);
            layoutQuantity = itemView.findViewById(R.id.layout_quantity);
            txt_details = itemView.findViewById(R.id.txt_details);
            item_category = itemView.findViewById(R.id.item_category);
            outOfStockTextView = itemView.findViewById(R.id.txt_out_of_stock);

        }
    }

    public interface ProductClickedListener{
        void onProductClicked(View view, Product product, int position);
    }
}
