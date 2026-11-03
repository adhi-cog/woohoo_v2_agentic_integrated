package com.sb.woohoo.ui.main.fragments.qna;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.databinding.FragmentQnaBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.PopFragment;
import com.sb.woohoo.ui.main.fragments.qna.di.DaggerQnaComponent;
import com.sb.woohoo.ui.main.fragments.qna.di.QnaComponent;
import com.sb.woohoo.ui.main.fragments.qna.di.QnaModule;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import jp.wasabeef.picasso.transformations.BlurTransformation;

//import jp.wasabeef.picasso.transformations.BlurTransformation;

/**
 * Show and manages QnA's of User.
 *
 * @author Pankaj
 */
public class QnaFragment
        extends FeatureFragment<MainActivity>
        implements QnaContract.Views, PopFragment {
    // Constants.
    private static final String BUNDLE_PARAMETER = "QNA";
    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_QNA;

    // Presenter.
    @Inject
    QnaPresenter qnaPresenter;
    // Other Components.
    @Inject
    Picasso mPicasso;
    @Inject
    AnalyticsHelper helper;
    // Variables.
    private String mQuestion;
    private IntentInfo mIntentInfo;
    private FragmentQnaBinding binding;

    public static QnaFragment newInstance(IntentInfo intentInfo) {
        QnaFragment fragment = new QnaFragment();
        Bundle args = new Bundle();
        args.putSerializable(BUNDLE_PARAMETER, intentInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    /****************************** {@link FeatureFragment} Methods ******************************/

    @Override
    public String createTag() {
        return QnaFragment.class.getSimpleName();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {
        mIntentInfo = intentInfo;
    }

    @Override
    public void initDagger() {
        QnaComponent qnaComponent =
                DaggerQnaComponent
                        .builder()
                        .qnaModule(new QnaModule(this))
                        .build();
        qnaComponent.injectQnaFragment(this);
    }

    @Override
    public void bindViews(View view) {
        view.setVisibility(View.INVISIBLE);
    }

    /********************************************** Methods ***************************/

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.Qna(),  null);
        binding = FragmentQnaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setVisibility(View.INVISIBLE);
        attachParent((MainActivity) getActivity());
        setBundleParameters();
        qnaPresenter.processActions(mIntentInfo);
        binding.fragmentQnaImageViewBack.setOnClickListener(view1 -> onBack());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getParentActivity().stopTextToSpeech(true);
        featureFragmentCleanUps();
    }

    @Override
    public BasePresenter getPresenter() {
        return qnaPresenter;
    }

    /********************************** {@code private} Methods **********************************/

    private void setBundleParameters() {
        // Get Bundle Parameters.
        Bundle bundle = getArguments();
        if (bundle != null) {
            mIntentInfo = (IntentInfo) bundle.getSerializable(BUNDLE_PARAMETER);
            if (mIntentInfo != null) {
                mQuestion = mIntentInfo.getUserQuery() + "?";
            }
        }
        if (getArguments() != null) {
            getArguments().remove(BUNDLE_PARAMETER);
        }
    }

    private void askQuestion(String question) {
        question = question.substring(0, 1).toUpperCase() + question.substring(1);
        qnaPresenter.askQuestion(question);
        binding.fragmentQnaTextViewQuestionQna.setText(question);
        binding.fragmentQnaTextViewAnswerText.setText("");
        binding.fragmentQnaImageViewAnswerImage.setImageResource(android.R.color.transparent);
    }

    public void setCurrentTime(String question) {
        qnaPresenter.setTime();
    }

    public void setCurrentDate(String question) {
        qnaPresenter.setDate();
    }

    public void onQuestion(IntentInfo qnaIntentInfo) {
        askQuestion(qnaIntentInfo.getUserQuery());
    }

    /***************************** {@link QnaContract.Views} Callback ****************************/

    @Override
    public void showAnswer(String answer, String image) {
        if (getView() != null) {
            getView().setVisibility(View.VISIBLE);
            binding.fragmentQnaTextViewAnswerText.setText(answer);
            if (image != null && !image.isEmpty()) {
                mPicasso
                        .load(image)
                        .transform(new BlurTransformation(getParentActivity(), 10, 1))
                        .fit()
                        .centerCrop()
                        .into( binding.fragmentQnaImageViewAnswerImage);
            }
        }
    }

    @Override
    public void speakAnswer(String answer) {
        String textToSpeak = requireContext().getResources().getString(R.string.found_on_web);
        boolean isAnswerEmpty = answer.isEmpty();
        boolean isAnswerStartsWithWolframString = answer.startsWith("StringJoin");
        if (!isAnswerEmpty && !isAnswerStartsWithWolframString) {
            textToSpeak =
                    answer
                            .replaceAll("Q:", "")
                            .replaceAll("A:", "");
        } else {
            binding.fragmentQnaTextViewAnswerText.setText("");
        }
        respondToUser(textToSpeak);
    }

    @Override
    public void errorInFetchingAnswer(String errorMessage) {
        // Check if there are fragments below
        if (!getParentActivity().isFragmentStacked()) {
            respondToUser(errorMessage);
        }
        getParentActivity().onBackPressed();
    }

    @Override
    public void showError(String errorMessage) {
        getParentActivity().onBackPressed();
        MethodUtils.showToastMessage(getParentActivity(), errorMessage);
    }

    @Override
    public void showProgressBar() {
        binding.fragmentQnaProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        binding.fragmentQnaProgressBar.setVisibility(View.GONE);
    }

    /********************************** Listeners **********************************/

    void onBack() {
        getParentActivity().stopTextToSpeech(true);
        getParentActivity().onBackPressed();
    }

    void onRootClicked() {
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        qnaPresenter.presenterCleanUps();
    }
}