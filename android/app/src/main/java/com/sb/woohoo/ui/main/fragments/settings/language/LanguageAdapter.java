package com.sb.woohoo.ui.main.fragments.settings.language;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.databinding.LayoutLanguageBinding;
import com.sb.woohoo.ui.main.fragments.settings.language.pojo.LanguageData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LanguageAdapterViewHolder> {

    private Context context;
    private Picasso mPicasso;
    private ArrayList<LanguageData> mList = new ArrayList<>();
    private LanguageItemClickListener languageItemClickListener;

    public LanguageAdapter(Context context, Picasso mPicasso, ArrayList<LanguageData> list, LanguageItemClickListener languageItemClickListener) {
        this.context = context;
        this.mPicasso = mPicasso;
        this.mList = list;
        this.languageItemClickListener = languageItemClickListener;
    }

    @NonNull
    @Override
    public LanguageAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutLanguageBinding binding = LayoutLanguageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new LanguageAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageAdapterViewHolder holder, int position) {
        holder.bindViews(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateListAndNotify(ArrayList<LanguageData> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    class LanguageAdapterViewHolder extends RecyclerView.ViewHolder {

        public LayoutLanguageBinding binding;

        LanguageAdapterViewHolder(LayoutLanguageBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        public void bindViews(LanguageData data) {
            binding.langText.setText(data.getLabel() + " ("+data.getLanguageCode()+")");
            binding.langRadio.setChecked(data.getSelectedLang());
            binding.langRadio.setOnClickListener(v -> languageItemClickListener.onLanguageItemClick(data));

            binding.layoutLang.setOnClickListener(v -> languageItemClickListener.onLanguageItemClick(data));
        }
    }

    public interface LanguageItemClickListener {
        void onLanguageItemClick(LanguageData category);
    }
}
