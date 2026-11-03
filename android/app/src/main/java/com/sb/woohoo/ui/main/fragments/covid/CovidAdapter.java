package com.sb.woohoo.ui.main.fragments.covid;

import static com.sb.woohoo.ui.main.fragments.covid.CovidFragment.mapListCovid;
import static com.sb.woohoo.ui.main.fragments.covid.CovidFragment.recentPos;

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
import com.sb.woohoo.databinding.ItemCovidBinding;
import com.sb.woohoo.ui.main.fragments.covid.pojo.Covid;
import com.sb.woohoo.ui.main.fragments.covid.pojo.Question;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class CovidAdapter extends PagerAdapter {


    int currentPosition = 0;

    private List<Question> covidFragmentlines = new ArrayList<>(0);
    private Picasso picasso;
    private CovidFragment covidFragment;
    ItemCovidBinding binding;

    @Inject
    public CovidAdapter(Picasso picasso, CovidFragment covidFragment) {
        this.picasso = picasso;
        this.covidFragment = covidFragment;
    }

    void updateFeedbackFragments(List<Question> newsHeadlines) {
        this.covidFragmentlines.clear();
        this.covidFragmentlines.addAll(newsHeadlines);

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return covidFragmentlines.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        Question article = covidFragmentlines.get(position);

        // Instantiate Parent View.

        LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            binding = ItemCovidBinding.inflate(inflater, container, false);
        }

        binding.ratingBar.setNoOfSmileys(2);

        String imgUrl = article.getMediaUrle();
        String newsTitle = article.getQuestion();

        binding.covidText.setText(newsTitle);
        if (imgUrl == null || imgUrl.equalsIgnoreCase("")) {
            picasso.load(R.drawable.amadeus_house_keeping).into(binding.covidImg);
        } else {
            picasso.load(imgUrl).into(binding.covidImg);
        }

        container.addView(binding.getRoot());
        Covid covid = mapListCovid.get(recentPos);
        if (covid != null && covid.rating > 0)
            binding.ratingBar.setRating(covid.rating, true);
        else
            binding.ratingBar.setRating(2,true);

        binding.ratingBar.setSmileySelectedListener(new SmileyRating.OnSmileySelectedListener() {
            @Override
            public void onSmileySelected(SmileyRating.Type type) {

                Covid covid = mapListCovid.get(recentPos);
                if (covid == null) {
                    covid = new Covid();
                }
                Log.e("rating_bar", "onSmileyChanged - " + type.getRating());
                covid.rating = type.getRating();
                mapListCovid.put(recentPos, covid);
            }
        });
        covid = mapListCovid.get(position);
        if (covid != null && covid.value != null)
            binding.editComment.setText("" + covid.value);

        binding.editComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Covid covid = mapListCovid.get(recentPos);
                if (covid == null) {
                    covid = new Covid();
                }
                covid.value = charSequence.toString();
                mapListCovid.put(recentPos, covid);
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

    public void updateUI(int recentPos) {
        Covid covid = mapListCovid.get(recentPos);
        if (covid != null && covid.rating > 0)
            binding.ratingBar.setRating(covid.rating, true);
    }
}
