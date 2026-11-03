package com.sb.woohoo.utils.speech;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.SpeechContext;
import com.google.cloud.speech.v1.SpeechGrpc;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.StreamingRecognitionConfig;
import com.google.cloud.speech.v1.StreamingRecognitionResult;
import com.google.cloud.speech.v1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1.StreamingRecognizeResponse;
import com.google.protobuf.ByteString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import io.grpc.okhttp.OkHttpChannelBuilder;
import io.grpc.stub.MetadataUtils;
import io.grpc.stub.StreamObserver;

public class GoogleCloudStreamingAsrClient {

    private static final String ASR_LOG_TAG = "ASR_FLOW";
    private static final String SPEECH_HOST = "speech.googleapis.com";
    private static final int SPEECH_PORT = 443;
    private static final long NO_TRANSCRIPT_TIMEOUT_MS = 15000L;
    private static final String GOOGLE_STT_MODEL = "latest_short";
    private static final List<String> COMMAND_HINTS = Arrays.asList(
            "open youtube",
            "open netflix",
            "open curtains",
            "close curtains",
            "turn on the lights",
            "turn off the lights",
            "turn on the tv",
            "turn off the tv",
            "send towels",
            "room service",
            "play music",
            "stop music"
    );

    public interface Callback {
        void onStreamingReady();

        void onRmsChanged(float rmsdB);

        void onPartialTranscript(String transcript);

        void onFinalTranscript(String transcript);

        void onError(Throwable throwable);
    }

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ServiceAccountTokenProvider tokenProvider;
    private final Callback callback;
    private final VoiceRecorder voiceRecorder;
    private final AtomicBoolean sessionActive = new AtomicBoolean(false);
    private final AtomicBoolean finalDelivered = new AtomicBoolean(false);
    private final AtomicInteger nextSessionId = new AtomicInteger(1);
    private volatile ManagedChannel managedChannel;
    private volatile StreamObserver<StreamingRecognizeRequest> requestObserver;
    private volatile int activeSessionId = -1;
    private volatile long sessionStartedAtMs;
    private volatile int audioChunkCount;
    private volatile long lastAudioLogAtMs;
    private volatile boolean transcriptObserved;
    private volatile boolean streamReadyNotified;
    private final Runnable noTranscriptWatchdog = new Runnable() {
        @Override
        public void run() {
            if (!sessionActive.get() || finalDelivered.get() || transcriptObserved) {
                return;
            }
            long elapsed = System.currentTimeMillis() - sessionStartedAtMs;
            Log.e(ASR_LOG_TAG, "gRPC ASR session#" + activeSessionId
                    + " timed out without transcript after " + elapsed + " ms");
            notifyError(new IllegalStateException("No gRPC transcript received before timeout"));
        }
    };

    public GoogleCloudStreamingAsrClient(@NonNull Context context, @NonNull Callback callback) {
        this.tokenProvider = new ServiceAccountTokenProvider(context);
        this.callback = callback;
        this.voiceRecorder = new VoiceRecorder(new VoiceRecorder.Callback() {
            @Override
            public void onVoiceStart() {
                Log.d(ASR_LOG_TAG, "gRPC ASR session#" + activeSessionId + " recorder detected voice start");
                notifyStreamingReadyOnce();
            }

            @Override
            public void onVoice(byte[] data, int size) {
                if (size <= 0) {
                    return;
                }
                byte[] chunk = new byte[size];
                System.arraycopy(data, 0, chunk, 0, size);
                emitRms(chunk, size);
                sendAudioChunk(chunk);
            }

            @Override
            public void onVoiceEnd() {
                Log.d(ASR_LOG_TAG, "gRPC ASR session#" + activeSessionId + " recorder detected voice end");
                finishStreamingRequest();
            }
        });
    }

