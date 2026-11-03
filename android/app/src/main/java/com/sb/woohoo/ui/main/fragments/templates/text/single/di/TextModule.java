package com.sb.woohoo.ui.main.fragments.templates.text.single.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.templates.text.single.TextFragment;

import dagger.Module;
import dagger.Provides;

@Module
public class TextModule {

    TextFragment mFragment;

    public TextModule(TextFragment textFragment) {
        this.mFragment = textFragment;
    }

    @Provides
    public TextFragment getFragment() {
        return mFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(mFragment.getActivity()).getWooHooAppComponent().getWooHooApiCall();
    }


}
