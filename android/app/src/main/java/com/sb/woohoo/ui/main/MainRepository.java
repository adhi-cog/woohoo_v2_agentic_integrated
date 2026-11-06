package com.sb.woohoo.ui.main;

import static com.sb.woohoo.data.prefs.SharedPrefsKey.PREF_KEY_DEVICE_ID;
import static com.sb.woohoo.data.prefs.SharedPrefsKey.PREF_KEY_USER_DETAILS;
import static com.sb.woohoo.data.prefs.SharedPrefsKey.PREF_KEY_USER_ID;
import static com.sb.woohoo.ui.main.MainPresenter.REQUEST_BEGINNING;
import static com.sb.woohoo.ui.main.fragments.settings.engineswitch.EngineSwitchFragment.DIALOGFLOW_ENGINE;
import static com.sb.woohoo.ui.main.fragments.settings.engineswitch.EngineSwitchFragment.SMARTFLOW_ENGINE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.NETWORK_SUCCESS_CODE;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sb.woohoo.BuildConfig;
import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.devices.LightConstants;
import com.sb.woohoo.ui.main.fragments.devices.LightControl;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.Light;
import com.sb.woohoo.ui.main.fragments.livefeed.pojo.LiveFeedTokenModel;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.HubStatusModel;
import com.sb.woohoo.ui.main.fragments.reservation.confrim.pojo.BookingConfirmResponse;
import com.sb.woohoo.ui.main.fragments.screensaver.pojo.ScreenSaverMultiResponse;
import com.sb.woohoo.ui.main.fragments.settings.language.pojo.LanguageResponse;
import com.sb.woohoo.ui.main.fragments.settings.update.UpdateDataModel;
import com.sb.woohoo.ui.main.fragments.settings.update.UpdateModel;
import com.sb.woohoo.ui.main.fragments.surveillancemode.pojo.UserDetailModel;
import com.sb.woohoo.ui.main.fragments.weather.di.LatLngByIPModel;
import com.sb.woohoo.ui.main.pojo.DrawerGridItems;
import com.sb.woohoo.ui.main.pojo.EndSession;
import com.sb.woohoo.ui.main.pojo.OrderDetailsResponse;
import com.sb.woohoo.ui.main.pojo.PostQuery;
import com.sb.woohoo.ui.main.pojo.StartSession;
import com.sb.woohoo.ui.main.pojo.StatsDumpRequest;
import com.sb.woohoo.ui.main.pojo.StatsDumpResponse;
import com.sb.woohoo.ui.main.pojo.nlp.IntentData;
import com.sb.woohoo.ui.main.pojo.nlp.NLPData;
import com.sb.woohoo.ui.main.pojo.nlp.NLPResponse;
import com.sb.woohoo.ui.main.agentic.AgenticCommandDispatcher;
import com.sb.woohoo.ui.main.agentic.AgenticEngine;
import com.sb.woohoo.ui.main.pojo.agentic.AgenticAckRequest;
import com.sb.woohoo.ui.main.pojo.agentic.AgenticDeviceCommand;
import com.sb.woohoo.ui.main.pojo.agentic.AgenticTurnRequest;
import com.sb.woohoo.ui.main.pojo.agentic.AgenticTurnResponse;
import com.sb.woohoo.ui.main.pojo.nlp.SpeechData;
import com.sb.woohoo.ui.main.pojo.nlp.SpeechResponse;
import com.sb.woohoo.ui.main.pojo.template.TemplateData;
import com.sb.woohoo.ui.main.secondarydisplay.SecondaryDisplayData;
import com.sb.woohoo.ui.main.secondarydisplay.SecondaryDisplayImage;
import com.spotify.sdk.android.player.Metadata;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;

