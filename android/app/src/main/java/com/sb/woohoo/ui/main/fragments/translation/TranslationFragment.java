package com.sb.woohoo.ui.main.fragments.translation;

import static com.sb.woohoo.utils.others.LangUtils.DEFAULT_LANG;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.databinding.FragmentTranslationBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.translation.di.DaggerTranslationComponent;
import com.sb.woohoo.ui.main.fragments.translation.di.TranslationComponent;
import com.sb.woohoo.ui.main.fragments.translation.di.TranslationModule;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.pojo.translate.TranslateResponse;
import com.sb.woohoo.utils.others.LangUtils;

import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;


/**
 * Audio Call.
 * This needs to be updated
 *
 * @author subham satyam.
 */
public class TranslationFragment
        extends FeatureFragment<MainActivity>
        implements TranslationContracts.View {

    private static final String BUNDLE_PARAMETER = "input";

    // Presenter.
    @Inject
    TranslationPresenter mPresenter;
    @Inject
    AnalyticsHelper helper;
    private IntentInfo mIntentInfo;

    private Boolean alterSourtarg = false;

    private ArrayList<String> orginalLangList = new ArrayList<>();
    private String source = "English";
    private String target = "Hindi";
    private int sourcePos = 0, targetPos = 0;
    private FragmentTranslationBinding binding;

    public TranslationFragment() { /*Required empty public constructor*/ }

    public static TranslationFragment newInstance() {
        return new TranslationFragment();
    }

    @Override
    public String createTag() {
        return TranslationFragment.class.getSimpleName();
    }

    /*********************************************** Fragment Callback Methods ***********************************************/

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    public static TranslationFragment newInstance(IntentInfo intentInfo) {
        TranslationFragment fragment = new TranslationFragment();
        Bundle args = new Bundle();
        args.putSerializable(BUNDLE_PARAMETER, intentInfo);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Injects the {@link TranslationFragment} to {@link TranslationComponent}.
     */
    @Override
    public void initDagger() {
        TranslationComponent component =
                DaggerTranslationComponent
                        .builder()
                        .translationModule(new TranslationModule(this))
                        .build();
        component.injectTranslationFragment(this);
    }

    /**
     * Initializes the Views.
     *
     * @param view View of {@link TranslationFragment}.
     */
    @Override
    public void bindViews(View view) {
        binding.fragmentToDoToolbar.commonToolbarBack.setOnClickListener(view1 -> onBackPressed());
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        helper.track(new Screen.Translation(),  null);
        binding = FragmentTranslationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setBundleParameters();

//        mSpinner.setOnItemSelectedListener(this);
        binding.spinner.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        ArrayAdapter aa = new ArrayAdapter(getParentActivity(), R.layout.spinner_top_item, orginalLangList);
        aa.setDropDownViewResource(R.layout.spinner_item);
        binding.spinner.setAdapter(aa);
        binding.spinner.setSelection(sourcePos);

//        mSpinnerTran.setOnItemSelectedListener(this);
        binding.spinner2.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        ArrayAdapter aa2 = new ArrayAdapter(getParentActivity(), R.layout.spinner_top_item, orginalLangList);
        aa2.setDropDownViewResource(R.layout.spinner_item);
        binding.spinner2.setAdapter(aa2);
        binding.spinner2.setSelection(targetPos);

    }

    private void setBundleParameters() {

        // Get Bundle Parameters.
        Bundle bundle = getArguments();
        if (bundle != null) {
            mIntentInfo = (IntentInfo) bundle.getSerializable(BUNDLE_PARAMETER);
            if (mIntentInfo != null) {
                source = (String) mIntentInfo.getParameters().get("source");
                target = (String) mIntentInfo.getParameters().get("target");

            }
        }

        LangUtils langAll = new LangUtils();

        int i = 0;
        for (Map.Entry<String, String> entry : langAll.allLang.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            orginalLangList.add(key);

            if (key.equalsIgnoreCase(source)) {
                sourcePos = i;
            }
            if (key.equalsIgnoreCase(target)) {
                targetPos = i;
            }
            i++;
        }

//        Collections.sort(orginalLangList, new Comparator<String>() {
//            @Override
//            public int compare(String text1, String text2) {
//                return text1.compareToIgnoreCase(text2);
//            }
//        });

        if (getArguments() != null) {
            getArguments().remove(BUNDLE_PARAMETER);
        }

    }

    @Override
    public BasePresenter getPresenter() {
        return mPresenter;
    }


    /************************************************ Views on-Click Listeners ***********************************************/

    void onBackPressed() {
        getParentActivity().onBackPressed();
    }


    void onParentClicked() {
    }

    void onSourceImgClick() {

//        translate(etSource.getText().toString());
    }

    public void translate(String input) {

        if (alterSourtarg) {

            source = binding.spinner2.getSelectedItem().toString();
            target = binding.spinner.getSelectedItem().toString();
            binding.etTarget.setBackgroundResource(R.drawable.bg_background_edittext_select);
            binding.etSource.setBackgroundResource(R.drawable.bg_background_edittext);
        } else {
            source = binding.spinner.getSelectedItem().toString();
            target = binding.spinner2.getSelectedItem().toString();
            binding.etTarget.setBackgroundResource(R.drawable.bg_background_edittext);
            binding.etSource.setBackgroundResource(R.drawable.bg_background_edittext_select);
        }

        mPresenter.setTranslate(source, target, input);

    }


    @Override
    public void onTraslateResult(TranslateResponse translateResponse) {

        getParentActivity().stopWakeWordAnimationFromFragment();
        if (alterSourtarg) {
            binding.etTarget.setText(translateResponse.getIn_text());
            binding.etSource.setText(translateResponse.getOutput_text());
            binding.etTarget.setBackgroundResource(R.drawable.bg_background_edittext);
            binding.etSource.setBackgroundResource(R.drawable.bg_background_edittext_select);
            alterSourtarg = false;

        } else {
            binding.etSource.setText(translateResponse.getIn_text());
            binding.etTarget.setText(translateResponse.getOutput_text());
            binding.etTarget.setBackgroundResource(R.drawable.bg_background_edittext_select);
            binding.etSource.setBackgroundResource(R.drawable.bg_background_edittext);
            alterSourtarg = true;

        }
        getParentActivity().setVoiceGenieLangChange(translateResponse.getTarget_code());

//        getParentActivity().askFollowUpQuery(translateResponse.getOutput_text());
//        getParentActivity().onRespondToUser(translateResponse.getOutput_text());
//        getParentActivity().readNewsHeadline(translateResponse.getOutput_text(),false);

        String temp = source;
        source = target;
        target = temp;

    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        mPresenter.presenterCleanUps();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getParentActivity().setVoiceGenieLangChange(DEFAULT_LANG);

    }
}