package com.sb.woohoo.ui.main.fragments.livefeedaudio;

public interface LiveFeedAudioContract {

    interface View {

        void onUpdateCountDownTimer(String countDown);

        void onConnectingToStream();

        void onConnectedToRoom();
    }

    interface Action {

        void endCall();

        void enableAudio(boolean enable);


        void onConnectCall();

        void enableSpeaker(boolean isEnabled);

        void sendAcceptActionObj(String senderClientId, String senderHubId, String receiverHubId, String token,
                                 boolean contactLiveFeed, String contactUserId);

    }
}