public class
MainRepository extends BaseRepository<MainPresenter> implements LightControl.LightControlCallback {

    /**
     * Duplicate and expiry rules for commands the agentic platform hands back. Held per
     * repository instance, which is per session: a command id only has to be remembered long
     * enough to catch the retry that follows it seconds later.
     */
    private final AgenticCommandDispatcher agenticDispatcher = new AgenticCommandDispatcher();
    private final String TAG = MainRepository.class.getSimpleName();
    private final String INTENT_CANCEL = "hub.controls.navigation.home";
    private final String FOLLOWUP_EXPLICIT = "followup";
    private final boolean FOLLOWUP_CANCEL = false;
    private SharedPrefsHelper mPrefsHelper;
    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;
    private Gson gson;
    private String querySend = "";
    private String queryLang = "";

    private int count = 0, countScreenSaver = 0;

    @Inject
    public MainRepository(WooHooApiCall wooHooApiCall,
                          SharedPrefsHelper prefsHelper,
                          CompositeDisposable compositeDisposable,
                          Gson gson) {
        LoggerWrapper.i(TAG, "Constructor");
        this.wooHooApiCall = wooHooApiCall;
        this.mPrefsHelper = prefsHelper;
        this.compositeDisposable = compositeDisposable;
        this.gson = gson;
    }

    void resetNlpContext() {
        //TODO will be added later.
//        compositeDisposable.add(
//                wooHooApiCall.resetDialogFlowContext(getDeviceID())
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribeWith(new DisposableCompletableObserver() {
//
//                            @Override
//                            public void onComplete() {
//
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                                getActions().onDialogFlowResetError(e.getMessage());
//                            }
//                        })
//        );
    }

    void processQuery(String query, int i, String lang) {
        count = i;
//        query="can you translate from english to german";

        querySend = query;
        queryLang = lang;
        getActions().stopWakeWord();
        LoggerWrapper.d(TAG, "Wake word stopped while NLP request is pending");
        PostQuery postQuery = new PostQuery();
        postQuery.setQuery(query);
        postQuery.setDeviceId(getDeviceID());
        // postQuery.setDeviceId("861107037615409");
        postQuery.setLang(lang);
        String localNlpUrl = BuildConfig.LOCAL_NLP_BASE_URL + "flow/conversation";
        String geminiIntentUrl = buildGeminiIntentUrl();

        if (AgenticEngine.isActive()) {
            processQueryWithAgentic(query, lang);
        } else if (!BuildConfig.GEMINI_INTENT_BASE_URL.isEmpty()) {
            LoggerWrapper.d(TAG, "Routing NLP request to Gemini intent API: " + geminiIntentUrl);
            compositeDisposable.add(
                    wooHooApiCall.geminiIntentRequest(geminiIntentUrl, postQuery)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<NLPResponse>() {

                                @Override
                                public void onSuccess(NLPResponse nlpResponse) {
                                    handleNLPResponse(nlpResponse);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    if (count < 3) {
                                        count++;
                                        processQuery(querySend, count, queryLang);
                                    }

                                    if (e instanceof HttpException) {
                                        HttpException exception = (HttpException) e;
                                        LoggerWrapper.e(MainRepository.class.getSimpleName(), "GEMINI INTENT ERROR CODE : " + exception.code());
                                    } else if (e.getMessage() != null) {
                                        LoggerWrapper.e(MainRepository.class.getSimpleName(), e.getMessage());
                                    }
                                    getActions().stopWakeWordAnimation();
                                    getActions().runWakeWord();
                                }
                            })
            );
        } else if (getEngine() == DIALOGFLOW_ENGINE) {
            compositeDisposable.add(
                    (BuildConfig.LOCAL_NLP_BASE_URL.isEmpty()
                            ? wooHooApiCall.languageProcessRequest(postQuery)
                            : wooHooApiCall.languageProcessRequestLocal(localNlpUrl, postQuery))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<NLPResponse>() {

                                @Override
                                public void onSuccess(NLPResponse nlpResponse) {
                                    handleNLPResponse(nlpResponse);
                                }

                                @Override
                                public void onError(Throwable e) {

                                    if (count < 3) {
                                        count++;
                                        processQuery(querySend, count, queryLang);
                                    }
                                    if (e instanceof HttpException) {
                                        HttpException exception = (HttpException) e;
                                        int errorCode = exception.code();
                                        LoggerWrapper.i(MainActivity.class.getSimpleName(), "languageProcessRequest:ERROR CODE : " + errorCode);
                                    } else {
                                        LoggerWrapper.e(MainRepository.class.getSimpleName(), e.getMessage());
                                    }
                                    getActions().stopWakeWordAnimation();
                                    getActions().runWakeWord();
                                }
                            })
            );
        } else {
            compositeDisposable.add(
                    (BuildConfig.LOCAL_NLP_BASE_URL.isEmpty()
                            ? wooHooApiCall.languageSmartFlow(postQuery)
                            : wooHooApiCall.languageSmartFlowLocal(localNlpUrl, postQuery))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<NLPResponse>() {

                                @Override
                                public void onSuccess(NLPResponse nlpResponse) {
                                    handleNLPResponse(nlpResponse);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    if (count < 3) {
                                        count++;
                                        processQuery(querySend, count, queryLang);
                                    }

                                    if (e instanceof HttpException) {
                                        HttpException exception = (HttpException) e;
                                        int errorCode = exception.code();
                                        LoggerWrapper.e(MainRepository.class.getSimpleName(), "ERROR CODE : " + errorCode);
                                    } else {
                                        if (e.getMessage() != null) {
                                            LoggerWrapper.e(MainRepository.class.getSimpleName(), e.getMessage());
                                        }
                                    }
                                    getActions().stopWakeWordAnimation();
                                    getActions().runWakeWord();
                                }
                            })
            );
        }
    }

    /**
     * One turn against the agentic platform.
     *
     * Replaces intent classification, not the microphone: the wake word and ASR above this
     * are untouched, and what changes is who decides what the words meant and who carries
     * the action out.
     *
     * Retries mirror the legacy engines (up to three) because the failure being retried is
     * the same one: a dropped request on a hotel network. Retrying is safe here for the same
     * reason it is safe there, with one addition - a repeated turn mints the same command
     * ids, so a hub that ends up seeing a response twice executes it once.
     */
    private void processQueryWithAgentic(String query, String lang) {
        String url = AgenticEngine.turnUrl();
        LoggerWrapper.d(TAG, "Routing turn to the agentic gateway: " + url);

        AgenticTurnRequest request = new AgenticTurnRequest(getDeviceID(), query, 0.95d);
        compositeDisposable.add(
                wooHooApiCall.agenticTurn(url, AgenticEngine.accessKey(), request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<AgenticTurnResponse>() {

                            @Override
                            public void onSuccess(AgenticTurnResponse response) {
                                handleAgenticResponse(response);
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (count < 3) {
                                    count++;
                                    processQuery(querySend, count, queryLang);
                                    return;
                                }
                                if (e instanceof HttpException) {
                                    LoggerWrapper.e(TAG, "AGENTIC ERROR CODE : " + ((HttpException) e).code());
                                } else if (e.getMessage() != null) {
                                    LoggerWrapper.e(TAG, "AGENTIC ERROR : " + e.getMessage());
                                }
                                // The guest is told, rather than left with silence that looks
                                // identical to the assistant ignoring them.
                                getActions().onResponseToUser(
                                        "I could not reach the assistant just then. Please try again.");
                                getActions().stopWakeWordAnimation();
                                getActions().runWakeWord();
                            }
                        })
        );
    }

    /**
     * Turn an agentic response into speech and device work.
     *
     * Order matters. Device commands are dispatched first so a screen is already opening as
     * the guest hears about it, which is how the legacy fragment intents behaved. Speech
     * comes second, and the microphone decision last.
     *
     * Nothing in {@code calls} is executed here, ever. Those ran server-side before this
     * response was written; re-dispatching them is how a light turns off and straight back on.
     */
    private void handleAgenticResponse(AgenticTurnResponse response) {
        if (response == null) {
            LoggerWrapper.e(TAG, "Empty agentic response");
            getActions().unprocessableIntent();
            return;
        }

        LoggerWrapper.d(TAG, "Agentic turn " + response.getTurnId()
                + " lane=" + response.getLane()
                + " calls=" + response.getCalls().size()
                + " deviceCommands=" + response.getDeviceCommands().size());

        if (response.hasFailedCall()) {
            // Speech already says so. Logged because a turn that sounds fine to the guest and
            // failed at the head-end is otherwise invisible.
            LoggerWrapper.e(TAG, "Agentic turn " + response.getTurnId() + " had a failed call");
        }

        for (AgenticDeviceCommand command : response.getDeviceCommands()) {
            dispatchAgenticCommand(command, response);
        }

        String speech = response.getSpeech();
        boolean hasSpeech = speech != null && !speech.trim().isEmpty();

        // A turn that asked the guest something has to reopen the microphone. Returning to
        // the wake word instead would make the assistant ask a question and then refuse to
        // hear the answer without being woken again.
        if (response.isAwaitingConfirmation() || isFollowUpLane(response.getLane())) {
            if (hasSpeech) {
                getActions().followUpForFulfillment(speech);
                return;
            }
        }

        if (hasSpeech) {
            getActions().onResponseToUser(speech);
        }
        getActions().stopWakeWordAnimation();
        getActions().runWakeWord();
    }

    private static boolean isFollowUpLane(String lane) {
        return "clarify".equals(lane) || "confirm".equals(lane);
    }

    /**
     * Run one device command, or decline it and say why.
     *
     * Most intents go through IntentProcessor exactly as an NLP intentAction did, which is
     * what keeps alarms, media and the rest working unchanged. A handful open a fragment
     * directly, because MainRepository always special-cased them.
     */
    private void dispatchAgenticCommand(AgenticDeviceCommand command, AgenticTurnResponse turn) {
        AgenticCommandDispatcher.Outcome outcome =
                agenticDispatcher.evaluate(command, System.currentTimeMillis());

        if (!outcome.shouldDispatch()) {
            LoggerWrapper.d(TAG, "Skipping device command "
                    + (command == null ? "null" : command.getCommandId())
                    + ": " + outcome.reason);
            if (command != null) {
                ackAgenticCommand(
                        command.getCommandId(),
                        outcome.decision == AgenticCommandDispatcher.Decision.SKIP_EXPIRED
                                ? "expired" : "unsupported",
                        outcome.reason);
            }
            return;
        }

        agenticDispatcher.markExecuted(command.getCommandId());
        String intent = command.getIntent();
        HashMap<String, Object> slots = command.getSlots();
        LoggerWrapper.d(TAG, "Dispatching device command " + command.getCommandId() + " -> " + intent);

        String screen = AgenticCommandDispatcher.directScreenFor(intent);
        try {
            if (screen == null) {
                // The ordinary path: the app's own registry resolves this to a fragment.
                getActions().processIntent(intent, slots, null, querySend, null, false);
            } else {
                openAgenticScreen(screen, slots);
            }
            ackAgenticCommand(command.getCommandId(), "applied", null);
        } catch (Exception dispatchFailure) {
            LoggerWrapper.e(TAG, "Device command " + command.getCommandId() + " failed: "
                    + dispatchFailure.getMessage());
            ackAgenticCommand(command.getCommandId(), "failed", dispatchFailure.getMessage());
        }
    }

    /** Screens the app opens directly rather than through the intent registry. */
    private void openAgenticScreen(String screen, HashMap<String, Object> slots) {
        switch (screen) {
            case AgenticCommandDispatcher.SCREEN_FNB:
                getActions().openFnBFragment(slots);
                break;
            case AgenticCommandDispatcher.SCREEN_FAQ:
                getActions().openFaqFragment();
                break;
            case AgenticCommandDispatcher.SCREEN_HOUSEKEEPING:
                getActions().openAmenitiesFragment();
                break;
            case AgenticCommandDispatcher.SCREEN_HOTEL_INFO:
                getActions().openHotelInfoFragment();
                break;
            case AgenticCommandDispatcher.SCREEN_BOOK_SERVICE:
                getActions().openBookServiceFragment();
                break;
            case AgenticCommandDispatcher.SCREEN_FEEDBACK:
                getActions().openFeedbackFragment();
                break;
            case AgenticCommandDispatcher.SCREEN_RESTAURANTS:
                getActions().openDiningRestaurantsFragment();
                break;
            case AgenticCommandDispatcher.SCREEN_RELAXATION_MUSIC:
                getActions().openRelaxationMusicFragment(slots);
                break;
            case AgenticCommandDispatcher.SCREEN_BUY_SIM:
                getActions().openBuySimFragment(slots);
                break;
            case AgenticCommandDispatcher.SCREEN_TRANSLATE:
                getActions().openTranlationFragment();
                break;
            case AgenticCommandDispatcher.SCREEN_MUSIC_SOURCE:
                // No screen of its own; the platform's speech asks which source to use.
                break;
            default:
                LoggerWrapper.e(TAG, "No screen mapped for " + screen);
                break;
        }
    }

    /**
     * Tell the platform what happened to a command.
     *
     * Failures are logged and swallowed. This is diagnostics: the guest has already been
     * answered, and letting a failed acknowledgement surface would turn a reporting problem
     * into a visible one.
     */
    private void ackAgenticCommand(String commandId, String status, String detail) {
        if (commandId == null || commandId.isEmpty() || !AgenticEngine.isActive()) {
            return;
        }
        compositeDisposable.add(
                wooHooApiCall.agenticCommandAck(
                                AgenticEngine.ackUrl(commandId),
                                AgenticEngine.accessKey(),
                                new AgenticAckRequest(status, detail, null))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> LoggerWrapper.d(TAG, "Acked " + commandId + " as " + status),
                                e -> LoggerWrapper.d(TAG, "Ack for " + commandId
                                        + " did not reach the platform: " + e.getMessage()))
        );
    }

    private String buildGeminiIntentUrl() {
        String baseUrl = BuildConfig.GEMINI_INTENT_BASE_URL;
        if (baseUrl.endsWith("/")) {
            return baseUrl + "api/intent/nlp";
        }
        return baseUrl + "/api/intent/nlp";
    }


    private int getEngine() {
        return WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_ENGINE, SMARTFLOW_ENGINE);
    }

    private void handleFulfillmentAction(SpeechData fulfillmentData) {
        // Ask followups and handle fulfilment
        String followUpQuestion = fulfillmentData.getSpeechText();
        if (followUpQuestion != null) {
            if (!followUpQuestion.trim().isEmpty()) {
                getActions().followUpForFulfillment(followUpQuestion);
            }
        }
    }

    private void handleIntentAction(IntentData intentData) {
        boolean explicitFollowUp = false;
        // FIXME parameters
        if (intentData.getIntentName() != null) {
            LoggerWrapper.i(MainRepository.class.getSimpleName(), "User Query: " + intentData.getUtterance());

//            if (intentData.getContexts() != null
//                    && intentData.getContexts().size() > 0) {
//                for (Context context : intentData.getContexts()) {
//                    if (context.getName().toLowerCase().endsWith(FOLLOWUP_EXPLICIT)) {
//                        explicitFollowUp = true;
//                    }
//                }
//            }

            if (intentData.getAction() == null || intentData.getAction().isEmpty()) {
                // FIXME : Cancelled intent logic for DialogFlow
                if (intentData.getAppUrl() != null && !intentData.getAppUrl().isEmpty()) {
                    if (intentData.getIntentName().equals("Show email")) {

                        intentData.setAppUrl(intentData.getAppUrl() + "/" + String.valueOf(intentData.getSlots().get("ordinal")));
                    }
                    if (intentData.getIntentName().equals("compose mail")) {
                        if (intentData.getSlots().containsKey("email")) {
                            intentData.setAppUrl(intentData.getAppUrl() + "?email=" + String.valueOf(intentData.getSlots().get("email")));
                        }
                    }
                    if (intentData.getIntentName().equals("reply mail")) {
                        if (intentData.getSlots().containsKey("ordinal")) {
                            intentData.setAppUrl(intentData.getAppUrl() + "/" + String.valueOf(intentData.getSlots().get("ordinal")) + "/reply");
                        }
                    }
                    if (intentData.getIntentName().equals("forward mail")) {
                        if (intentData.getSlots().containsKey("ordinal")) {
                            intentData.setAppUrl(intentData.getAppUrl() + "/" + String.valueOf(intentData.getSlots().get("ordinal")) + "/forward");
                        }
                    }
                    LoggerWrapper.d("appurl", intentData.getAppUrl());
                    getActions().switchToWebView(intentData.getAppUrl());
                } else {
                    String speechText = getSuccessSpeechText(intentData.getSpeech());
                    if (speechText != null && !speechText.trim().isEmpty()) {
                        getActions().onResponseToUser(speechText);
                        getActions().stopWakeWordAnimation();
                        getActions().runWakeWord();
                    } else {
                        getActions().processIntent(
                                INTENT_CANCEL,
                                intentData.getSlots(),
                                intentData.getSpeech(),
                                intentData.getUtterance(),
                                intentData.getAppUrl(),
                                FOLLOWUP_CANCEL);
                    }
                }

            } else {
                getActions().processIntent(intentData.getIntentName(), intentData.getSlots(),
                        intentData.getSpeech(), intentData.getUtterance(), intentData.getAppUrl(), explicitFollowUp);
            }
        } else {
            getActions().unprocessableIntent();
        }
    }

    private String getSuccessSpeechText(SpeechResponse speechResponse) {
        if (speechResponse == null || speechResponse.getOnSuccessSpeech() == null) {
            return null;
        }

        SpeechData speechData = speechResponse.getOnSuccessSpeech();
        if (speechData.getSpeechSsml() != null && !speechData.getSpeechSsml().trim().isEmpty()) {
            return speechData.getSpeechSsml();
        }
        return speechData.getSpeechText();
    }

    private void handleTemplateAction(TemplateData templateData) {
        getActions().processTemplates(templateData);
    }

    private boolean isSpeechAction(String action) {
        return action.contentEquals(NLPData.DeviceActions.ACTION_SPEECH)
                || action.contentEquals(NLPData.DeviceActions.ACTION_SPEECH_LEGACY);
    }

    private void handleMultiAction(NLPData multiActionData) {
        List<NLPData> actions = multiActionData.getActions();
        if (actions == null || actions.isEmpty()) {
            getActions().unprocessableIntent();
            return;
        }
        for (NLPData actionData : actions) {
            if (actionData == null || actionData.getDeviceAction() == null) {
                continue;
            }
            String action = actionData.getDeviceAction();
            if (action.contentEquals(NLPData.DeviceActions.ACTION_INTENT) && actionData.getIntentData() != null) {
                handleIntentAction(actionData.getIntentData());
            } else if (action.contentEquals(NLPData.DeviceActions.ACTION_TEMPLATE)) {
                handleTemplateAction(actionData.getTemplateData());
            } else if (action.contentEquals(NLPData.DeviceActions.ACTION_FULFILLMENT)) {
                handleFulfillmentAction(actionData.getFulfillmentData());
            } else if (isSpeechAction(action)
                    && actionData.getSpeechData() != null
                    && actionData.getSpeechData().getSpeechText() != null) {
                getActions().onResponseToUser(actionData.getSpeechData().getSpeechText());
            }
        }
        if (multiActionData.getSpeechData() != null && multiActionData.getSpeechData().getSpeechText() != null) {
            getActions().onResponseToUser(multiActionData.getSpeechData().getSpeechText());
        }
        getActions().stopWakeWordAnimation();
        getActions().runWakeWord();
    }

    private void handleNLPResponse(NLPResponse nlpResponse) {

        if (nlpResponse != null) {
            NLPData data = nlpResponse.getData();

            if (data != null) {
                // Handle follow ups
                String deviceAction = data.getDeviceAction();
                LoggerWrapper.d(TAG, "NLP response action=" + deviceAction
                        + ", speechText=" + (data.getSpeechData() != null ? data.getSpeechData().getSpeechText() : null));
                if (deviceAction != null && !deviceAction.isEmpty()) {
                    if (deviceAction.contentEquals(NLPData.DeviceActions.ACTION_FULFILLMENT)) {
                        handleFulfillmentAction(data.getFulfillmentData());
                    }

                    // Handle Intents
                    else if (deviceAction.contentEquals(NLPData.DeviceActions.ACTION_INTENT)) {

                        if (data.getIntentData().getIntentName().equals(Categories.Skills.Translate.COMMAND_ACTION_TRANSLATE)) {
                            getActions().followUpForFulfillment(data.getIntentData().getSpeech().getOnSuccessSpeech().getSpeechText());
                            getActions().openTranlationFragment();
                        }else if (data.getIntentData().getIntentName().equals("hub.skills.music.choose_source")) {
                            getActions().followUpForFulfillment(getSuccessSpeechText(data.getIntentData().getSpeech()));
                        } else if (data.getIntentData().getIntentName().equals(Categories.Skills.FnB.COMMAND_ACTION_FNB)) {
                            getActions().openFnBFragment(data.getIntentData().getSlots());
                            String speechText = getSuccessSpeechText(data.getIntentData().getSpeech());
                            if (speechText != null && !speechText.trim().isEmpty()) {
                                getActions().onResponseToUser(speechText);
                            }
                        } else if (data.getIntentData().getIntentName().startsWith(Categories.Skills.COMMAND_SKILL_RELAXATION_MUSIC)) {
                            HashMap<String, Object> slots = data.getIntentData().getSlots();
                            if (slots != null) {
                                slots.put("_voice_action", data.getIntentData().getIntentName());
                            }
                            getActions().openRelaxationMusicFragment(slots);
                            String intentName = data.getIntentData().getIntentName();
                            String speechText = getSuccessSpeechText(data.getIntentData().getSpeech());
                            boolean opensCategoryForManualSelection = Categories.Skills.RelaxationMusic.COMMAND_ACTION_SHOW.equals(intentName)
                                    && slots != null
                                    && slots.get("category_name") != null
                                    && !String.valueOf(slots.get("category_name")).trim().isEmpty();
                            boolean asksForMusicCategory = Categories.Skills.RelaxationMusic.COMMAND_ACTION_SHOW.equals(intentName)
                                    && !opensCategoryForManualSelection
                                    && speechText != null
                                    && speechText.trim().endsWith("?");
                            if (asksForMusicCategory) {
                                getActions().followUpForFulfillment(speechText);
                            } else if (speechText != null && !speechText.trim().isEmpty() && !opensCategoryForManualSelection) {
                                getActions().onResponseToUser(speechText);
                            }
                        } else if (data.getIntentData().getIntentName().startsWith(Categories.Skills.COMMAND_SKILL_BUY_SIM)) {
                            getActions().openBuySimFragment(data.getIntentData().getSlots());
                            getActions().stopWakeWordAnimation();
                            getActions().runWakeWord();
                        } else if (data.getIntentData().getIntentName().equals("hub.skills.faq")) {
                            getActions().openFaqFragment();
                            getActions().stopWakeWordAnimation();
                            getActions().runWakeWord();
                        } else if (data.getIntentData().getIntentName().equals("hub.skills.housekeeping")) {
                            getActions().openAmenitiesFragment();
                            getActions().stopWakeWordAnimation();
                            getActions().runWakeWord();
                        } else if (data.getIntentData().getIntentName().equals("hub.skills.hotelinfo")) {
                            getActions().openHotelInfoFragment();
                            getActions().stopWakeWordAnimation();
                            getActions().runWakeWord();
                        } else if (data.getIntentData().getIntentName().equals("hub.skills.service.book")) {
                            getActions().openBookServiceFragment();
                            getActions().stopWakeWordAnimation();
                            getActions().runWakeWord();
                        } else if (data.getIntentData().getIntentName().equals(Categories.Skills.Restaurants.COMMAND_ACTION_BOOK_TABLE)
                                || data.getIntentData().getIntentName().equals(Categories.Skills.Restaurants.COMMAND_ACTION_SHOW)) {
                            getActions().openDiningRestaurantsFragment();
                            String speechText = getSuccessSpeechText(data.getIntentData().getSpeech());
                            if (speechText != null && !speechText.trim().isEmpty()) {
                                getActions().followUpForFulfillment(speechText);
                            } else {
                                getActions().stopWakeWordAnimation();
                                getActions().runWakeWord();
                            }
                        } else if (data.getIntentData().getIntentName().equals("hub.skills.feedback")) {
                            getActions().openFeedbackFragment();
                            getActions().stopWakeWordAnimation();
                            getActions().runWakeWord();
                        }  else {
                            handleIntentAction(data.getIntentData());
                        }
                    }

                    // Handle Templates
                    else if (deviceAction.contentEquals(NLPData.DeviceActions.ACTION_TEMPLATE)) {
                        handleTemplateAction(data.getTemplateData());
                    }

                    // Handle Intents
                    else if (isSpeechAction(deviceAction)) {
                        if (data.getSpeechData() != null && data.getSpeechData().getSpeechText() != null) {
                            LoggerWrapper.d(TAG, "Speaking NLP speech_data: " + data.getSpeechData().getSpeechText());
                            respondWithSpeechData(data.getSpeechData().getSpeechText());
                        } else {
                            LoggerWrapper.e(TAG, "speech_data action received without speech_data.text");
                            getActions().onResponseToUser(R.string.didnt_get_that);
                            getActions().stopWakeWordAnimation();
                            getActions().runWakeWord();
                        }
                    } else if (deviceAction.contentEquals(NLPData.DeviceActions.ACTION_MULTI)) {
                        handleMultiAction(data);
                    } else {
                        LoggerWrapper.e(TAG, "API Response Exception");
                        getActions().onAPICallError(R.string.no_device_action_fetched);
                        getActions().stopWakeWordAnimation();
                        getActions().runWakeWord();
                    }
                } else if (!data.isUserAction()) {
                    if (data.getSpeechData() != null && data.getSpeechData().getSpeechText() != null) {
                        respondWithSpeechData(data.getSpeechData().getSpeechText());
                    } else {
                        getActions().onResponseToUser(R.string.didnt_get_that);
                        getActions().stopWakeWordAnimation();
                        getActions().runWakeWord();
                    }
                } else {

                    if (count < 3) {
                        count++;
                        processQuery(querySend, count, queryLang);
                    }
                    LoggerWrapper.e(TAG, "API Response Exception");
                    getActions().onAPICallError(R.string.no_data_fetched);
                    getActions().stopWakeWordAnimation();
                    getActions().runWakeWord();
                }

            } else {

                if (count < 3) {
                    count++;
                    processQuery(querySend, count, queryLang);
                }
                LoggerWrapper.e(TAG, "API Response Exception");
                getActions().onAPICallError(R.string.no_nlp_response_fetched);
                getActions().stopWakeWordAnimation();
                getActions().runWakeWord();
            }

        } else {

            if (count < 3) {
                count++;
                processQuery(querySend, count, queryLang);
            }
            LoggerWrapper.e(TAG, "API Response Exception");
            getActions().onAPICallError(R.string.no_nlp_response_fetched);
            getActions().stopWakeWordAnimation();
            getActions().runWakeWord();
        }
    }

    private void respondWithSpeechData(String speechText) {
        if (speechText != null && speechText.trim().endsWith("?")) {
            LoggerWrapper.d(TAG, "Routing speech_data through follow-up TTS");
            LoggerWrapper.d(TAG, "Calling MainPresenter.askFollowUpQuery for speech_data");
            getActions().askFollowUpQuery(speechText);
            LoggerWrapper.d(TAG, "Returned from MainPresenter.askFollowUpQuery for speech_data");
        } else {
            LoggerWrapper.d(TAG, "Routing speech_data through normal TTS");
            getActions().onResponseToUser(speechText);
            getActions().stopWakeWordAnimation();
            getActions().runWakeWord();
        }
    }

    void getUserDetails() {
        compositeDisposable.add(
                wooHooApiCall.getUserDetails(WooHooApp.getSharedPrefsHelper().get(PREF_KEY_USER_ID, null))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserDetailModel>() {
                            @Override
                            public void onSuccess(UserDetailModel userDetailModel) {
                                WooHooApp.getSharedPrefsHelper().setUserDetails(PREF_KEY_USER_DETAILS, new Gson().toJson(userDetailModel.getUserData()));
                            }

                            @Override
                            public void onError(Throwable e) {
                                LoggerWrapper.d("MainRepository", e.getMessage());
                            }
                        })
        );
    }

    public void updateHubStatus(boolean isBusy) {
        compositeDisposable.add(
                wooHooApiCall.updateHubStatus(getDeviceID(), isBusy)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                getActions().onHubStatusUpdateSuccess();
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onHubStatusUpdateError(e.getMessage());
                            }
                        }));
    }

    void requestLiveFeedToken(String deviceId) {
        compositeDisposable.add(
                wooHooApiCall.getLiveFeedToken(deviceId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<LiveFeedTokenModel>() {
                            @Override
                            public void onSuccess(LiveFeedTokenModel liveFeedToken) {
                                getActions().onLiveFeedTokenSuccess(liveFeedToken.getData());
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onLiveFeedTokenError(e.getMessage());
                            }
                        }));
    }

    public JsonObject getPlayStatusJSONObject(boolean isPlaying) {
        JsonObject playStatusJson = new JsonObject();
        playStatusJson.addProperty("playingStatus", isPlaying);
        playStatusJson.addProperty("hubId", mPrefsHelper.get(SharedPrefsKey.PREF_KEY_DEVICE_ID, ""));
        playStatusJson.addProperty("from", "hub");
        playStatusJson.addProperty("token", mPrefsHelper.get(SharedPrefsKey.PREF_KEY_SESSION_TOKEN, ""));
        return playStatusJson;
    }

    public JsonObject setMusicMetaData(Metadata data) {
        JsonObject musicDataJson = new JsonObject();
        musicDataJson.addProperty("from", "hub");
        musicDataJson.add("currentSong", getMusicMetaData(data.currentTrack));
        musicDataJson.add("previousSong", getMusicMetaData(data.prevTrack));
        musicDataJson.add("nextSong", getMusicMetaData(data.nextTrack));
        musicDataJson.addProperty("playingStatus", true);
        musicDataJson.addProperty("hubId", mPrefsHelper.get(SharedPrefsKey.PREF_KEY_DEVICE_ID, ""));
        musicDataJson.addProperty("token", mPrefsHelper.get(SharedPrefsKey.PREF_KEY_SESSION_TOKEN, ""));
        return musicDataJson;
    }

    public JsonObject getMuteData(boolean isMute) {
        JsonObject muteDataJson = new JsonObject();
        muteDataJson.addProperty("hubId", mPrefsHelper.get(SharedPrefsKey.PREF_KEY_DEVICE_ID, ""));
        muteDataJson.addProperty("token", mPrefsHelper.get(SharedPrefsKey.PREF_KEY_SESSION_TOKEN, ""));
        muteDataJson.addProperty("volume", !isMute);
        return muteDataJson;
    }

    private JsonObject getMusicMetaData(Metadata.Track track) {
        if (track == null) {
            return null;
        } else {
            JsonObject metaDataJson = new JsonObject();
            metaDataJson.addProperty("name", track.name);
            metaDataJson.addProperty("artist", track.artistName);
            metaDataJson.addProperty("image", track.albumCoverWebUrl);
            return metaDataJson;
        }
    }

    private String getDeviceID() {
        return mPrefsHelper == null ? "" : mPrefsHelper.get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");
    }

    void checkForHubStatus(final int request) {
        compositeDisposable.add(
                wooHooApiCall.getHubStatus(WooHooApp.getSharedPrefsHelper().get(PREF_KEY_DEVICE_ID, null))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<HubStatusModel>() {
                            @Override
                            public void onSuccess(HubStatusModel hubStatusModel) {
                                if (request == REQUEST_BEGINNING) {
                                    getActions().serverWorking();
                                    getActions().surveillanceMode(hubStatusModel.getHubStatus().getSurveillanceMode());
                                    getActions().onSettingsMode(hubStatusModel.getHubStatus().getSettingsMode());
                                } else {
                                    getActions().onSurveillanceModeChanged(hubStatusModel.getHubStatus().getSurveillanceMode());
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (e instanceof UnknownHostException) {
                                    getActions().onNoInternet(e.getMessage());
                                } else {
                                    getActions().onServerError(e.getMessage());
                                }
                            }
                        })
        );
    }

    public void makeAPICall(Light light) {
        LightControl lightControl = new LightControl(this);
        light.getState().setHue(light.getState().getHue() * LightConstants.EXTENDED_COLOR_LIGHT_MUL_FACTOR);
        lightControl.changeLightState(light, LightConstants.LightState.FROM_SOCKET, compositeDisposable, wooHooApiCall);

    }

    void getLatlngWithIP() {
        compositeDisposable.add(
                wooHooApiCall.getLatlngWithIP(getIP(), getDeviceId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<LatLngByIPModel>() {
                            @Override
                            public void onSuccess(LatLngByIPModel latLngByIPModel) {
                                if (latLngByIPModel != null) {
                                    getActions().IPRetriveSucess(latLngByIPModel);

                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        }));
    }

    private String getIP() {
        return mPrefsHelper == null ? "" : mPrefsHelper.get(SharedPrefsKey.PREF_KEY_IP, "");
    }

    private String getDeviceId() {
        return mPrefsHelper == null ? "" : mPrefsHelper.get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");
    }


    @Override
    public void onSuccess() {
        LoggerWrapper.i(TAG, "onSuccess: " + "Light State Updated");
    }

    @Override
    public void onError(String message) {
        LoggerWrapper.i(TAG, "onSuccess: " + "Light State Update Error");

    }

//    public void saveIPData(LatLngByIPModel latLngByIPModel) {
//        SharedPrefsHelper helper = mPrefsHelper;
//            helper.put(SharedPrefsKey.PREF_KEY_TIME_ZONE, latLngByIPModel.data.timezone);
//    }

    public void preformCheckUpdate() {
        compositeDisposable.add(wooHooApiCall.getUpdate(mPrefsHelper.get(SharedPrefsKey.PREF_KEY_DEVICE_ID, ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<UpdateModel>() {

                    @Override
                    public void onSuccess(UpdateModel updateModel) {
                        LoggerWrapper.i(MainRepository.class.getSimpleName(), "**** get version api sucess");
                        responseCall(updateModel);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoggerWrapper.e(MainRepository.class.getSimpleName(), "**** get version api error " + e.getMessage());

                        getActions().onCheckUpdateFailed();
                    }
                }));
    }

    private void responseCall(UpdateModel updateModel) {
        if (updateModel != null) {
            UpdateDataModel updateDataModel = updateModel.getData();
            if (updateDataModel != null) {
                getActions().onCheckUpdateSuccess(updateDataModel);
            } else {
                getActions().onCheckUpdateFailed();
            }
        } else {
            getActions().onCheckUpdateFailed();
        }
    }

    public void getScreenSaverTime(int i) {
        countScreenSaver = i;
        compositeDisposable.add(wooHooApiCall.getScreenSaverMulti(getDeviceID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ScreenSaverMultiResponse>() {
                    @Override
                    public void onSuccess(ScreenSaverMultiResponse screenSaverResponse) {
                        if (screenSaverResponse != null)
                            getActions().onScreenSaverFetched(screenSaverResponse.getData());
                        else {
                            if (countScreenSaver < 3) {
                                countScreenSaver++;
                                getScreenSaverTime(countScreenSaver);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (countScreenSaver < 3) {
                            countScreenSaver++;
                            getScreenSaverTime(countScreenSaver);
                        }
                        LoggerWrapper.e(MainRepository.class.getSimpleName(), "**** timezone retrive error " + e.getMessage());
                    }
                }));
    }

    public void getSecondDisplayData() {
        compositeDisposable.add(wooHooApiCall.getScreenSaverMulti(getDeviceID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ScreenSaverMultiResponse>() {
                    @Override
                    public void onSuccess(ScreenSaverMultiResponse screenSaverResponse) {
                        if (screenSaverResponse != null && screenSaverResponse.getData()!= null) {
                            List<SecondaryDisplayImage> images = new ArrayList<>();
                            if(screenSaverResponse.getData().getSlideShow() && screenSaverResponse.getData().getSlideShowImages()!= null && screenSaverResponse.getData().getSlideShowImages().size() > 0) {
                                for (String url : screenSaverResponse.getData().getSlideShowImages()) {
                                    images.add(new SecondaryDisplayImage(url, screenSaverResponse.getData().getTitle(), screenSaverResponse.getData().getDescription()));
                                }
                            } else if(screenSaverResponse.getData().getImageUrl() != null) {
                                images.add(new SecondaryDisplayImage(screenSaverResponse.getData().getImageUrl(), screenSaverResponse.getData().getTitle(), screenSaverResponse.getData().getDescription()));
                            }
                            if(images.size() > 0) {
                                getActions().onSecondDisplayDataFetched(new SecondaryDisplayData(screenSaverResponse.getData().getSlideShowTimeInMillis(),images));
                            } else {
                                LoggerWrapper.e("MainRepository", "getScreenSaverMulti: no screensaver found");
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoggerWrapper.e(MainRepository.class.getSimpleName(), "getSecondDisplayData" + e.getMessage());
                    }
                }));
    }

    public void startSessionApi(DrawerGridItems drawerGridItems) {

        compositeDisposable.add(wooHooApiCall.startSession(
                mPrefsHelper.get(SharedPrefsKey.PREF_KEY_DEVICE_ID, ""), "" + drawerGridItems.getData().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<StartSession>() {

                    @Override
                    public void onSuccess(StartSession appstoreRes) {
                        getActions().onStartSessionSucess(appstoreRes);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoggerWrapper.e(MainRepository.class.getSimpleName(), "**** session start error " + e.getMessage());
                        getActions().onError(R.string.try_again);

                    }
                }));
    }


    public void placeOrder(JSONObject body) {
        try {
            compositeDisposable.add(wooHooApiCall.placeOrder(body.getString("hubId"), body.getString("quantity"), body.getString("hubId"), body.getString("price"), body.getString("userId"), body.getString("itemName"))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<OrderDetailsResponse>() {
                        @Override
                        public void onSuccess(OrderDetailsResponse screenSaverResponse) {
                            getActions().orderPlaced();

                        }

                        @Override
                        public void onError(Throwable e) {
                            LoggerWrapper.e(MainRepository.class.getSimpleName(), "**** timezone retrive error " + e.getMessage());
                        }
                    }));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void endSessionApi(StartSession sessionId) {

        compositeDisposable.add(wooHooApiCall.endSession(sessionId.getData().getSessionId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<EndSession>() {

                    @Override
                    public void onSuccess(EndSession appstoreRes) {
                        LoggerWrapper.i(MainRepository.class.getSimpleName(), "**** session end api");
                        getActions().onSessionEnd(appstoreRes);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoggerWrapper.e(MainRepository.class.getSimpleName(), "**** session end error " + e.getMessage());
                        getActions().onError(R.string.try_again);
                    }
                }));
    }

    void updateAppVersion() {
        String versionName = BuildConfig.VERSION_NAME;
        compositeDisposable.add(
                wooHooApiCall.updateAppVersion(getDeviceId(), versionName)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                LoggerWrapper.e("TAG", "**** App version Update " + versionName);
                            }

                            @Override
                            public void onError(Throwable e) {
                                LoggerWrapper.e("TAG", "**** App version Update error " + e.getMessage());
                            }
                        }));
    }

    public void callStatsDumpAPI(StatsDumpRequest statsDumpRequest) {
        compositeDisposable.add(
                wooHooApiCall.callStatsDumpAPI(statsDumpRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<StatsDumpResponse>() {
                            @Override
                            public void onSuccess(StatsDumpResponse response) {

                            }
                            @Override
                            public void onError(Throwable e) {

                            }
                        }));
    }

    void getLanguageRequest()
    {
        compositeDisposable.add(wooHooApiCall.getAllLocale().subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(
                        new DisposableSingleObserver<LanguageResponse>()
        {
            @Override
            public void onSuccess(LanguageResponse languageResponse) {
                LoggerWrapper.e(MainRepository.class.getSimpleName(),
                        "**** Get All locale success " + languageResponse.toString());
                int status = languageResponse.getStatus();
                if (status == NETWORK_SUCCESS_CODE) {
                    if (!languageResponse.getData().isEmpty()) {
                        getActions().onAllLocaleFetchSucess(languageResponse.getData());
                    } else {
                        getActions().onError(R.string.no_result_fetched);
                    }
                } else {
                    getActions().onError(status);
                }
            }

            @Override
            public void onError(Throwable e) {
                LoggerWrapper.e(MainRepository.class.getSimpleName(),
                        "**** Get All locale error " + e.getMessage());
                getActions().onError(R.string.bad_gateway);
            }
        }));
    }
}
