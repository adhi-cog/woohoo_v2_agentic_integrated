package com.sb.woohoo.ui.main.fragments.overview.item;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.LayoutOverviewCategoriesBinding;
import com.sb.woohoo.ui.main.fragments.overview.Category;
import com.sb.woohoo.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class OverviewItemsAdapter extends RecyclerView.Adapter<OverviewItemsAdapter.CategoriesViewHolder> {

    private Context context;
    private Picasso mPicasso;
    private List<Category> mList = new ArrayList<>();
    private CategoryItemClickListener categoryItemClickListener;

    public OverviewItemsAdapter(Context context, Picasso mPicasso, List<Category> list, CategoryItemClickListener categoryItemClickListener) {
        this.context = context;
        this.mPicasso = mPicasso;
        this.mList = list;
        this.categoryItemClickListener = categoryItemClickListener;
    }

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutOverviewCategoriesBinding binding = LayoutOverviewCategoriesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoriesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position) {
        holder.bindViews(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateListAndNotify(List<Category> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    class CategoriesViewHolder extends RecyclerView.ViewHolder {

        public LayoutOverviewCategoriesBinding binding;

        CategoriesViewHolder(LayoutOverviewCategoriesBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        public void bindViews(Category category) {
            binding.txtTitle.setText(category.getName());
            binding.txtDescription.setText(category.getDescription());
            if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
                mPicasso.load(category.getImageUrl()).into(binding.imgCategory);
            } else {
                mPicasso.cancelRequest(binding.imgCategory);
                binding.imgCategory.setImageDrawable(null);
            }
            if (WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "-1").equals(Constants.GBTB_DOMAIN_ID)) {
                binding.txtTitle.setTextSize(context.getResources().getDimension(R.dimen.sp_24));
                binding.txtDescription.setTextSize(context.getResources().getDimension(R.dimen.sp_22));
            } else {
                binding.txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.sp_18));
                binding.txtDescription.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.sp_16));
            }
            binding.layoutCategories.setOnClickListener(v -> categoryItemClickListener.onCategoryItemClick(category));
            binding.imgNext.setVisibility(View.VISIBLE);
            binding.txtTitle.setMaxLines(1);
            binding.txtDescription.setMaxLines(1);

        }
    }

    public interface CategoryItemClickListener {
        void onCategoryItemClick(Category faqCategory);
    }
}
