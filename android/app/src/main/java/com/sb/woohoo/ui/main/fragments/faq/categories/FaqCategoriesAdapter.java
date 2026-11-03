package com.sb.woohoo.ui.main.fragments.faq.categories;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.LayoutFaqCategoriesBinding;
import com.sb.woohoo.ui.main.fragments.faq.categories.pojo.FaqCategory;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FaqCategoriesAdapter extends RecyclerView.Adapter<FaqCategoriesAdapter.CategoriesViewHolder> {

    private Context context;
    private Picasso mPicasso;
    private List<FaqCategory> mList = new ArrayList<>();
    private FaqCategoryItemClickListener faqCategoryItemClickListener;

    public FaqCategoriesAdapter(Context context, Picasso mPicasso, List<FaqCategory> list, FaqCategoryItemClickListener faqCategoryItemClickListener){
        this.context = context;
        this.mPicasso = mPicasso;
        this.mList = list;
        this.faqCategoryItemClickListener = faqCategoryItemClickListener;
    }

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutFaqCategoriesBinding binding = LayoutFaqCategoriesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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

    public void updateListAndNotify(List<FaqCategory> list){
        this.mList = list;
        notifyDataSetChanged();
    }
    class CategoriesViewHolder extends RecyclerView.ViewHolder{

        public LayoutFaqCategoriesBinding binding;

        CategoriesViewHolder(LayoutFaqCategoriesBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        public void bindViews(FaqCategory faqCategory) {
            binding.tvGridItemTitle.setText(faqCategory.getCategoryName());
            if (!TextUtils.isEmpty(faqCategory.getCategoryIcon())) {
                mPicasso.load(faqCategory.getCategoryIcon()).into(binding.ivGridItemIcon);
                binding.ivGridItemIcon.setColorFilter(ContextCompat.getColor(context, R.color.white));
            }
            binding.rlDrawerGridItem.setOnClickListener(v -> faqCategoryItemClickListener.onCategoryItemClick(faqCategory));
        }
    }

    public interface FaqCategoryItemClickListener{
        void onCategoryItemClick(FaqCategory faqCategory);
    }
}
