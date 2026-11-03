package com.sb.woohoo.ui.main.fragments.notification;

import android.os.Parcel;
import android.os.Parcelable;


public class Notification implements Parcelable {
    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };
    private String notification;
    private String notificationTitle;
    private String notificationContent;

    public Notification(String notification, String notificationTitle, String notificationContent) {
        this.notification = notification;
        this.notificationTitle = notificationTitle;
        this.notificationContent = notificationContent;
    }

    protected Notification(Parcel in) {
        notification = in.readString();
        notificationTitle = in.readString();
        notificationContent = in.readString();
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationContent() {
        return notificationContent;
    }

    public void setNotificationContent(String notificationContent) {
        this.notificationContent = notificationContent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(notification);
        parcel.writeString(notificationTitle);
        parcel.writeString(notificationContent);
    }
}
