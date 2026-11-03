package com.sb.woohoo.ui.enrollment.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.ui.enrollment.EnrollmentActivity;
import com.sb.woohoo.ui.enrollment.EnrollmentPresenter;
import com.sb.woohoo.ui.enrollment.EnrollmentRepository;

import dagger.Module;
import dagger.Provides;

@Module
public class EnrollmentModule {
    private EnrollmentActivity mActivity;

    public EnrollmentModule(EnrollmentActivity activity) {
        this.mActivity = activity;
    }

    @Provides
    public EnrollmentActivity providesEnrollmentActivity() {
        return mActivity;
    }

    @Provides
    public EnrollmentPresenter providesEnrollmentPresenter(EnrollmentRepository repository) {
        return new EnrollmentPresenter(mActivity, repository, mActivity);
    }
}