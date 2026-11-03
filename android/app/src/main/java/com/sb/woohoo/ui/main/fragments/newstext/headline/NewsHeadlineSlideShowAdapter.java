package com.sb.woohoo.ui.main.fragments.newstext.headline;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.sb.woohoo.databinding.ItemNewsHeadlineBinding;
import com.sb.woohoo.ui.main.fragments.newstext.headline.pojo.Article;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class NewsHeadlineSlideShowAdapter extends PagerAdapter {


    private List<Article> newsHeadlines = new ArrayList<>(0);
    private Picasso picasso;
    private NewsHeadlineFragment newsHeadlineFragment;
    private ItemNewsHeadlineBinding binding;

    @Inject
    public NewsHeadlineSlideShowAdapter(Picasso picasso, NewsHeadlineFragment newsHeadlineFragment) {
        this.picasso = picasso;
        this.newsHeadlineFragment = newsHeadlineFragment;
    }

    void updateNewsHeadlines(List<Article> newsHeadlines) {
        this.newsHeadlines.clear();
        for (int i = 0; i < newsHeadlines.size() && i < 10; i++) {
            this.newsHeadlines.add(newsHeadlines.get(i));
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return newsHeadlines.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        Article article = newsHeadlines.get(position);

        // Instantiate Parent View.
        LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            binding = ItemNewsHeadlineBinding.inflate(inflater, container, false);
        }


        binding.newsHeadlineCardView.setOnClickListener(v -> newsHeadlineFragment.newsDetailShow(article));

        String imgUrl = article.getUrlToImage();
        String newsTitle = article.getTitle();

        binding.newsHeadlineText.setText(newsTitle);
        picasso.load(imgUrl).into(binding.newsHeadlineImg);

        container.addView(binding.getRoot());

        return binding.getRoot();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
