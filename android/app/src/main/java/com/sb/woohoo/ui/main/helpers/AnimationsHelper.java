package com.sb.woohoo.ui.main.helpers;

import static com.sb.woohoo.data.prefs.SharedPrefsKey.PREF_MUTE_STATE;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.databinding.ActivityHomeBinding;
import com.sb.woohoo.ui.base.BaseHelper;
import com.sb.woohoo.ui.main.Interactors.listener.HelperListener;
import com.sb.woohoo.ui.main.MainActivity;

import javax.inject.Inject;

public class AnimationsHelper extends BaseHelper {

    private final float ALPHA_OPAQUE = 1f;
    private final float ALPHA_TRANSPARENT = 0f;
    private final float POSITION_INITIAL = 1f;
    private final float POSITION_ORIGINAL = 0f;
    private final int DURATION_POP_UP = 100; //ms
    private final int DURATION_FADE_IN = 150; //ms
    private Interpolator accelerateInterpolator = new AccelerateDecelerateInterpolator();
    private WakeWordHandler wakeWordHandler;
    private NetworkIssuesHandler networkIssuesHandler;
    private DrawerAnimationsHandler drawerAnimationsHandler;

    @Inject
    public AnimationsHelper(MainActivity activity) {
        LoggerWrapper.i(AnimationsHelper.class.getSimpleName(), "Constructor");
        wakeWordHandler = new WakeWordHandler(activity, activity.binding);
        networkIssuesHandler = new NetworkIssuesHandler(activity.binding);
        drawerAnimationsHandler = new DrawerAnimationsHandler(activity.binding);
    }

