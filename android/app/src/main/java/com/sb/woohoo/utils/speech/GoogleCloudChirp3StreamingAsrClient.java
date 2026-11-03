package com.sb.woohoo.utils.speech;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.cloud.speech.v2.ExplicitDecodingConfig;
import com.google.cloud.speech.v2.RecognitionConfig;
import com.google.cloud.speech.v2.RecognitionFeatures;
import com.google.cloud.speech.v2.SpeechGrpc;
import com.google.cloud.speech.v2.SpeechRecognitionAlternative;
import com.google.cloud.speech.v2.StreamingRecognitionConfig;
import com.google.cloud.speech.v2.StreamingRecognitionFeatures;
import com.google.cloud.speech.v2.StreamingRecognitionResult;
import com.google.cloud.speech.v2.StreamingRecognizeRequest;
import com.google.cloud.speech.v2.StreamingRecognizeResponse;
import com.google.protobuf.ByteString;

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

public class GoogleCloudChirp3StreamingAsrClient {

    private static final String ASR_LOG_TAG = "ASR_FLOW";
    private static final String CHIRP3_REGION = "us";
    private static final String SPEECH_HOST = CHIRP3_REGION + "-speech.googleapis.com";
    private static final int SPEECH_PORT = 443;
    private static final long NO_TRANSCRIPT_TIMEOUT_MS = 15000L;
    private static final String CHIRP3_MODEL = "chirp_3";
    private static final String CHIRP3_AUTO_LANGUAGE = "auto";

    public interface Callback {
        void onStreamingReady();

        void onRmsChanged(float rmsdB);

        void onPartialTranscript(String transcript);

        void onFinalTranscript(String transcript, String languageCode);

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
    private volatile String recognizerName;
    private final Runnable noTranscriptWatchdog = new Runnable() {
        @Override
        public void run() {
            if (!sessionActive.get() || finalDelivered.get() || transcriptObserved) {
                return;
            }
            long elapsed = System.currentTimeMillis() - sessionStartedAtMs;
            Log.e(ASR_LOG_TAG, "Chirp 3 ASR session#" + activeSessionId
                    + " timed out without transcript after " + elapsed + " ms");
            notifyError(new IllegalStateException("No Chirp 3 transcript received before timeout"));
        }
    };

    public GoogleCloudChirp3StreamingAsrClient(@NonNull Context context, @NonNull Callback callback) {
        this.tokenProvider = new ServiceAccountTokenProvider(context);
        this.callback = callback;
        this.voiceRecorder = new VoiceRecorder(new VoiceRecorder.Callback() {
            @Override
            public void onVoiceStart() {
                Log.d(ASR_LOG_TAG, "Chirp 3 ASR session#" + activeSessionId
                        + " recorder detected voice start");
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
                Log.d(ASR_LOG_TAG, "Chirp 3 ASR session#" + activeSessionId
                        + " recorder detected voice end");
                finishStreamingRequest();
            }
        });
    }

