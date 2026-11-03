package com.sb.woohoo.ui.main.fragments.faq.categories;

import static com.sb.woohoo.ui.main.helpers.NetworkHelper.BAD_GATEWAY_ERROR_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.EMPTY_RESULT_CODE;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.databinding.FragmentFaqCategoriesBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.faq.categories.di.DaggerFaqComponent;
import com.sb.woohoo.ui.main.fragments.faq.categories.di.FaqComponent;
import com.sb.woohoo.ui.main.fragments.faq.categories.di.FaqModule;
import com.sb.woohoo.ui.main.fragments.faq.categories.pojo.FaqCategory;
import com.sb.woohoo.ui.main.fragments.faq.questions.FaqQnAFragment;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class FaqCategoriesFragment extends FeatureFragment<MainActivity> implements FaqCategoriesContract.Views{

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;

    FaqCategoriesAdapter faqCategoriesAdapter;
    @Inject
    AnalyticsHelper helper;
    @Inject
    Picasso picasso;
    @Inject
    FaqCategoriesPresenter faqCategoriesPresenter;
    private List<FaqCategory> categoriesList = new ArrayList<>();
    private FragmentFaqCategoriesBinding binding;

    public static FaqCategoriesFragment newInstance() {
        FaqCategoriesFragment fragment = new FaqCategoriesFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.FaqCategories(),  null);
        binding = FragmentFaqCategoriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (binding.hotelLogo != null && !TextUtils.isEmpty(((MainActivity) requireActivity()).getHotelLogo())){
            picasso.load(((MainActivity)getActivity()).getHotelLogo()).into(binding.hotelLogo);
        }
        initViews();
        faqCategoriesPresenter.faqCategoriesRequest();
    }

    private void initViews() {
        faqCategoriesAdapter = new FaqCategoriesAdapter(getActivity(), WooHooApp.get(getActivity()).getWooHooAppComponent().getPicasso(), categoriesList, faqCategory -> {
            Bundle bundle = new Bundle();
            bundle.putString("category_id", String.valueOf(faqCategory.getId()));
            bundle.putString("category_name", String.valueOf(faqCategory.getCategoryName()));
            FaqQnAFragment faqQnAFragment = FaqQnAFragment.newInstance();
            faqQnAFragment.setArguments(bundle);
            getParentActivity().switchFragmentUsingReplace(faqQnAFragment);
        });
        binding.rvFaq.setAdapter(faqCategoriesAdapter);
        binding.rvFaq.setLayoutManager(new GridLayoutManager(getActivity(), 2));
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void onCategoriesResponse(List<FaqCategory> list) {
        if (list != null && !list.isEmpty()){
            categoriesList.clear();
            categoriesList.addAll(list);
            faqCategoriesAdapter.updateListAndNotify(categoriesList);
        }
    }

    @Override
    public void onErrorResponse(int errorCode) {
        if (errorCode == EMPTY_RESULT_CODE) {
            MethodUtils.showToastMessage(getContext(), getString(R.string.no_result_fetched));
        } else if (errorCode == BAD_GATEWAY_ERROR_CODE) {
            MethodUtils.showToastMessage(getContext(), getString(R.string.bad_gateway));
        } else {
            MethodUtils.showToastMessage(getContext(), getString(R.string.try_again));
        }
        getParentActivity().onBackPressed();
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
    public void initDagger() {
        FaqComponent faqComponent = DaggerFaqComponent.builder()
                .faqModule(new FaqModule(this)).build();
        faqComponent.injectFaqCategoriesFragment(this);
    }

    @Override
    public void bindViews(View view) {
        binding.commonToolbarBack.setOnClickListener(view1 -> onBack());
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public BasePresenter getPresenter() {
        return faqCategoriesPresenter;
    }

    void onBack() {
        MethodUtils.hideSoftInputKeyboard(getView(), getParentActivity());
        getParentActivity().onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getParentActivity().stopTextToSpeech(false);
        featureFragmentCleanUps();
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        faqCategoriesPresenter.presenterCleanUps();
    }
}
