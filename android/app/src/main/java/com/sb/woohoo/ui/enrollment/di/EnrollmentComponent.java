package com.sb.woohoo.ui.enrollment.di;

import com.sb.woohoo.di.WooHooAppComponent;
import com.sb.woohoo.ui.enrollment.EnrollmentActivity;

import dagger.Component;

@Component(modules = EnrollmentModule.class, dependencies = WooHooAppComponent.class)
@EnrollmentScope
public interface EnrollmentComponent {
    void injectEnrollmentActivity(EnrollmentActivity activity);
}