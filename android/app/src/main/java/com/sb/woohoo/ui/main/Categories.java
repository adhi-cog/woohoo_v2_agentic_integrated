package com.sb.woohoo.ui.main;

public class Categories {
    public static final String COMMAND_CATEGORY_DEVICE = "hub.devices";
    public static final String COMMAND_CATEGORY_SKILLS = "hub.skills";
    public static final String COMMAND_CATEGORY_COMMUNICATION = "hub.communication";
    public static final String COMMAND_CATEGORY_SECURITY = "hub.security";
    public static final String COMMAND_CATEGORY_CONTROLS = "hub.controls";

    public class Skills {
        public static final String COMMAND_SKILL_REMINDER = "hub.skills.reminders";
        public static final String COMMAND_SKILL_ALARM = "hub.skills.alarms";
        public static final String COMMAND_SKILL_TODO = "hub.skills.todos";
        public static final String COMMAND_SKILL_TIMER = "hub.skills.timer";
        public static final String COMMAND_SKILL_QNA = "hub.skills.qna";
        public static final String COMMAND_SKILL_MUSIC = "hub.skills.music";
        public static final String COMMAND_SKILL_RELAXATION_MUSIC = "hub.skills.relaxation_music";
        public static final String COMMAND_SKILL_BUY_SIM = "hub.skills.buy_sim";
        public static final String COMMAND_SKILL_VIDEO = "hub.skills.videos";
        public static final String COMMAND_SKILL_NEWS = "hub.skills.news";
        public static final String COMMAND_SKILL_WEATHER = "hub.skills.weather";
        public static final String COMMAND_SKILL_CAMERA = "hub.skills.camera";
        public static final String COMMAND_SKILL_TRANSLATE = "hub.skills.translate";

        public class Translate{
            public static final String  COMMAND_ACTION_TRANSLATE = "hub.skills.translate";

            public static final int TRANSLATE_LANG = 102;
        }
        public class FnB{
            public static final String COMMAND_ACTION_FNB = "FnB.Show";
        }
        public class Restaurants {
            public static final String COMMAND_ACTION_BOOK_TABLE = "hub.skills.restaurants.book_table";
            public static final String COMMAND_ACTION_SHOW = "hub.skills.restaurants.show";
        }
        public class Reminder {
            public static final String COMMAND_ACTION_SHOW = "hub.skills.reminders.show";
            public static final String COMMAND_ACTION_ADD = "hub.skills.reminders.add";
            public static final String COMMAND_ACTION_DELETE_TITLE = "hub.skills.reminders.delete.title";
            public static final String COMMAND_ACTION_DELETE_ALL = "hub.skills.reminders.delete.all";
            public static final String COMMAND_ACTION_EDIT = "hub.skills.reminders.edit";
            public static final String COMMAND_ACTION_DELETE_TITLE_ALTERNATIVE = "hub.skills.reminders.delete.title.alternative";
            public static final String COMMAND_ACTION_EDIT_ALTERNATIVE = "hub.skills.reminders.edit.alternative";

            public static final int REMINDER_SHOW = 102;
            public static final int REMINDER_ADD = 103;
            public static final int REMINDER_DELETE_TITLE = 104;
            public static final int REMINDER_DELETE_ALL = 105;
            public static final int REMINDER_EDIT = 106;
            public static final int REMINDER_DELETE_TITLE_ALTERNATIVE = 107;
            public static final int REMINDER_EDIT_ALTERNATIVE = 108;
        }

        public class Alarm {
            public static final String COMMAND_ACTION_SHOW = "hub.skills.alarms.show";
            public static final String COMMAND_ACTION_ADD = "hub.skills.alarms.add";
            public static final String COMMAND_ACTION_DELETE_ORDINAL = "hub.skills.alarms.delete.ordinal";
            public static final String COMMAND_ACTION_DELETE_ALL = "hub.skills.alarms.delete.all";
            public static final String COMMAND_ACTION_SNOOZE = "hub.skills.alarms.snooze";