    public void start(@NonNull String fallbackLanguageCode) {
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
                String projectId = tokenProvider.getProjectId();
                recognizerName = "projects/" + projectId + "/locations/"
                        + CHIRP3_REGION + "/recognizers/_";

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
                        .setRecognizer(recognizerName)
                        .setStreamingConfig(StreamingRecognitionConfig.newBuilder()
                                .setConfig(buildRecognitionConfig())
                                .setStreamingFeatures(StreamingRecognitionFeatures.newBuilder()
                                        .setInterimResults(true)
                                        .setEnableVoiceActivityEvents(true)
                                        .setEndpointingSensitivity(
                                                StreamingRecognitionFeatures.EndpointingSensitivity
                                                        .ENDPOINTING_SENSITIVITY_SHORT)
                                        .build())
                                .build())
                        .build());
                Log.d(ASR_LOG_TAG, "Using Chirp 3 gRPC ASR: streaming started"
                        + " session#" + activeSessionId
                        + " recognizer=" + recognizerName
                        + " fallbackLang=" + fallbackLanguageCode
                        + " language=" + CHIRP3_AUTO_LANGUAGE
                        + " model=" + CHIRP3_MODEL);
                voiceRecorder.start();
                Log.d(ASR_LOG_TAG, "Chirp 3 ASR session#" + activeSessionId
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
                tokenProvider.getProjectId();
                Log.d(ASR_LOG_TAG, "Chirp 3 ASR warmup finished in "
                        + (System.currentTimeMillis() - startedAt) + " ms");
            } catch (Exception e) {
                Log.w(ASR_LOG_TAG, "Chirp 3 ASR warmup failed", e);
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

    private RecognitionConfig buildRecognitionConfig() {
        return RecognitionConfig.newBuilder()
                .setExplicitDecodingConfig(ExplicitDecodingConfig.newBuilder()
                        .setEncoding(ExplicitDecodingConfig.AudioEncoding.LINEAR16)
                        .setSampleRateHertz(16000)
                        .setAudioChannelCount(1)
                        .build())
                .setModel(CHIRP3_MODEL)
                .addLanguageCodes(CHIRP3_AUTO_LANGUAGE)
                .setFeatures(RecognitionFeatures.newBuilder()
                        .setEnableAutomaticPunctuation(true)
                        .setMaxAlternatives(1)
                        .build())
                .build();
    }

    private StreamObserver<StreamingRecognizeResponse> createResponseObserver() {
        return new StreamObserver<StreamingRecognizeResponse>() {
            @Override
            public void onNext(StreamingRecognizeResponse response) {
                Log.d(ASR_LOG_TAG, "Chirp 3 ASR session#" + activeSessionId
                        + " received response"
                        + " resultsCount=" + response.getResultsCount()
                        + " speechEvent=" + response.getSpeechEventType().name());
                if (response.getResultsCount() == 0) {
                    return;
                }

                StreamingRecognitionResult result = response.getResults(0);
                if (result.getAlternativesCount() == 0) {
                    return;
                }

                SpeechRecognitionAlternative alternative = result.getAlternatives(0);
                String transcript = alternative.getTranscript();
                if (transcript == null || transcript.trim().isEmpty()) {
                    return;
                }

                transcriptObserved = true;
                mainHandler.removeCallbacks(noTranscriptWatchdog);
                mainHandler.postDelayed(noTranscriptWatchdog, NO_TRANSCRIPT_TIMEOUT_MS);

                String detectedLanguage = result.getLanguageCode();
                if (result.getIsFinal()) {
                    Log.d(ASR_LOG_TAG, "Chirp 3 ASR final session#" + activeSessionId
                            + " lang=" + detectedLanguage + ": " + transcript);
                    if (finalDelivered.compareAndSet(false, true)) {
                        sessionActive.set(false);
                        mainHandler.removeCallbacks(noTranscriptWatchdog);
                        postToMain(() -> callback.onFinalTranscript(transcript, detectedLanguage));
                        executorService.execute(() -> {
                            voiceRecorder.stop();
                            finishStreamingRequest();
                            shutdownChannel();
                        });
                    }
                } else {
                    Log.d(ASR_LOG_TAG, "Chirp 3 ASR partial session#" + activeSessionId
                            + " lang=" + detectedLanguage + ": " + transcript);
                    postToMain(() -> callback.onPartialTranscript(transcript));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(ASR_LOG_TAG, "Chirp 3 ASR session#" + activeSessionId
                        + " response stream error", throwable);
                notifyError(throwable);
            }

            @Override
            public void onCompleted() {
                Log.d(ASR_LOG_TAG, "Chirp 3 ASR session#" + activeSessionId
                        + " response stream completed; finalDelivered=" + finalDelivered.get()
                        + ", transcriptObserved=" + transcriptObserved);
                mainHandler.removeCallbacks(noTranscriptWatchdog);
                shutdownChannel();
                if (!transcriptObserved && !finalDelivered.get()) {
                    notifyError(new IllegalStateException("Chirp 3 stream completed without transcript"));
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
            Log.d(ASR_LOG_TAG, "Chirp 3 ASR session#" + activeSessionId
                    + " sending audio chunk size=" + chunk.length
                    + " count=" + audioChunkCount
                    + " elapsedMs=" + (now - sessionStartedAtMs));
            lastAudioLogAtMs = now;
        }
        localRequestObserver.onNext(StreamingRecognizeRequest.newBuilder()
                .setAudio(ByteString.copyFrom(chunk))
                .build());
    }

    private void finishStreamingRequest() {
        StreamObserver<StreamingRecognizeRequest> localRequestObserver = requestObserver;
        requestObserver = null;
        if (localRequestObserver != null) {
            try {
                Log.d(ASR_LOG_TAG, "Chirp 3 ASR session#" + activeSessionId
                        + " closing request stream");
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

    private void postToMain(Runnable runnable) {
        mainHandler.post(runnable);
    }
}
