package com.sb.woohoo.ui.main.fragments.feedback;


import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.sb.woohoo.R;
import com.sb.woohoo.custom.smileyrating.SmileyRating;
import com.sb.woohoo.databinding.ItemFeedbackBinding;
import com.sb.woohoo.ui.main.fragments.feedback.pojo.Question;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class FeedbackAdapter extends PagerAdapter {



    private List<Question> feedbackFragmentlines = new ArrayList<>(0);
    private Picasso picasso;
    private FeedbackFragment feedbackFragment;
    private ItemFeedbackBinding binding;

    @Inject
    public FeedbackAdapter(Picasso picasso, FeedbackFragment feedbackFragment) {
        this.picasso = picasso;
        this.feedbackFragment = feedbackFragment;
    }

    void updateFeedbackFragments(List<Question> questionList) {
        this.feedbackFragmentlines.clear();
        this.feedbackFragmentlines.addAll(questionList);

        notifyDataSetChanged();
    }

    public List<Question> getList() {
        return feedbackFragmentlines;
    }

    @Override
    public int getCount() {
        return feedbackFragmentlines.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        Question article = feedbackFragmentlines.get(position);

        // Instantiate Parent View.
        LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            binding = ItemFeedbackBinding.inflate(inflater, container, false);
        }


        binding.ratingBar.setNoOfSmileys(5);

        String imgUrl = article.getMediaUrle();
        String newsTitle = article.getQuestion();

        binding.feedbackText.setText(newsTitle);
        if (imgUrl == null || imgUrl.equalsIgnoreCase("")) {
            picasso.load(R.drawable.amadeus_house_keeping).into(binding.feedbackImg);
        } else {
            picasso.load(imgUrl).into(binding.feedbackImg);
        }

        container.addView(binding.getRoot());

        binding.ratingBar.setSmileySelectedListener(new SmileyRating.OnSmileySelectedListener() {
            @Override
            public void onSmileySelected(SmileyRating.Type type) {
                Question article = feedbackFragmentlines.get(fragpos);
                article.setRating(type.getRating());

            }
        });

        binding.editComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.scrollview.scrollTo(0, binding.scrollview.getBottom());
            }
        });
        binding.editComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Question article = feedbackFragmentlines.get(fragpos);
                article.setFeedback(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return binding.getRoot();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    private int fragpos = 0;

    public void updateUI(int recentPos) {
        this.fragpos = recentPos;
        Question article = feedbackFragmentlines.get(recentPos);

    }
}