            public static final int ALARM_SHOW = 102;
            public static final int ALARM_ADD = 103;
            public static final int ALARM_DELETE_ORDINAL = 104;
            public static final int ALARM_DELETE_ALL = 105;
            public static final int ALARM_SNOOZE = 106;
        }

        public class ToDo {
            public static final String COMMAND_ACTION_SHOW = "hub.skills.todos.show";
            public static final String COMMAND_ACTION_ADD = "hub.skills.todos.add";
            public static final String COMMAND_ACTION_DELETE_ORDINAL = "hub.skills.todos.delete.ordinal";
            public static final String COMMAND_ACTION_DELETE_ALL = "hub.skills.todos.delete.all";

            public static final int TODO_SHOW = 102;
            public static final int TODO_ADD = 103;
            public static final int TODO_DELETE_ORDINAL = 104;
            public static final int TODO_DELETE_ALL = 105;
        }

        public class Timer {
            public static final String COMMAND_ACTION_SHOW = "hub.skills.timer.show";
            public static final String COMMAND_ACTION_SET = "hub.skills.timer.set";
            public static final String COMMAND_ACTION_STOP = "hub.skills.timer.stop";

            public static final int TIMER_SHOW = 102;
            public static final int TIMER_SET = 103;
            public static final int TIMER_STOP = 104;
        }

        public class QnA {
            public static final String COMMAND_ACTION_QUERY = "hub.skills.qna.fallback";
            public static final String COMMAND_ACTION_STOP = "hub.skills.qna.stop";
            public static final String COMMAND_ACTION_CURRENT_TIME = "hub.skills.qna.currenttime";
            public static final String COMMAND_ACTION_CURRENT_DATE = "hub.skills.qna.currentdate";

            public static final int QNA_QUERY = 102;
            public static final int QNA_STOP = 103;
            public static final int QNA_CURRENT_TIME = 105;
            public static final int QNA_CURRENT_DATE = 106;
        }

        public class Music {
            public static final String COMMAND_ACTION_SEARCH = "hub.skills.music.search";
            public static final String COMMAND_ACTION_PLAY = "hub.skills.music.play";
            public static final String COMMAND_ACTION_CONTROL_NEXT = "hub.skills.music.controls.next";
            public static final String COMMAND_ACTION_CONTROL_PREV = "hub.skills.music.controls.previous";
            public static final String COMMAND_ACTION_CONTROL_PAUSE = "hub.skills.music.controls.pause";
            public static final String COMMAND_ACTION_CONTROL_REPEAT = "hub.skills.music.controls.repeat";
            public static final String COMMAND_ACTION_CONTROL_STOP = "hub.skills.music.controls.stop";
            public static final String COMMAND_ACTION_CONTROL_PLAY = "hub.skills.music.controls.play";
            public static final String COMMAND_ACTION_CONTROL_PLAYLIST = "hub.skills.music.playlist";

            public static final int MUSIC_SEARCH = 102;
            public static final int MUSIC_PLAY = 103;
            public static final int MUSIC_CONTROL_NEXT = 104;
            public static final int MUSIC_CONTROL_PREV = 105;
            public static final int MUSIC_CONTROL_PAUSE = 106;
            public static final int MUSIC_CONTROL_REPEAT = 107;
            public static final int MUSIC_CONTROL_STOP = 108;
            public static final int MUSIC_CONTROL_PLAY = 109;
            public static final int MUSIC_CONTROL_PLAYLIST = 110;
        }

        public class RelaxationMusic {
            public static final String COMMAND_ACTION_SHOW = "hub.skills.relaxation_music.show";
            public static final String COMMAND_ACTION_PLAY = "hub.skills.relaxation_music.play";
            public static final String COMMAND_ACTION_NEXT = "hub.skills.relaxation_music.next";
            public static final String COMMAND_ACTION_PREVIOUS = "hub.skills.relaxation_music.previous";
            public static final String COMMAND_ACTION_PAUSE = "hub.skills.relaxation_music.pause";
            public static final String COMMAND_ACTION_RESUME = "hub.skills.relaxation_music.resume";
        }

