package com.sb.woohoo.ui.main.fragments.newstext.headline;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.fragments.newstext.headline.pojo.NewsHeadline;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.HashMap;

import javax.inject.Inject;

public class NewsHeadlinePresenter extends BasePresenter implements NewsHeadlineContract.Actions {

    private NewsHeadlineRepository newsHeadlineRepository;
    private NewsHeadlineFragment mView;

    @Inject
    public NewsHeadlinePresenter(NewsHeadlineRepository newsHeadlineRepository, NewsHeadlineFragment mView) {
        this.newsHeadlineRepository = newsHeadlineRepository;
        this.mView = mView;
        newsHeadlineRepository.onAttach(this);
    }

    private void askNewsWithCountryCode() {
        newsHeadlineRepository.makeLocationApiRequest();
        mView.showProgress();
    }

    private void askNewsWithQuery(String query) {
        newsHeadlineRepository.makeGeneralApiRequest(query);
        mView.showProgress();
    }

    @Override
    public void newsHeadlineSuccess(NewsHeadline newsHeadlineResponse) {
        mView.onNewsResult(newsHeadlineResponse.getData().getArticles());
        mView.hideProgress();
    }

    @Override
    public void newsHeadlineError(String error) {
        mView.onNewsHeadlineError(error);
        mView.hideProgress();
    }

    @Override
    public void processActions(IntentInfo intentInfo) {
        if (intentInfo != null) {
            switch (intentInfo.getAction()) {
                case Categories.Skills.News.NEWS_CONTROL_LAUNCH: {
                    askNewsWithCountryCode();
                }
                break;

                case Categories.Skills.News.NEWS_CONTROL_LOCATION: {
                    // TODO query news.
                }
                break;

                case Categories.Skills.News.NEWS_CONTROL_SEARCH: {
                    HashMap<String, String> intentParameters = intentInfo.getParameters();
                    String query = "";
                    if (intentParameters.containsKey("query"))
                        query = intentParameters.get("query");
                    if (query != null && !query.isEmpty())
                        askNewsWithQuery(query);
                }
                break;

                case Categories.Controls.Player.PLAYER_NEXT:
                case Categories.Skills.News.NEWS_CONTROL_NEXT:
                    mView.proceedToNextPage();
                    break;

                case Categories.Controls.Player.PLAYER_PREVIOUS:
                case Categories.Skills.News.NEWS_CONTROL_PREVIOUS:
                    mView.proceedToPreviousPage();
                    break;

                case Categories.Controls.Player.PLAYER_STOP:
                    mView.onBack();
                    break;
            }
        }else{
            askNewsWithCountryCode();
        }
    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        newsHeadlineRepository.repositoryCleanUps();
    }
}
