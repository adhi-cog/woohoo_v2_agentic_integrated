package com.sb.woohoo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sb.woohoo.ui.onboarding.OnboardingActivity;

public class MyStartupReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, OnboardingActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