        public class BuySim {
            public static final String COMMAND_ACTION_SHOW = "hub.skills.buy_sim.show";
            public static final String COMMAND_ACTION_SELECT_PACKAGE = "hub.skills.buy_sim.select_package";
        }

        public class Video {
            public static final String COMMAND_ACTION_VIDEO_SEARCH = "hub.skills.videos.search";
            public static final String COMMAND_ACTION_VIDEO_AUTOPLAY = "hub.skills.videos.search.autoplay";
            public static final String COMMAND_ACTION_VIDEO_PLAY_ORDINAL = "hub.skills.videos.play.ordinal";
            public static final String COMMAND_ACTION_VIDEO_PLAY_ORDINAL_ALTERNATIVE = "hub.skills.videos.play.ordinal.alternative";
            public static final String COMMAND_ACTION_VIDEO_LAUNCH = "hub.skills.videos.launch";
            public static final String COMMAND_ACTION_VIDEO_LAUNCH_AUTOPLAY = "hub.skills.videos.launch.autoplay";
            public static final String COMMAND_ACTION_VIDEO_CONTROLS_PLAY = "hub.skills.videos.controls.play";
            public static final String COMMAND_ACTION_VIDEO_CONTROLS_PAUSE = "hub.skills.videos.controls.pause";
            //            public static final String COMMAND_ACTION_VIDEO_CONTROLS_PREVIOUS = "hub.skills.videos.controls.previous";
//            public static final String COMMAND_ACTION_VIDEO_CONTROLS_NEXT = "hub.skills.videos.controls.next";
            public static final String COMMAND_ACTION_VIDEO_CONTROLS_BACK = "hub.skills.videos.controls.back";
            public static final String COMMAND_ACTION_VIDEO_CONTROLS_BACK_ALTERNATIVE = "hub.skills.videos.controls.back.alternative";

            public static final int VIDEO_SEARCH = 102;
            public static final int VIDEO_AUTOPLAY = 103;
            public static final int VIDEO_PLAY_ORDINAL = 104;
            public static final int VIDEO_PLAY_ORDINAL_ALTERNATIVE = 112;
            public static final int VIDEO_LAUNCH = 105;
            public static final int VIDEO_LAUNCH_AUTOPLAY = 106;
            public static final int VIDEO_CONTROLS_PLAY = 107;
            public static final int VIDEO_CONTROLS_PAUSE = 108;
            //            public static final int VIDEO_CONTROLS_NEXT = 109;
//            public static final int VIDEO_CONTROLS_NEXT_ALTERNATIVE = 114;
//            public static final int VIDEO_CONTROLS_PREVIOUS = 110;
//            public static final int VIDEO_CONTROLS_PREVIOUS_ALTERNATIVE = 113;
            public static final int VIDEO_CONTROLS_BACK = 111;
            public static final int VIDEO_CONTROLS_BACK_ALTERNATIVE = 115;
        }

        public class News {
            public static final String COMMAND_ACTION_CONTROL_PAUSE = "hub.skills.news.player.pause";
            public static final String COMMAND_ACTION_CONTROL_STOP = "hub.skills.news.player.stop";
            public static final String COMMAND_ACTION_CONTROL_PLAY = "hub.skills.news.player.play";
            public static final String COMMAND_ACTION_SEARCH = "hub.skills.news.search";
            public static final String COMMAND_ACTION_LOCATION = "hub.skills.news.location";
            public static final String COMMAND_ACTION_LAUNCH = "hub.skills.news.launch";
            public static final String COMMAND_ACTION_CONTROL_PREVIOUS = "hub.skills.news.player.previous";
            public static final String COMMAND_ACTION_CONTROL_NEXT = "hub.skills.news.player.next";

