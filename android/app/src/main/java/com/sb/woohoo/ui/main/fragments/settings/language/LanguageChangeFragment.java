package com.sb.woohoo.ui.main.fragments.settings.language;

import static com.sb.woohoo.data.prefs.SharedPrefsKey.PREF_INTRO_VIDEO_KEY;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.BAD_GATEWAY_ERROR_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.EMPTY_RESULT_CODE;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.databinding.FragmentLanguageChangeBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.settings.language.di.DaggerLanguageComponent;
import com.sb.woohoo.ui.main.fragments.settings.language.di.LanguageComponent;
import com.sb.woohoo.ui.main.fragments.settings.language.di.LanguageModule;
import com.sb.woohoo.ui.main.fragments.settings.language.pojo.LanguageData;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.LocaleHelper;
import com.sb.woohoo.utils.others.MethodUtils;

import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

public class LanguageChangeFragment extends FeatureFragment<MainActivity> implements LanguageContracts.Views {

    private FragmentLanguageChangeBinding binding;

    public static LanguageChangeFragment newInstance() {
        return new LanguageChangeFragment();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        LanguageComponent languageComponent = DaggerLanguageComponent
                .builder()
                .languageModule(new LanguageModule(this))
                .build();
        languageComponent.injectLanguageChangeFragment(this);
    }

    @Override
    public void bindViews(View view) {
        binding.commonToolbarLayout.commonToolbarTitle.setText("Language Change");
        binding.commonToolbarLayout.commonToolbarBack.setOnClickListener(view1 -> onBackPressed());
    }

    @Override
    public String getCategory() {
        return LanguageChangeFragment.class.getSimpleName();
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    @Inject
    LanguagePresenter mLanguagePresenter;

    LanguageAdapter languageAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLanguageChangeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();
        mLanguagePresenter.getAllLanguage();
    }

    private void initViews() {
        languageAdapter = new LanguageAdapter(getActivity(), WooHooApp.get(getActivity()).getWooHooAppComponent().getPicasso(), new ArrayList<>(), data -> {
            if(!Objects.equals(LocaleHelper.getLanguage(getActivity()), data.getLanguageCode())) {
                if(data != null && data.getIntroVideo() != null){
                    WooHooApp.getSharedPrefsHelper().setIntroVideo(PREF_INTRO_VIDEO_KEY, data.getIntroVideo());
                }
                LocaleHelper.setLocale(getParentActivity(), data.getLanguageCode());
                Intent intent = getParentActivity().getIntent();
                getParentActivity().finish();
                startActivity(intent);
            }else{
                getParentActivity().onBackPressed();
            }

        });
        binding.recyclerView.setAdapter(languageAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getParentActivity()));

    }


    void onRootClicked() {

    }

    void onBackPressed() {
        getParentActivity().onBackPressed();
    }

    @Override
    public void onError(int errorCode) {
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
    public void onAllLocaleFetch(ArrayList<LanguageData> data) {
        ArrayList<LanguageData> dataNew = new ArrayList<>(data);
        String selectedLang = LocaleHelper.getLanguage(getActivity());

        for (int i=0; i<dataNew.size(); i++){
            dataNew.get(i).setSelectedLang(Objects.equals(dataNew.get(i).getLanguageCode(), selectedLang));
        }
        languageAdapter.updateListAndNotify(dataNew);
    }
}

