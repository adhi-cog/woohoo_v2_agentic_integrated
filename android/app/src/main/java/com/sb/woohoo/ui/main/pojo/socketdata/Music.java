package com.sb.woohoo.ui.main.pojo.socketdata;

import com.google.gson.annotations.SerializedName;

public class Music {
    public class MuteToggleData {
        @SerializedName("data")
        private MuteData data;

        public MuteData getData() {
            return data;
        }

        public void setData(MuteData data) {
            this.data = data;
        }

        @SuppressWarnings("unused")
        public class MuteData {
            @SerializedName("id")
            private String id;
            @SerializedName("hub_id")
            private String hubID;
            @SerializedName("volume")
            private boolean volume;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getHubID() {
                return hubID;
            }

            public void setHubID(String hubID) {
                this.hubID = hubID;
            }

            public boolean isVolume() {
                return volume;
            }

            public void setVolume(boolean volume) {
                this.volume = volume;
            }
        }
    }

    public class PlayPauseData {
        @SerializedName("data")
        private PlayStateData data;

        public PlayStateData getData() {
            return data;
        }

        public void setData(PlayStateData data) {
            this.data = data;
        }

        @SuppressWarnings("unused")
        public class PlayStateData {
            @SerializedName("id")
            private String id;
            @SerializedName("hub_id")
            private String hubID;
            @SerializedName("isPlaying")
            private boolean isPlaying;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getHubID() {
                return hubID;
            }

            public void setHubID(String hubID) {
                this.hubID = hubID;
            }

            public boolean isPlaying() {
                return isPlaying;
            }

            public void setPlaying(boolean playing) {
                isPlaying = playing;
            }
        }
    }

    public class ChangeTrackData {
        @SerializedName("data")
        private TrackChangeData data;

        public TrackChangeData getData() {
            return data;
        }

        public void setData(TrackChangeData data) {
            this.data = data;
        }

        @SuppressWarnings("unused")
        public class TrackChangeData {
            @SerializedName("id")
            private String id;
            @SerializedName("hub_id")
            private String hubID;
            @SerializedName("changeTo")
            private String changeTo;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getHubID() {
                return hubID;
            }

            public void setHubID(String hubID) {
                this.hubID = hubID;
            }

            public String getChangeTo() {
                return changeTo;
            }

            public void setChangeTo(String changeTo) {
                this.changeTo = changeTo;
            }
        }
    }
}