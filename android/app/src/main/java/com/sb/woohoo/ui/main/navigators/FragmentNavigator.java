package com.sb.woohoo.ui.main.navigators;

import android.content.res.Configuration;
import android.os.Handler;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.R;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.base.FragmentTransactionManager;
import com.sb.woohoo.ui.main.callbacks.MediaPopListener;
import com.sb.woohoo.ui.main.callbacks.PopFragment;
import com.sb.woohoo.ui.main.fragments.home.HomeFragment;
import com.sb.woohoo.ui.main.fragments.screensaver.ScreenSaverFragment;
import com.sb.woohoo.ui.main.fragments.transparent.TransparentFragment;
import com.sb.woohoo.utils.others.MethodUtils;

import javax.inject.Inject;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import static android.view.View.VISIBLE;
import static com.sb.woohoo.ui.base.IHintBarState.HINT_RESET;
import static com.sileria.android.Resource.getResources;

/**
 * Handles the navigation of Fragment in the Activity.
 */
public class FragmentNavigator implements FragmentManager.OnBackStackChangedListener, MediaPopListener {

    private static int SCREEN_SAVER_TIMEOUT = 300 * 1000; // 5 mins timeout
    private final static int POP_TIMEOUT = 10 * 1000;
    private FragmentManager mFragmentManager;
    private int mContainerLayout;
    private int mContainerLeft;
    private Handler handler;
    private FragmentNavigatorCallback fragmentNavigatorCallback;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            fragmentNavigatorCallback.onBackNavigation();
        }
    };
    private Runnable screenSaverRunnable = this::startScreenSaver;

    @Inject
    public FragmentNavigator(FragmentManager fragmentManager) {
        LoggerWrapper.i(FragmentNavigator.class.getSimpleName(), "Constructor");
        this.mContainerLayout = R.id.content_home;
        this.mContainerLeft = R.id.container_left;
        this.mFragmentManager = fragmentManager;
        handler = new Handler();
    }

    /**
     * Add a new listener for changes to the fragment back stack.
     */
    public void setOnBackStackChangeListener() {
        mFragmentManager.addOnBackStackChangedListener(this);
    }

    /**
     * @return Returns the current fragment visible on the device
     */
    public Fragment getCurrentFragment() {
        return FragmentTransactionManager.getCurrentFragment(mFragmentManager);
    }

    /**
     * @return Return the number of entries currently in the back stack.
     */
    public int getBackStackEntryCount() {
        return mFragmentManager.getBackStackEntryCount();
    }

    public void popBackStackImmediate() {
        mFragmentManager.popBackStackImmediate();
    }

    /**
     * Changes the current fragment of Activity with given fragment. \
     * Implement the new instance before calling this method
     *
     * @param fragment Instance of {@link FeatureFragment}.
     */
    public void changeFragmentUsingAdd(FeatureFragment fragment) {
        FragmentTransactionManager.doContentFragmentTransactionUsingAdd(
                mFragmentManager,
                fragment,
                false,
                mContainerLayout
        );
    }

    public void changeFragmentUsingAdd(FeatureFragment leftFragment, FeatureFragment rightFragment) {
        FragmentTransactionManager.doContentFragmentTransactionUsingAdd(
                mFragmentManager,
                leftFragment,
                rightFragment,
                false,
                mContainerLeft,
                mContainerLayout
        );
    }

    public void changeFragmentUsingReplace(FeatureFragment fragment) {
        FragmentTransactionManager.doContentFragmentTransactionUsingReplace(
                mFragmentManager,
                fragment,
                false,
                mContainerLayout
        );
    }

    /**
     * Set the listener for the callbacks
     *
     * @param callBack Implement the callbacks
     */
    public void setFragmentListener(FragmentNavigatorCallback callBack) {
        this.fragmentNavigatorCallback = callBack;
    }

    @Override
    public void pausePop() {
        handler.removeCallbacks(runnable);
        handler.removeCallbacks(screenSaverRunnable);
    }

    @Override
    public void snoozePop() {
        if (getCurrentFragment() instanceof PopFragment) {
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, POP_TIMEOUT);
        } else if (getCurrentFragment() instanceof HomeFragment) {
            handler.removeCallbacks(runnable);
            handler.removeCallbacks(screenSaverRunnable);
            handler.postDelayed(screenSaverRunnable, SCREEN_SAVER_TIMEOUT);
        } else {
            handler.removeCallbacks(runnable);
            handler.removeCallbacks(screenSaverRunnable);
        }
    }

    private void startScreenSaver() {

        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            changeFragmentUsingAdd(ScreenSaverFragment.newInstance());
        } else {
            changeFragmentUsingAdd(ScreenSaverFragment.newInstance(), TransparentFragment.newInstance());
        }
        pausePop();
    }

    public void updateScreenSaverTime(int time){
        time = time * 60 * 1000;
        LoggerWrapper.i("screen saver time",String.valueOf(time));
        SCREEN_SAVER_TIMEOUT = time;
        snoozePop();
    }

    @Override
    public void onBackStackChanged() {
        Fragment currentFragment = getCurrentFragment();
        handler.removeCallbacks(screenSaverRunnable);

        if (currentFragment instanceof HomeFragment) {
            handler.postDelayed(screenSaverRunnable, SCREEN_SAVER_TIMEOUT);

            // fragmentNavigatorCallback.resetNlpContext();

            fragmentNavigatorCallback.changeHintBar(HINT_RESET, HINT_RESET, VISIBLE);
            ((HomeFragment) currentFragment).playOrbAnimation();
            ((HomeFragment) currentFragment).setLeftPaneFragment();
        } else {
            Fragment fragment = mFragmentManager.findFragmentByTag("HomeFragment");
            if (fragment != null) {
                ((HomeFragment) fragment).pauseOrbAnimation();
            }
            snoozePop();
        }
    }

    public boolean isFragmentStacked() {
        return mFragmentManager.getBackStackEntryCount() > 2;
    }

    public interface FragmentNavigatorCallback {
        void resetNlpContext();

        void onBackNavigation();

        void changeHintBar(int hintText, int backgroundColour, int visibility);
    }
}