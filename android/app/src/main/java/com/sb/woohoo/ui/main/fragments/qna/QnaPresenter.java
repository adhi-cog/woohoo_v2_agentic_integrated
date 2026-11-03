package com.sb.woohoo.ui.main.fragments.qna;

import com.sb.woohoo.R;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.fragments.qna.pojo.WolframData;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

/**
 * Created by myT on 3/16/2018.
 */

public class QnaPresenter
        extends BasePresenter
        implements QnaContract.Actions {
    private QnaRepository qnaRepository;
    private QnaFragment mView;

    @Inject
    public QnaPresenter(QnaRepository qnaRepository, QnaFragment mView) {
        this.qnaRepository = qnaRepository;
        this.mView = mView;
        this.qnaRepository.onAttach(this);
    }

    @Override
    public void askQuestion(String question) {
        mView.showProgressBar();
        qnaRepository.askQuestionToWolframAlpha(question);
    }

    @Override
    public void onWolframAlphaSuccess(WolframData answer) {
        mView.hideProgressBar();
        if (answer == null) {
            mView.errorInFetchingAnswer(mView.getString(R.string.not_found_anything));
        } else {
            String result = answer.getResult() == null || answer.getResult().length() == 0 ? "" : answer.getResult();
            String image = answer.getImage() == null || answer.getImage().length() == 0 ? "" : answer.getImage();
            mView.showAnswer(result, image);
            mView.speakAnswer(result);
        }
    }

    @Override
    public void onWolframAlphaFailure() {
        mView.hideProgressBar();
        mView.errorInFetchingAnswer(mView.getString(R.string.not_found_anything));
    }

    @Override
    public void onWolframAlphaError(String error) {
        mView.hideProgressBar();
        mView.showError(error);
    }

    public void setDate() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE ,dd-MMMM-yyyy");

        String dateResult = simpleDateFormat.format(date);
        mView.respondToUser(String.format(mView.getString(R.string.hey_its),dateResult));
    }

    public void setTime() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        String timeResult = simpleDateFormat.format(date);
        mView.respondToUser(String.format(mView.getString(R.string.hey_its), timeResult));
    }

    @Override
    public void processActions(IntentInfo intentInfo) {
        String mQuestion;

        if (intentInfo != null) {
            switch (intentInfo.getAction()) {
                case Categories.Skills.QnA.QNA_STOP:
                case Categories.Controls.Player.PLAYER_STOP:
                case Categories.Controls.Player.PLAYER_PAUSE:
                    mView.onBack();
                    break;

                case Categories.Skills.QnA.QNA_QUERY: {
                    mQuestion = intentInfo.getUserQuery() + "?";
                    askQuestion(mQuestion);
                }
                break;
                case Categories.Skills.QnA.QNA_CURRENT_TIME: {
                    mQuestion = intentInfo.getUserQuery() + "?";
                    mView.setCurrentTime(mQuestion);
                }
                break;
                case Categories.Skills.QnA.QNA_CURRENT_DATE: {
                    mQuestion = intentInfo.getUserQuery() + "?";
                    mView.setCurrentDate(mQuestion);
                }
                break;
            }
        }
    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        qnaRepository.repositoryCleanUps();
    }
}