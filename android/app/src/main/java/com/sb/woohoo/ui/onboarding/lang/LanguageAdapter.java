package com.sb.woohoo.ui.onboarding.lang;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sb.woohoo.R;
import com.sb.woohoo.custom.RecyclerViewEmptySupport;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by admin on 1/20/18.
 */

public class LanguageAdapter extends RecyclerViewEmptySupport.Adapter<LanguageAdapter.LanguageViewHolder> {

    private List<String> languageList = new ArrayList<>(0);
    private LanguageSelectionFragment clickListener;

    public LanguageAdapter(LanguageSelectionFragment clickListener) {
        this.clickListener = clickListener;
    }

    public void updateList(List<String> languageList) {
        if (languageList != null) {
            if (this.languageList != null) {
                this.languageList.clear();
            }
            if (this.languageList != null) {
                this.languageList.addAll(languageList);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public LanguageAdapter.LanguageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_language, parent, false);
        return new LanguageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LanguageAdapter.LanguageViewHolder holder, int position) {
        holder.languageText.setText(languageList.get(position));
    }

    @Override
    public int getItemCount() {
        return languageList == null ? 0 : languageList.size();
    }

    public class LanguageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView languageText;

        LanguageViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            languageText = itemView.findViewById(R.id.text_language);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            clickListener.onListItemClick(clickedPosition);
        }
    }
}