            public static final int NEWS_CONTROL_PAUSE = 102;
            public static final int NEWS_CONTROL_STOP = 103;
            public static final int NEWS_CONTROL_PLAY = 104;
            public static final int NEWS_CONTROL_SEARCH = 105;
            public static final int NEWS_CONTROL_LOCATION = 106;
            public static final int NEWS_CONTROL_LAUNCH = 107;
            public static final int NEWS_CONTROL_PREVIOUS = 108;
            public static final int NEWS_CONTROL_NEXT = 109;

        }

        public class Weather {
            public static final String COMMAND_ACTION_WEATHER_OUTFIT = "hub.skills.weather.outfit";
            public static final String COMMAND_ACTION_WEATHER_SEARCH = "hub.skills.weather.search";
            public static final String COMMAND_ACTION_WEATHER_CONDITION = "hub.skills.weather.condition";
            public static final String COMMAND_ACTION_WEATHER_SHOW = "hub.skills.weather.show";

            public static final int WEATHER_OUTFIT = 102;
            public static final int WEATHER_SEARCH = 103;
            public static final int WEATHER_CONDITION = 104;
            public static final int WEATHER_SHOW = 105;
        }

        public class Camera {
            public static final String COMMAND_ACTION_SELFIE = "hub.skills.camera.selfie";

            public static final int CAMERA_SELFIE = 102;
        }

    }

    public class Controls {
        public static final String COMMAND_CONTROLS_NAVIGATION = "hub.controls.navigation";
        public static final String COMMAND_CONTROLS_PLAYER = "hub.controls.player";
        public static final String COMMAND_CONTROLS_SYSTEM = "hub.controls.system";
        public static final String COMMAND_CONTROLS_ALERT = "hub.controls.RemoteAlertModel";

        public class Navigation {
            public static final String COMMAND_NAVIGATION_HOME = "hub.controls.navigation.home";

            public static final int NAVIGATION_HOME = 101;
        }

        public class Player {
            public static final String COMMAND_PLAYER_PAUSE = "hub.controls.player.pause";
            public static final String COMMAND_PLAYER_PLAY = "hub.controls.player.play";
            public static final String COMMAND_PLAYER_RESUME = "hub.controls.player.resume";
            public static final String COMMAND_PLAYER_PREVIOUS = "hub.controls.player.previous";
            public static final String COMMAND_PLAYER_NEXT = "hub.controls.player.next";
            public static final String COMMAND_PLAYER_STOP = "hub.controls.player.stop";

            public static final int PLAYER_PAUSE = 202;
            public static final int PLAYER_PLAY = 203;
            public static final int PLAYER_RESUME = 207;
            public static final int PLAYER_PREVIOUS = 204;
            public static final int PLAYER_NEXT = 205;
            public static final int PLAYER_STOP = 206;
        }

        public class Volume {
            public static final String COMMAND_VOLUME_UP = "hub.controls.system.volume.up";
            public static final String COMMAND_VOLUME_DOWN = "hub.controls.system.volume.down";
            public static final String COMMAND_VOLUME_SET = "hub.controls.system.volume.set";
            public static final String COMMAND_VOLUME_MUTE = "hub.controls.system.volume.mute";
            public static final String COMMAND_VOLUME_UNMUTE = "hub.controls.system.volume.unmute";

            public static final int VOLUME_UP = 102;
            public static final int VOLUME_DOWN = 103;
            public static final int VOLUME_SET = 104;
            public static final int VOLUME_MUTE = 105;
            public static final int VOLUME_UNMUTE = 106;
        }

