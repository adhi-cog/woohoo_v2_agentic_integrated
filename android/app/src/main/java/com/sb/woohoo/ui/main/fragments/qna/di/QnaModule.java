package com.sb.woohoo.ui.main.fragments.qna.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.qna.QnaFragment;
import com.sb.woohoo.ui.main.fragments.qna.QnaPresenter;
import com.sb.woohoo.ui.main.fragments.qna.QnaRepository;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

/**
 * Created by myT on 3/17/2018.
 */
@Module
public class QnaModule {
    private QnaFragment qnaFragment;

    public QnaModule(QnaFragment qnaFragment) {
        this.qnaFragment = qnaFragment;
    }

    @Provides
    public Picasso providesPicasso() {
        return WooHooApp.get(qnaFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public QnaFragment providesQnaFragment() {
        return qnaFragment;
    }

    @Provides
    public QnaPresenter providesQnaPresenter(QnaRepository repository) {
        return new QnaPresenter(repository, qnaFragment);
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(qnaFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(qnaFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}