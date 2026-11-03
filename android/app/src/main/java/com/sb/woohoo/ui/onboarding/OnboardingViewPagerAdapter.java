package com.sb.woohoo.ui.onboarding;

import com.sb.woohoo.ui.main.fragments.settings.wifi.WifiFragment;
import com.sb.woohoo.ui.onboarding.intro.WooHooIntroFragment;
import com.sb.woohoo.ui.onboarding.login.LoginFragment;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import static com.sb.woohoo.ui.main.fragments.settings.wifi.WifiFragment.ONBOARDING;

/*** ViewPager Adapter used in {@link OnboardingActivity}.
 * Created by admin on 1/20/18.*/
public class OnboardingViewPagerAdapter extends FragmentPagerAdapter {
    // Constants.
    public static final String FRAGMENT_WIFI = "WiFI";
    public static final String FRAGMENT_LOGIN = "Login";
    public static final String FRAGMENT_WOOHOO_INTRO = "WooHooIntro";
    // Fragments.
    private WifiFragment wifiSelectionFragment;
    //public static final String FRAGMENT_LANGUAGE             = "Language";
    // Variables.
    private List<String> mFragments;

    OnboardingViewPagerAdapter(
            FragmentManager fm,
            List<String> fragments) {
        super(fm);
        this.mFragments = fragments;
    }

    /************************************* {@link FragmentPagerAdapter} Callbacks *************************************/

    @Override
    public Fragment getItem(int position) {
        switch (mFragments.get(position)) {
            case FRAGMENT_WIFI: {
                return wifiSelectionFragment == null
                        ? wifiSelectionFragment = WifiFragment.newInstance(ONBOARDING)
                        : wifiSelectionFragment;
            }

            case FRAGMENT_LOGIN: {
                return LoginFragment.newInstance();
            }

            case FRAGMENT_WOOHOO_INTRO: {
                return WooHooIntroFragment.newInstance();
            }

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mFragments == null ? 0 : mFragments.size();
    }

    public interface FragmentOrder {
        interface FactoryReset {
            int WIFI = 0;
            int LOGIN = 1;
            int WOOHOO_INTRO = 2;
        }

        interface LogOut {
            int LOGIN = 0;
            int WIFI = 1;
        }
    }
}