        public class SystemSetting {
            public static final String COMMAND_NIGHT_MODE_ON = "hub.controls.system.night_mode.on";
            public static final String COMMAND_NIGHT_MODE_OFF = "hub.controls.system.night_mode.off";
            public static final String COMMAND_BLUETOOTH_ON = "hub.controls.system.bluetooth.on";
            public static final String COMMAND_BLUETOOTH_OFF = "hub.controls.system.bluetooth.off";
            public static final String COMMAND_BLUETOOTH_OPEN = "hub.controls.system.bluetooth.open";
            public static final String COMMAND_BRIGHTNESS_UP = "hub.controls.system.brightness.up";
            public static final String COMMAND_BRIGHTNESS_DOWN = "hub.controls.system.brightness.down";
            public static final String COMMAND_BRIGHTNESS_SET = "hub.controls.system.brightness.set";

            public static final int NIGHT_MODE_ON = 107;
            public static final int NIGHT_MODE_OFF = 108;
            public static final int BLUETOOTH_ON = 109;
            public static final int BLUETOOTH_OFF = 110;
            public static final int BLUETOOTH_OPEN = 111;
            public static final int BRIGHTNESS_UP = 112;
            public static final int BRIGHTNESS_DOWN = 113;
            public static final int BRIGHTNESS_SET = 114;
        }

        public class Alert {
            public static final String COMMAND_ALERT_DISMISS = "hub.controls.RemoteAlertModel.dismiss";

            public static final int ALERT_DISMISS = 102;
        }
    }

    public class Communication {
        public static final String COMMAND_COMMUNICATION_CONTACT = "hub.communication.contacts";
        public static final String COMMAND_COMMUNICATION_CALLS = "hub.communication.calls";

        public class Contacts {
            public static final String COMMAND_COMMUNICATION_SHOW = "hub.communication.contacts.show";
            public static final int CONTACT_SHOW = 102;
        }

        public class VoiceCall {

            public static final String COMMAND_VOICE_CALL = "hub.communication.calls.phone.contact";

            public static final int VOICE_CALL = 103;

        }
    }



    public class Devices {
        public static final String COMMAND_DEVICES_LIGHTS = "hub.devices.lights";

        public class Light {
            public static final String COMMAND_ACTION_SHOW = "hub.devices.lights.show";
            public static final String COMMAND_ACTION_LIGHTS_SEARCH = "hub.devices.lights.search";
            public static final String COMMAND_ACTION_LIGHTS_REGISTER = "hub.devices.lights.register";

            public static final String COMMAND_ACTION_LIGHTS_ALL_TOGGLE_OFF = "hub.devices.lights.all.toggle.off";
            public static final String COMMAND_ACTION_LIGHTS_ALL_TOGGLE_ON = "hub.devices.lights.all.toggle.on";
            public static final String COMMAND_ACTION_LIGHTS_ALL_BRIGHTNESS = "hub.devices.lights.all.brightness";
            public static final String COMMAND_ACTION_LIGHTS_ALL_HUE = "hub.devices.lights.all.hue";
            public static final String COMMAND_ACTION_LIGHTS_REGISTER_AUTO = "hub.devices.lights.register.auto";
            public static final String COMMAND_ACTION_LIGHTS_SCENE = "hub.devices.lights.scene";
            public static final String COMMAND_ACTION_LIGHTS_INC_BRIGHTNESS = "hub.devices.lights.brightness.up";
            public static final String COMMAND_ACTION_LIGHTS_DEC_BRIGHTNESS = "hub.devices.lights.brightness.down";


            public static final int LIGHTS_SHOW = 101;
            public static final int LIGHTS_SEARCH = 102;
            public static final int LIGHTS_REGISTER = 103;

            public static final int LIGHTS_ALL_TOGGLE_OFF = 104;
            public static final int LIGHTS_ALL_TOGGLE_ON = 105;
            public static final int LIGHTS_ALL_BRIGHTNESS = 106;
            public static final int LIGHTS_ALL_HUE = 107;
            public static final int LIGHTS_REGISTER_AUTO = 108;
            public static final int LIGHTS_SCENE = 109;
            public static final int LIGHTS_INC_BRIGHTNESS = 110;
            public static final int LIGHTS_DEC_BRIGHTNESS = 111;
        }
    }
}
