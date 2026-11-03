package com.sb.woohoo.ui.base;


import com.sb.woohoo.R;
import com.sb.woohoo.ui.main.fragments.screensaver.ScreenSaverFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentTransactionManager {

    private static int animationIn = R.anim.fragment_in;
    private static int animationOut = R.anim.fragment_out;

    public static void setTransitionAnimations(int animationIn, int animationOut) {
        if (animationIn != -1) {
            FragmentTransactionManager.animationIn = animationIn;
        }
        if (animationOut != -1) {
            FragmentTransactionManager.animationOut = animationOut;
        }
    }

    //  public static void

    public static synchronized void doContentFragmentTransactionUsingAdd(FragmentManager mFragmentManager,
                                                                         BaseFragment leftFragment,
                                                                         BaseFragment righFragment,
                                                                         boolean popStack,
                                                                         int leftcontainer,
                                                                         int rightcontainer) {
        if (!mFragmentManager.isDestroyed()) {
            if (popStack) {
                mFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.add(rightcontainer, righFragment, righFragment.createTag());
            ft.add(leftcontainer, leftFragment, leftFragment.createTag());
            ft.addToBackStack(righFragment.createTag());
            ft.commitAllowingStateLoss();
        }
    }

    public static synchronized void doContentFragmentTransactionUsingAdd(FragmentManager mFragmentManager,
                                                                         BaseFragment fragment,
                                                                         boolean popStack,
                                                                         int container) {
        if (!mFragmentManager.isDestroyed()) {
            if (popStack) {
                mFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            FragmentTransaction ft = mFragmentManager.beginTransaction();
           /* if (!(fragment instanceof IFragmentAnimationSkipper)) {
                ft.setCustomAnimations(animationIn, animationOut, animationIn, animationOut);
            }*/
            ft.add(container, fragment, fragment.createTag());

            ft.addToBackStack(fragment.createTag());
            ft.commitAllowingStateLoss();
           // mFragmentManager.executePendingTransactions();
        }
    }

    public static synchronized void doContentFragmentTransactionUsingReplace(FragmentManager mFragmentManager,
                                                                             BaseFragment fragment,
                                                                             boolean popStack,
                                                                             int container) {
        if (!mFragmentManager.isDestroyed()) {
//            if (popStack) {
//                mFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//            }
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            /*if (!(fragment instanceof IFragmentAnimationSkipper)) {
                ft.setCustomAnimations(animationIn, animationOut, animationIn, animationOut);
            }*/
            ft.replace(container, fragment);

            ft.addToBackStack(fragment.createTag());
            ft.commitAllowingStateLoss();
            //mFragmentManager.executePendingTransactions();
        }
    }

    public static Fragment getCurrentFragment(FragmentManager mFragmentManager) {
        if (mFragmentManager != null) {
            if (mFragmentManager.getBackStackEntryCount() != 0) {
                String fragmentTag = mFragmentManager.getBackStackEntryAt(mFragmentManager.getBackStackEntryCount() - 1).getName();
                return mFragmentManager.findFragmentByTag(fragmentTag);
            }
        }
        return null;
    }

    public static void setLeftPane(FragmentManager mFragmentManager, BaseFragment fragment) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.setCustomAnimations(animationIn, animationOut, animationIn, animationOut);
        ft.replace(R.id.container_left, fragment, fragment.createTag());
        ft.commitAllowingStateLoss();
    }
}