    private AlphaAnimation getAlphaAnimation(float fromAlpha, float toAlpha, long animationDuration) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        alphaAnimation.setDuration(animationDuration);
        return alphaAnimation;
    }

    private TranslateAnimation getTranslateAnimation(float fromX, float fromY, float toX, float toY, int relativeTo, long animationDuration) {
        TranslateAnimation translateAnimation = new TranslateAnimation(relativeTo, fromX, relativeTo, toX, relativeTo, fromY, relativeTo, toY);
        translateAnimation.setDuration(animationDuration);
        return translateAnimation;
    }

    private TranslateAnimation getHideTranslateAnimation(float toY, long animationDuration) {
        TranslateAnimation translateAnimation = new
                TranslateAnimation(POSITION_ORIGINAL,
                POSITION_ORIGINAL,
                POSITION_ORIGINAL,
                toY);
        translateAnimation.setDuration(animationDuration);
        return translateAnimation;
    }

    public NetworkIssuesHandler getNetworkIssuesHandler() {
        return networkIssuesHandler;
    }

    public WakeWordHandler getWakeWordHandler() {
        return wakeWordHandler;
    }

    public DrawerAnimationsHandler getDrawerAnimationsHandler() {
        return drawerAnimationsHandler;
    }

    @Override
    public void onHelperCleanUp() {
        wakeWordHandler = null;
        networkIssuesHandler = null;
        drawerAnimationsHandler = null;
    }

    public void showMuteBar(boolean aIsNetworkPresent) {
        getNetworkIssuesHandler().showMuteError(aIsNetworkPresent);
    }

    public void hideMuteBar(boolean aIsNetworkPresent) {
        getNetworkIssuesHandler().hideMuteError(aIsNetworkPresent);
    }

    public class WakeWordHandler {

        private final int MODULATION_INTERVAL = 40; // in ms
        private final float MODULATION_MIN = 2.0f;
        private final float MODULATION_MAX = 10.0f;
        private final float MODULATOR_MAX = 1.6f;
        private final float MODULATOR_MIN = 1.0f;
        private final int DELAY_ANIMATION_CHECK = 200; // in ms
        private final int POST_ASR_ACTION_DELAY_MS = 800;
        private final ActivityHomeBinding binding;

        Activity activityContext;
        Handler wakeWordTextAnimationHandler = new Handler();
        private boolean isActive = false;
        private boolean isFollowUp = false;
        private boolean textAnimationCompletion = true;
        private float lastModulationValue = MODULATION_MIN;
        private int animatedCharacterLength = 1;
        private Runnable animationStopper = new Runnable() {
            @Override
            public void run() {
                if (textAnimationCompletion || !isFollowUp) {
                    stopVoiceAnimation(false, null);
                } else {
                    wakeWordTextAnimationHandler.postDelayed(animationStopper, DELAY_ANIMATION_CHECK);
                }
            }
        };

        public WakeWordHandler(Activity activityContext, ActivityHomeBinding binding) {
            this.activityContext = activityContext;
            this.binding = binding;
        }

        public boolean isActive() {
            return isActive;
        }

        private void toggleVisibility(int visibility) {
            binding.wakeWordAnimationBackground.setVisibility(visibility);
            binding.wakeWordAnimationText.setVisibility(visibility);
            binding.voiceModulationView.setVisibility(visibility);
            if (visibility != View.VISIBLE) {
                setProcessingVisibility(View.GONE);
                setRecordingVisibility(View.GONE);
            }
        }

        private void setProcessingVisibility(int visibility) {
            if (binding.wakeWordProcessing != null) {
                binding.wakeWordProcessing.setVisibility(visibility);
            }
        }

        private void setRecordingVisibility(int visibility) {
            if (binding.wakeWordRecordingLabel != null) {
                binding.wakeWordRecordingLabel.setVisibility(visibility);
            }
        }

        public void resetAnimation() {
            binding.wakeWordAnimationText.setText("");
        }

        public void setProcessing(boolean processing) {
            setProcessingVisibility(processing ? View.VISIBLE : View.GONE);
            if (processing) {
                setRecordingVisibility(View.GONE);
            }
        }

        public void setRecording(boolean recording) {
            setRecordingVisibility(recording ? View.VISIBLE : View.GONE);
            if (recording) {
                setProcessingVisibility(View.GONE);
            }
        }

        public void modulateVoice(float modulationValue) {
            Animation modulationAnimation = getModulationAnimation(lastModulationValue, modulationValue);
            animateModulation(modulationAnimation);
            animateModulation(getModulationAnimation(lastModulationValue, modulationValue));
            lastModulationValue = modulationValue;
        }

        public boolean isFollowUp() {
            return isFollowUp;
        }

        public void setFollowUp(boolean followUp) {
            isFollowUp = followUp;
        }

        private void animateModulation(Animation modulationAnimation) {
            binding.voiceModulationView.startAnimation(modulationAnimation);
        }

        private Animation getModulationAnimation(float currentSize, float targetSize) {
            if (currentSize < MODULATION_MIN) {
                currentSize = MODULATION_MIN;
            }
            if (targetSize < MODULATION_MIN) {
                targetSize = MODULATION_MIN;
            }
            float currentScale = (((currentSize - MODULATION_MIN) * (MODULATOR_MAX - MODULATOR_MIN)) / (MODULATION_MAX - MODULATION_MIN)) + MODULATOR_MIN;
            float targetScale = (((targetSize - MODULATION_MIN) * (MODULATOR_MAX - MODULATOR_MIN)) / (MODULATION_MAX - MODULATION_MIN)) + MODULATOR_MIN;
//            LoggerWrapper.i("Modulation", targetScale + "");
            float pivot = 0.5f;
            int pivotReference = Animation.RELATIVE_TO_SELF;
            ScaleAnimation modulationAnimation = new ScaleAnimation(currentScale, targetScale, currentScale, targetScale, pivotReference, pivot, pivotReference, pivot);
            modulationAnimation.setDuration(MODULATION_INTERVAL);
            modulationAnimation.setInterpolator(accelerateInterpolator);
            return modulationAnimation;
        }

        public void startVoiceAnimation(Animation.AnimationListener dialogAnimationListener) {
            // Visibility toggle, if invisible
            if (!isActive) {
                isActive = true;
                toggleVisibility(View.VISIBLE);
                Animation popUpAnimation = getTranslateAnimation(POSITION_ORIGINAL, POSITION_INITIAL, POSITION_ORIGINAL, POSITION_ORIGINAL, Animation.RELATIVE_TO_SELF, DURATION_POP_UP);
                popUpAnimation.setInterpolator(accelerateInterpolator);
                popUpAnimation.setDuration(DURATION_POP_UP);
                if (dialogAnimationListener != null) {
                    popUpAnimation.setAnimationListener(dialogAnimationListener);
                }
                binding.wakeWordAnimationBackground.startAnimation(popUpAnimation);
            }
        }

        public void stopVoiceAnimation(boolean needTorunWakeWordRecobnition, WakeWordHelper mWakeWordHelper) {
            LoggerWrapper.i(AnimationsHelper.class.getSimpleName(), "Stop animation, isActive :" + isActive + " textAnimationComplete " + textAnimationCompletion + " Follow up " + isFollowUp);
            if (textAnimationCompletion && isActive() && !isFollowUp) {
                Animation popUpAnimation = getTranslateAnimation(POSITION_ORIGINAL, POSITION_ORIGINAL, POSITION_ORIGINAL, POSITION_INITIAL, Animation.RELATIVE_TO_SELF, DURATION_POP_UP);
                popUpAnimation.setInterpolator(accelerateInterpolator);
                popUpAnimation.setDuration(DURATION_POP_UP);
                popUpAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        toggleVisibility(View.GONE);
                        isActive = false;
                        if (needTorunWakeWordRecobnition && mWakeWordHelper != null)
                            mWakeWordHelper.runWakeWordRecognition();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                binding.wakeWordAnimationBackground.startAnimation(popUpAnimation);
            }
            if (!textAnimationCompletion || isFollowUp) {
                wakeWordTextAnimationHandler.postDelayed(animationStopper, DELAY_ANIMATION_CHECK);
            }
        }

        public void animateText(final String userQuery, HelperListener mListener) {
            animateText(userQuery, mListener, null);
        }

        public void animateText(final String userQuery, HelperListener mListener, String languageCode) {
            final int animationDelay = 20; // in millis
            animatedCharacterLength = 1;
            wakeWordTextAnimationHandler.postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            if (animatedCharacterLength <= userQuery.length()) {
                                textAnimationCompletion = false;
                                binding.wakeWordAnimationText.setText(userQuery.substring(0, animatedCharacterLength));
                                wakeWordTextAnimationHandler.postDelayed(this, animationDelay);
                                animatedCharacterLength++;
                            } else {
                                textAnimationCompletion = true;
                                wakeWordTextAnimationHandler.postDelayed(
                                        () -> activityContext.runOnUiThread(() -> {
                                            if (languageCode != null && !languageCode.trim().isEmpty()) {
                                                mListener.processQuery(userQuery, languageCode);
                                            } else {
                                                mListener.processQuery(userQuery);
                                            }
                                        }),
                                        POST_ASR_ACTION_DELAY_MS
                                );
                            }
                        }
                    },
                    animationDelay
            );
        }
    }

    public class NetworkIssuesHandler {
        private final ActivityHomeBinding binding;

        public NetworkIssuesHandler(ActivityHomeBinding binding) {
            this.binding = binding;
        }

        private void toggleVisibility(int visibility) {
            binding.errorLayout.viewNetworkIssue.setVisibility(visibility);
        }

        public void showError(int resId) {
            binding.errorLayout.errorText.setText(resId);
            binding.errorLayout.networkViewHolder.setVisibility(View.VISIBLE);
            binding.errorLayout.muteViewHolder.setVisibility(View.GONE);

            if (binding.errorLayout.viewNetworkIssue.getVisibility() != View.VISIBLE) {
                toggleVisibility(View.VISIBLE);
                Animation popUpAnimation = getTranslateAnimation(POSITION_ORIGINAL, POSITION_INITIAL,
                        POSITION_ORIGINAL, POSITION_ORIGINAL, Animation.RELATIVE_TO_SELF, DURATION_POP_UP);
                popUpAnimation.setInterpolator(accelerateInterpolator);
                popUpAnimation.setDuration(DURATION_POP_UP);
                binding.errorLayout.viewNetworkIssue.startAnimation(popUpAnimation);
            }
        }

        public void showMuteError(boolean aIsNetworkPresent) {
            if (!aIsNetworkPresent) {
                return;
            }

            binding.errorLayout.networkViewHolder.setVisibility(View.GONE);
            binding.errorLayout.muteViewHolder.setVisibility(View.VISIBLE);

            if (binding.errorLayout.viewNetworkIssue.getVisibility() != View.VISIBLE) {
                toggleVisibility(View.VISIBLE);
                Animation popUpAnimation = getTranslateAnimation(POSITION_ORIGINAL, POSITION_INITIAL,
                        POSITION_ORIGINAL, POSITION_ORIGINAL, Animation.RELATIVE_TO_SELF, DURATION_POP_UP);
                popUpAnimation.setInterpolator(accelerateInterpolator);
                popUpAnimation.setDuration(DURATION_POP_UP);
                binding.errorLayout.viewNetworkIssue.startAnimation(popUpAnimation);
            }
        }

        public void hideMuteError(boolean aIsNetworkPresent) {
            binding.errorLayout.muteViewHolder.setVisibility(View.GONE);
            if (!aIsNetworkPresent) {
                binding.errorLayout.networkViewHolder.setVisibility(View.VISIBLE);
            } else {
                dismissError();
            }
        }

        public void dismissError() {
            hide();
        }

        private void hide() {
            if (binding.errorLayout.viewNetworkIssue.getVisibility() == View.GONE) {
                return;
            }

            Animation popUpAnimation = getTranslateAnimation(POSITION_ORIGINAL, POSITION_ORIGINAL,
                    POSITION_ORIGINAL, POSITION_INITIAL, Animation.RELATIVE_TO_SELF, DURATION_POP_UP);
            popUpAnimation.setInterpolator(accelerateInterpolator);
            popUpAnimation.setDuration(DURATION_POP_UP);
            popUpAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    toggleVisibility(View.GONE);
                    binding.errorLayout.networkViewHolder.setVisibility(View.GONE);
                    binding.errorLayout.muteViewHolder.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            binding.errorLayout.viewNetworkIssue.startAnimation(popUpAnimation);
        }
    }


    public class DrawerAnimationsHandler implements Animation.AnimationListener {
        private final ActivityHomeBinding binding;

        DrawerAnimationsHandler(ActivityHomeBinding binding) {
            this.binding = binding;
        }

        public void toggleVisibility(boolean isVisible) {
            binding.screenOverlay.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }

        public void onDrawerOpen() {
            Animation fadeInAnimation = getAlphaAnimation(ALPHA_TRANSPARENT, ALPHA_OPAQUE, DURATION_FADE_IN);
            toggleVisibility(true);
            binding.screenOverlay.startAnimation(fadeInAnimation);
        }

        public void onDrawerClose() {
            Animation fadeOutAnimation = getAlphaAnimation(ALPHA_OPAQUE, ALPHA_TRANSPARENT, DURATION_FADE_IN);
            fadeOutAnimation.setAnimationListener(this);
            binding.screenOverlay.startAnimation(fadeOutAnimation);
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            toggleVisibility(false);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