    public void start(@NonNull String languageCode) {
        stop();
        activeSessionId = nextSessionId.getAndIncrement();
        sessionStartedAtMs = System.currentTimeMillis();
        audioChunkCount = 0;
        lastAudioLogAtMs = 0L;
        transcriptObserved = false;
        streamReadyNotified = false;
        sessionActive.set(true);
        finalDelivered.set(false);
        mainHandler.removeCallbacks(noTranscriptWatchdog);
        mainHandler.postDelayed(noTranscriptWatchdog, NO_TRANSCRIPT_TIMEOUT_MS);
        executorService.execute(() -> {
            try {
                String accessToken = tokenProvider.getAccessToken();
                Metadata headers = new Metadata();
                headers.put(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER),
                        "Bearer " + accessToken);
                managedChannel = OkHttpChannelBuilder.forAddress(SPEECH_HOST, SPEECH_PORT)
                        .useTransportSecurity()
                        .build();
                SpeechGrpc.SpeechStub speechStub = SpeechGrpc.newStub(managedChannel)
                        .withInterceptors(MetadataUtils.newAttachHeadersInterceptor(headers));
                requestObserver = speechStub.streamingRecognize(createResponseObserver());
                requestObserver.onNext(StreamingRecognizeRequest.newBuilder()
                        .setStreamingConfig(StreamingRecognitionConfig.newBuilder()
                                .setConfig(buildRecognitionConfig(languageCode))
                                .setInterimResults(true)
                                .setSingleUtterance(false)
                                .setEnableVoiceActivityEvents(true)
                                .build())
                        .build());
                Log.d(ASR_LOG_TAG, "Using gRPC ASR: streaming started"
                        + " session#" + activeSessionId
                        + " lang=" + languageCode
                        + " model=" + GOOGLE_STT_MODEL);
                voiceRecorder.start();
                Log.d(ASR_LOG_TAG, "gRPC ASR session#" + activeSessionId
                        + " voice recorder started at sampleRate=" + voiceRecorder.getSampleRate());
            } catch (Exception e) {
                notifyError(e);
            }
        });
    }

    public void warmUp() {
        executorService.execute(() -> {
            try {
                long startedAt = System.currentTimeMillis();
                tokenProvider.getAccessToken();
                Log.d(ASR_LOG_TAG, "gRPC ASR warmup finished in "
                        + (System.currentTimeMillis() - startedAt) + " ms");
            } catch (Exception e) {
                Log.w(ASR_LOG_TAG, "gRPC ASR warmup failed", e);
            }
        });
    }

    public void stop() {
        sessionActive.set(false);
        finalDelivered.set(false);
        transcriptObserved = false;
        streamReadyNotified = false;
        mainHandler.removeCallbacks(noTranscriptWatchdog);
        voiceRecorder.stop();
        finishStreamingRequest();
        shutdownChannel();
    }

    private RecognitionConfig buildRecognitionConfig(String languageCode) {
        return RecognitionConfig.newBuilder()
                .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                .setSampleRateHertz(16000)
                .setAudioChannelCount(1)
                .setLanguageCode(languageCode)
                .addAllAlternativeLanguageCodes(buildAlternativeLanguageCodes(languageCode))
                .setModel(GOOGLE_STT_MODEL)
                .setEnableAutomaticPunctuation(true)
                .addSpeechContexts(SpeechContext.newBuilder()
                        .addAllPhrases(COMMAND_HINTS)
                        .setBoost(18.0f)
                        .build())
                .build();
    }

    private StreamObserver<StreamingRecognizeResponse> createResponseObserver() {
        return new StreamObserver<StreamingRecognizeResponse>() {
            @Override
            public void onNext(StreamingRecognizeResponse response) {
                Log.d(ASR_LOG_TAG, "gRPC ASR session#" + activeSessionId
                        + " received response"
                        + " resultsCount=" + response.getResultsCount()
                        + " speechEvent=" + response.getSpeechEventType().name());
                if (response.getResultsCount() == 0) {
                    Log.d(ASR_LOG_TAG, "gRPC ASR session#" + activeSessionId
                            + " received response with no results");
                    return;
                }

                StreamingRecognitionResult result = response.getResults(0);
                if (result.getAlternativesCount() == 0) {
                    Log.d(ASR_LOG_TAG, "gRPC ASR session#" + activeSessionId
                            + " received result with no alternatives");
                    return;
                }

                SpeechRecognitionAlternative alternative = result.getAlternatives(0);
                String transcript = alternative.getTranscript();
                if (transcript == null || transcript.trim().isEmpty()) {
                    Log.d(ASR_LOG_TAG, "gRPC ASR session#" + activeSessionId
                            + " received empty transcript result");
                    return;
                }

                transcriptObserved = true;
                mainHandler.removeCallbacks(noTranscriptWatchdog);
                mainHandler.postDelayed(noTranscriptWatchdog, NO_TRANSCRIPT_TIMEOUT_MS);

                if (result.getIsFinal()) {
                    Log.d(ASR_LOG_TAG, "gRPC ASR final session#" + activeSessionId + ": " + transcript);
                    if (finalDelivered.compareAndSet(false, true)) {
                        sessionActive.set(false);
                        mainHandler.removeCallbacks(noTranscriptWatchdog);
                        postToMain(() -> callback.onFinalTranscript(transcript));
                        executorService.execute(() -> {
                            voiceRecorder.stop();
                            finishStreamingRequest();
                            shutdownChannel();
                        });
                    }
                } else {
                    Log.d(ASR_LOG_TAG, "gRPC ASR partial session#" + activeSessionId + ": " + transcript);
                    postToMain(() -> callback.onPartialTranscript(transcript));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(ASR_LOG_TAG, "gRPC ASR session#" + activeSessionId + " response stream error", throwable);
                notifyError(throwable);
            }

            @Override
            public void onCompleted() {
                Log.d(ASR_LOG_TAG, "gRPC ASR session#" + activeSessionId
                        + " response stream completed; finalDelivered=" + finalDelivered.get()
                        + ", transcriptObserved=" + transcriptObserved);
                mainHandler.removeCallbacks(noTranscriptWatchdog);
                shutdownChannel();
                if (!transcriptObserved && !finalDelivered.get()) {
                    notifyError(new IllegalStateException("gRPC stream completed without transcript"));
                }
            }
        };
    }

    private void sendAudioChunk(byte[] chunk) {
        if (!sessionActive.get()) {
            return;
        }
        StreamObserver<StreamingRecognizeRequest> localRequestObserver = requestObserver;
        if (localRequestObserver == null) {
            return;
        }
        audioChunkCount++;
        long now = System.currentTimeMillis();
        if (audioChunkCount <= 3 || now - lastAudioLogAtMs >= 1000L) {
            Log.d(ASR_LOG_TAG, "gRPC ASR session#" + activeSessionId
                    + " sending audio chunk size=" + chunk.length
                    + " count=" + audioChunkCount
                    + " elapsedMs=" + (now - sessionStartedAtMs));
            lastAudioLogAtMs = now;
        }
        localRequestObserver.onNext(StreamingRecognizeRequest.newBuilder()
                .setAudioContent(ByteString.copyFrom(chunk))
                .build());
    }

    private void finishStreamingRequest() {
        StreamObserver<StreamingRecognizeRequest> localRequestObserver = requestObserver;
        requestObserver = null;
        if (localRequestObserver != null) {
            try {
                Log.d(ASR_LOG_TAG, "gRPC ASR session#" + activeSessionId + " closing request stream");
                localRequestObserver.onCompleted();
            } catch (StatusRuntimeException ignored) {
            }
        }
    }

    private void shutdownChannel() {
        ManagedChannel channel = managedChannel;
        managedChannel = null;
        if (channel != null && !channel.isShutdown()) {
            channel.shutdownNow();
        }
    }

    private void emitRms(byte[] chunk, int size) {
        long sum = 0;
        int samples = 0;
        for (int i = 0; i < size - 1; i += 2) {
            short sample = (short) ((chunk[i] & 0xff) | (chunk[i + 1] << 8));
            sum += sample * (long) sample;
            samples++;
        }
        if (samples == 0) {
            return;
        }
        double rms = Math.sqrt(sum / (double) samples);
        float rmsdB = (float) (20 * Math.log10(Math.max(rms, 1d)));
        postToMain(() -> callback.onRmsChanged(rmsdB));
    }

    private void notifyError(Throwable throwable) {
        if (!sessionActive.getAndSet(false)) {
            return;
        }
        mainHandler.removeCallbacks(noTranscriptWatchdog);
        voiceRecorder.stop();
        finishStreamingRequest();
        shutdownChannel();
        postToMain(() -> callback.onError(throwable));
    }

    private void notifyStreamingReadyOnce() {
        if (streamReadyNotified) {
            return;
        }
        streamReadyNotified = true;
        postToMain(callback::onStreamingReady);
    }

    private List<String> buildAlternativeLanguageCodes(String primaryLanguageCode) {
        List<String> alternatives = new ArrayList<>();
        addAlternativeLanguage(alternatives, primaryLanguageCode, "en-US");
        addAlternativeLanguage(alternatives, primaryLanguageCode, "en-GB");

        if ("hi-IN".equalsIgnoreCase(primaryLanguageCode)
                || "en-US".equalsIgnoreCase(primaryLanguageCode)
                || "en-GB".equalsIgnoreCase(primaryLanguageCode)
                || "en-IN".equalsIgnoreCase(primaryLanguageCode)) {
            addAlternativeLanguage(alternatives, primaryLanguageCode, "hi-IN");
            addAlternativeLanguage(alternatives, primaryLanguageCode, "en-IN");
        } else if ("zh-CN".equalsIgnoreCase(primaryLanguageCode)) {
            addAlternativeLanguage(alternatives, primaryLanguageCode, "zh-CN");
            addAlternativeLanguage(alternatives, primaryLanguageCode, "hi-IN");
        } else if ("ar-SA".equalsIgnoreCase(primaryLanguageCode)) {
            addAlternativeLanguage(alternatives, primaryLanguageCode, "ar-SA");
            addAlternativeLanguage(alternatives, primaryLanguageCode, "hi-IN");
        } else if ("de-DE".equalsIgnoreCase(primaryLanguageCode)) {
            addAlternativeLanguage(alternatives, primaryLanguageCode, "de-DE");
            addAlternativeLanguage(alternatives, primaryLanguageCode, "hi-IN");
        } else if ("ru-RU".equalsIgnoreCase(primaryLanguageCode)) {
            addAlternativeLanguage(alternatives, primaryLanguageCode, "ru-RU");
            addAlternativeLanguage(alternatives, primaryLanguageCode, "hi-IN");
        } else if ("fr-FR".equalsIgnoreCase(primaryLanguageCode)) {
            addAlternativeLanguage(alternatives, primaryLanguageCode, "fr-FR");
            addAlternativeLanguage(alternatives, primaryLanguageCode, "hi-IN");
        } else if ("es-ES".equalsIgnoreCase(primaryLanguageCode)) {
            addAlternativeLanguage(alternatives, primaryLanguageCode, "es-ES");
            addAlternativeLanguage(alternatives, primaryLanguageCode, "hi-IN");
        } else if ("nl-NL".equalsIgnoreCase(primaryLanguageCode)) {
            addAlternativeLanguage(alternatives, primaryLanguageCode, "nl-NL");
            addAlternativeLanguage(alternatives, primaryLanguageCode, "hi-IN");
        } else if ("it-IT".equalsIgnoreCase(primaryLanguageCode)) {
            addAlternativeLanguage(alternatives, primaryLanguageCode, "it-IT");
            addAlternativeLanguage(alternatives, primaryLanguageCode, "hi-IN");
        }
        return alternatives.size() > 3 ? alternatives.subList(0, 3) : alternatives;
    }

    private void addAlternativeLanguage(List<String> alternatives, String primaryLanguageCode, String languageCode) {
        if (alternatives.size() >= 3
                || languageCode.equalsIgnoreCase(primaryLanguageCode)
                || alternatives.contains(languageCode)) {
            return;
        }
        alternatives.add(languageCode);
    }

    private void postToMain(Runnable runnable) {
        mainHandler.post(runnable);
    }
}
