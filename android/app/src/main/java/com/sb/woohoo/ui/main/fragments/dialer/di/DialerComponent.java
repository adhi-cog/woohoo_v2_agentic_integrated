package com.sb.woohoo.ui.main.fragments.dialer.di;

import com.sb.woohoo.ui.main.fragments.dialer.DialerFragment;

import dagger.Component;

/**
 * {@link Component} for {@link DialerFragment}.
 *
 * @author Ritwik Jamuar.
 */
@Component(modules = DialerModule.class)
 public interface DialerComponent {
    void injectDialerFragment(DialerFragment fragment);
}