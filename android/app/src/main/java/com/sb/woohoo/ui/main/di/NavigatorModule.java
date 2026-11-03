package com.sb.woohoo.ui.main.di;


import android.content.Context;

import com.sb.woohoo.ui.main.navigators.FragmentNavigator;
import com.sb.woohoo.ui.main.navigators.StateNavigator;

import androidx.fragment.app.FragmentManager;
import dagger.Module;
import dagger.Provides;

@Module
public class NavigatorModule {
    @Provides
    @MainScope
    FragmentNavigator providesFragmentNavigator(
            FragmentManager fragmentManager) {
        return new FragmentNavigator(fragmentManager);
    }

    @Provides
    @MainScope
    StateNavigator providesStateNavigator(Context context, FragmentManager fragmentManager) {
        return new StateNavigator(context, fragmentManager);
    }
}
