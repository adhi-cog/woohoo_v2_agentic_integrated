package com.sb.woohoo.ui.main.fragments.timer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.HashMap;

import javax.inject.Inject;

import static com.sb.woohoo.ui.main.Categories.Skills.Timer.TIMER_SET;
import static com.sb.woohoo.ui.main.Categories.Skills.Timer.TIMER_SHOW;
import static com.sb.woohoo.ui.main.Categories.Skills.Timer.TIMER_STOP;

public class TimerPresenter extends BasePresenter {

    private static final String SEPARATOR_EQUALS = "=";
    private static final String SEPARATOR_COLON = ":";
    private static final String NLP_KEY_DURATION = "duration";
    private static final String NLP_KEY_AMOUNT = "amount";
    private static final String NLP_KEY_UNIT = "unit";
    private TimerContract.View timerView;

    @Inject
    public TimerPresenter(TimerFragment timerFragment) {
        this.timerView = timerFragment;
    }

    public static String getTimeString(int timeValue, String timeUnit) {
        switch (timeUnit) {
            case "min": {
                if (timeValue > 1) {
                    return "minutes";
                } else {
                    return "minute";
                }
            }
            case "s": {
                if (timeValue > 1) {
                    return "seconds";
                } else {
                    return "second";
                }
            }
            case "h": {
                if (timeValue > 1) {
                    return "hours";
                } else {
                    return "hour";
                }
            }
            default:
                return timeUnit;
        }
    }

    @Override
    public void processActions(IntentInfo intentInfo) {
        if (intentInfo != null) {
            HashMap<String, Object> intentParameters = intentInfo.getParameters();
            switch (intentInfo.getAction()) {
                case TIMER_SET: {
                    String jsonString = intentParameters.get(NLP_KEY_DURATION)
                            .toString()
                            .replaceAll(SEPARATOR_EQUALS, SEPARATOR_COLON);

                    JsonObject jsonObject = new JsonParser()
                            .parse(jsonString)
                            .getAsJsonObject();

                    String amount = jsonObject.get(NLP_KEY_AMOUNT).getAsString();
                    String unit = jsonObject.get(NLP_KEY_UNIT).getAsString();

                    if (amount != null && unit != null) {
                        double d = Double.parseDouble(amount);
                        timerView.startTimer(d, unit);
                    }
                }

                break;
                case TIMER_SHOW:
                    timerView.checkAndShow();
                    break;

                case TIMER_STOP:
                    timerView.cancelTimer();
                    break;
            }
        }
    }
}
