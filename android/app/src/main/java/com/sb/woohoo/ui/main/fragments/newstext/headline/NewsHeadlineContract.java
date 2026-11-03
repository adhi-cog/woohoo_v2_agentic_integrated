package com.sb.woohoo.ui.main.fragments.newstext.headline;

import com.sb.woohoo.ui.main.fragments.newstext.headline.pojo.Article;
import com.sb.woohoo.ui.main.fragments.newstext.headline.pojo.NewsHeadline;

import java.util.List;

public interface NewsHeadlineContract {

    interface Views {

        void onNewsHeadlineError(String errorMessage);

        void onNewsResult(List<Article> newsHeadlines);

        void newsDetailShow(Article newsDetail);

//        void readToUserHeadline(List<Article> newsHeadlines);

        void showProgress();

        void hideProgress();

    }

    interface Actions {

        void newsHeadlineSuccess(NewsHeadline newsHeadline);

        void newsHeadlineError(String error);

    }
}
