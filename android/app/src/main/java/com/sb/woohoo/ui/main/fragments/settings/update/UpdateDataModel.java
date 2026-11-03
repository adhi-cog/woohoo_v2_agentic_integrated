package com.sb.woohoo.ui.main.fragments.settings.update;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class UpdateDataModel implements Parcelable {

    public static final Parcelable.Creator<UpdateDataModel> CREATOR = new Parcelable.Creator<UpdateDataModel>() {
        @Override
        public UpdateDataModel createFromParcel(Parcel source) {
            return new UpdateDataModel(source);
        }

        @Override
        public UpdateDataModel[] newArray(int size) {
            return new UpdateDataModel[size];
        }
    };
    @SerializedName("isCritical")
    @Expose
    private Boolean isCritical;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("versionCode")
    @Expose
    private Integer versionCode;
    @SerializedName("versionName")
    @Expose
    private String versionName;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("__v")
    @Expose
    private Integer v;

    public UpdateDataModel() {
    }

    protected UpdateDataModel(Parcel in) {
        this.isCritical = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.created = in.readString();
        this.id = in.readString();
        this.name = in.readString();
        this.versionCode = (Integer) in.readValue(Integer.class.getClassLoader());
        this.versionName = in.readString();
        this.url = in.readString();
        this.description = in.readString();
        this.v = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public Boolean getIsCritical() {
        return isCritical;
    }

    public void setIsCritical(Boolean isCritical) {
        this.isCritical = isCritical;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(Integer versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.isCritical);
        dest.writeString(this.created);
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeValue(this.versionCode);
        dest.writeString(this.versionName);
        dest.writeString(this.url);
        dest.writeString(this.description);
        dest.writeValue(this.v);
    }
}