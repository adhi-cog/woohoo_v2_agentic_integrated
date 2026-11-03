package com.sb.woohoo.ui.onboarding.lang;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.custom.RecylerViewClickListener;
import com.sb.woohoo.databinding.FragmentLanguageSelectionBinding;
import com.sb.woohoo.ui.base.BaseFragment;
import com.sb.woohoo.ui.onboarding.OnboardingActivity;
import com.sb.woohoo.ui.onboarding.OnboardingViewPagerAdapter;
import com.sb.woohoo.ui.onboarding.lang.di.DaggerLanguageComponent;
import com.sb.woohoo.ui.onboarding.lang.di.LanguageComponent;
import com.sb.woohoo.ui.onboarding.lang.di.LanguageModule;

import java.util.List;

import javax.inject.Inject;

public class LanguageSelectionFragment extends BaseFragment implements LanguageContract.View, RecylerViewClickListener {

    @Inject
    LanguageAdapter languageAdapter;

    @Inject
    LanguagePresenter languagePresenter;


    private FragmentLanguageSelectionBinding binding;

    public static LanguageSelectionFragment newInstance() {
        Bundle args = new Bundle();
        LanguageSelectionFragment fragment = new LanguageSelectionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentLanguageSelectionBinding.inflate(inflater,container, false);

        LanguageComponent languageComponent = DaggerLanguageComponent.builder()
                .languageModule(new LanguageModule(this))
                .build();

        languageComponent.injectLanguageFragment(this);

        init();
        return binding.getRoot();
    }

    private void init() {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.listLanguage.setLayoutManager(layoutManager);
        binding.listLanguage.setAdapter(languageAdapter);

        languagePresenter.loadLanguages();


    }

    @Override
    public void showLanguages(List<String> languageList) {
        languageAdapter.updateList(languageList);
    }

    @Override
    public void showEmptyText() {
        //We can an emptyView and set it here
        //Right now I have added just
        binding.listLanguage.setVisibility(View.GONE);
        Toast.makeText(getActivity(), getString(R.string.no_languages), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideEmptyText() {

    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        //Change Fragment
        OnboardingActivity activity = ((OnboardingActivity) getActivity());
        if (activity != null) {
            activity.setCurrentItem(OnboardingViewPagerAdapter.FragmentOrder.FactoryReset.WIFI, true);
        }
    }

    @Override
    public String createTag() {
        return LanguageSelectionFragment.class.getSimpleName();
    }

}
