package com.sb.woohoo.utils.speech;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.annotation.NonNull;

/**
 * Records 16k mono PCM and streams fixed-size chunks to the callback.
 *
 * <p>This mirrors the working reference app much more closely: fixed 16k sampling, 100 ms chunks,
 * and audio-source fallback between {@link MediaRecorder.AudioSource#VOICE_RECOGNITION} and
 * {@link MediaRecorder.AudioSource#MIC}. Local VAD is kept only for UI/session boundaries.</p>
 */
public class VoiceRecorder {

    private static final String TAG = "ASR_FLOW";
    private static final int SAMPLE_RATE = 16000;
    private static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int[] AUDIO_SOURCE_CANDIDATES = new int[]{
            MediaRecorder.AudioSource.VOICE_RECOGNITION,
            MediaRecorder.AudioSource.MIC
    };
    private static final int AMPLITUDE_THRESHOLD = 1800;
    private static final int SPEECH_TIMEOUT_MILLIS = 1200;
    private static final int MAX_SPEECH_LENGTH_MILLIS = 12 * 1000;

    private final Callback callback;
    private volatile AudioRecord audioRecord;
    private volatile Thread thread;
    private volatile byte[] chunkBuffer;
    private volatile boolean running;
    private long lastVoiceHeardMillis = Long.MAX_VALUE;
    private long voiceStartedMillis;
    private int audioSource = MediaRecorder.AudioSource.DEFAULT;

    public VoiceRecorder(@NonNull Callback callback) {
        this.callback = callback;
    }

    public void start() {
        stop();
        audioRecord = createAudioRecord();
        if (audioRecord == null) {
            throw new RuntimeException("Cannot instantiate VoiceRecorder");
        }
        running = true;
        audioRecord.startRecording();
        Thread localThread = new Thread(new ProcessVoice(), "grpc-asr-recorder");
        thread = localThread;
        localThread.start();
    }

    public void stop() {
        running = false;
        dismiss();
        Thread localThread = thread;
        thread = null;
        if (localThread != null) {
            localThread.interrupt();
        }

        AudioRecord localRecord = audioRecord;
        audioRecord = null;
        if (localRecord != null) {
            try {
                localRecord.stop();
            } catch (IllegalStateException ignored) {
            }
            localRecord.release();
        }
        chunkBuffer = null;
    }

    public void dismiss() {
        if (lastVoiceHeardMillis != Long.MAX_VALUE) {
            lastVoiceHeardMillis = Long.MAX_VALUE;
            callback.onVoiceEnd();
        }
    }

    public int getSampleRate() {
        AudioRecord localRecord = audioRecord;
        return localRecord != null ? localRecord.getSampleRate() : SAMPLE_RATE;
    }

    public int getAudioSource() {
        return audioSource;
    }

    private AudioRecord createAudioRecord() {
        final int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL, ENCODING);
        if (minBufferSize <= 0) {
            return null;
        }

        final int chunkBytes = SAMPLE_RATE / 10 * 2;
        final int bufferSize = Math.max(minBufferSize, chunkBytes * 2);

        for (int source : AUDIO_SOURCE_CANDIDATES) {
            AudioRecord candidate = new AudioRecord(source, SAMPLE_RATE, CHANNEL, ENCODING, bufferSize);
            if (candidate.getState() == AudioRecord.STATE_INITIALIZED) {
                audioSource = source;
                chunkBuffer = new byte[chunkBytes];
                Log.d(TAG, "gRPC ASR recorder using audio source=" + source
                        + " bufferSize=" + bufferSize
                        + " chunkBytes=" + chunkBytes);
                return candidate;
            }
            candidate.release();
        }

        return null;
    }

    public abstract static class Callback {
        public void onVoiceStart() {
        }

        public void onVoice(byte[] data, int size) {
        }

        public void onVoiceEnd() {
        }
    }

    private class ProcessVoice implements Runnable {

        @Override
        public void run() {
            while (running) {
                AudioRecord localRecord = audioRecord;
                byte[] localBuffer = chunkBuffer;
                if (localRecord == null || localBuffer == null || Thread.currentThread().isInterrupted()) {
                    break;
                }

                int size = localRecord.read(localBuffer, 0, localBuffer.length);
                if (size <= 0 || !running) {
                    continue;
                }

                long now = System.currentTimeMillis();
                callback.onVoice(localBuffer, size);

                if (isHearingVoice(localBuffer, size)) {
                    if (lastVoiceHeardMillis == Long.MAX_VALUE) {
                        voiceStartedMillis = now;
                        callback.onVoiceStart();
                    }
                    lastVoiceHeardMillis = now;
                    if (now - voiceStartedMillis > MAX_SPEECH_LENGTH_MILLIS) {
                        end();
                    }
                } else if (lastVoiceHeardMillis != Long.MAX_VALUE
                        && now - lastVoiceHeardMillis > SPEECH_TIMEOUT_MILLIS) {
                    end();
                }
            }
        }

        private void end() {
            lastVoiceHeardMillis = Long.MAX_VALUE;
            callback.onVoiceEnd();
        }

        private boolean isHearingVoice(byte[] buffer, int size) {
            for (int i = 0; i < size - 1; i += 2) {
                int sample = (buffer[i] & 0xff) | (buffer[i + 1] << 8);
                sample = Math.abs((short) sample);
                if (sample > AMPLITUDE_THRESHOLD) {
                    return true;
                }
            }
            return false;
        }
    }
}
