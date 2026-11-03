package com.sb.woohoo.ui.main.pojo;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class DrawerGridItems implements Serializable {

    private Drawable mDrawable;

    public Drawable getDrawable() {
        return mDrawable;
    }

    public String getAppPackage() {
        return appPackage;
    }

    //this will be null unless this is local app existing on device.
    private String appPackage;
    private int mIcon;
    private String mTitle;
    private Appstore.Data data;

    public DrawerGridItems(Appstore.Data url,int mIcon, String mTitle) {
        this.mIcon = mIcon;
        this.mTitle = mTitle;
        this.data = url;
    }

    public DrawerGridItems(Appstore.Data url, String mTitle, String appPackage, Drawable drawable) {
        this.appPackage = appPackage;
        this.mIcon = -1;
        this.mTitle = mTitle;
        this.data = url;
        this.mDrawable = drawable;
    }

    public DrawerGridItems(DrawerGridItems gridItems) {
        this.mIcon=gridItems.getIcon();
        this.mTitle=gridItems.getTitle();
        this.data=gridItems.getData();
    }


    public int getIcon() {
        return mIcon;
    }
    public Appstore.Data getData() {
        return data;
    }

    public String getTitle() {
        return mTitle;
    }
}
