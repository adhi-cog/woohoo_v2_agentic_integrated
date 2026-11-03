package com.sb.woohoo.ui.main.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class DrawerGridItemModel implements Parcelable {
    public static final Creator<DrawerGridItemModel> CREATOR = new Creator<DrawerGridItemModel>() {
        @Override
        public DrawerGridItemModel createFromParcel(Parcel in) {
            return new DrawerGridItemModel(in);
        }

        @Override
        public DrawerGridItemModel[] newArray(int size) {
            return new DrawerGridItemModel[size];
        }
    };
    private List<DrawerGridItems> drawerGridItemsList;

    public DrawerGridItemModel(List<DrawerGridItems> drawerGridItemsList) {
        this.drawerGridItemsList = drawerGridItemsList;
    }

    protected DrawerGridItemModel(Parcel in) {
    }

    public List<DrawerGridItems> getDrawerGridItemsList() {
        return drawerGridItemsList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
