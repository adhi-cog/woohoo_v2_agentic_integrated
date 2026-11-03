package com.sb.woohoo.ui.main.fragments.faq.questions;

import static com.sb.woohoo.ui.main.helpers.NetworkHelper.BAD_GATEWAY_ERROR_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.EMPTY_RESULT_CODE;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.databinding.FragmentFaqQnaBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.faq.questions.di.DaggerFaqQnAComponent;
import com.sb.woohoo.ui.main.fragments.faq.questions.di.FaqQnAComponent;
import com.sb.woohoo.ui.main.fragments.faq.questions.di.FaqQnAModule;
import com.sb.woohoo.ui.main.fragments.faq.questions.pojo.AnswerData;
import com.sb.woohoo.ui.main.fragments.faq.questions.pojo.FaqQuestion;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class FaqQnAFragment extends FeatureFragment<MainActivity> implements FaqQnAContract.Views{
    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;

    @Inject
    AnalyticsHelper helper;
    @Inject
    Picasso picasso;
    @Inject
    FaqQnAPresenter faqQnAPresenter;
    private FaqQnAAdapter faqQnAAdapter;
    private String categoryId = "";
    private String categoryName = "";
    private FragmentFaqQnaBinding binding;

    public static FaqQnAFragment newInstance() {
        FaqQnAFragment fragment = new FaqQnAFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.FaqQnA(),  null);
        binding = FragmentFaqQnaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (binding.hotelLogo != null && !TextUtils.isEmpty(((MainActivity) requireActivity()).getHotelLogo())){
            picasso.load(((MainActivity)getActivity()).getHotelLogo()).into(binding.hotelLogo);
        }

        if (getArguments() != null){
            categoryName = getArguments().getString("category_name");
            categoryId = getArguments().getString("category_id");
        }
        binding.commonToolbarTitle.setText(getString(R.string.faq) + "-" + categoryName);
        initViews();
        faqQnAPresenter.getFaqQuestions(categoryId);
    }

    private void initViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.ItemAnimator animator = binding.rvFaq.getItemAnimator();
        if (animator instanceof DefaultItemAnimator) {
            ((DefaultItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        binding.rvFaq.setLayoutManager(layoutManager);

    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        FaqQnAComponent faqQnAComponent = DaggerFaqQnAComponent.builder()
                .faqQnAModule(new FaqQnAModule(this)).build();
        faqQnAComponent.injectFaqQnAFragment(this);
    }

    @Override
    public void bindViews(View view) {
       binding.commonToolbarBack.setOnClickListener(view1 -> onBack());
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public BasePresenter getPresenter() {
        return faqQnAPresenter;
    }

    void onBack() {
        getParentActivity().onBackPressed();
    }

    @Override
    public void onQuestionsResponse(List<FaqQuestion> list) {
        Log.e("onQuestionsResponse","list size - " + list.size());
        List<QuestionExpandableGroup> mList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            List<AnswerData> answerlist = new ArrayList<>();
            answerlist.add(list.get(i).getAnswer().getAnswerData());
            mList.add(new QuestionExpandableGroup(list.get(i).getQuestion(), answerlist));
        }
        faqQnAAdapter = new FaqQnAAdapter(mList);
        binding.rvFaq.setAdapter(faqQnAAdapter);

    }

    @Override
    public void onErrorResponse(int errorCode) {
        if (errorCode == EMPTY_RESULT_CODE) {
            MethodUtils.showToastMessage(getContext(),  getString(R.string.no_result_fetched));
        } else if (errorCode == BAD_GATEWAY_ERROR_CODE) {
            MethodUtils.showToastMessage(getContext(),  getString(R.string.bad_gateway));
        } else {
            MethodUtils.showToastMessage(getContext(),  getString(R.string.try_again));
        }
        onBack();
    }

    @Override
    public void showProgress() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        faqQnAPresenter.presenterCleanUps();
    }
}
