package com.sb.woohoo.ui.main.fragments.contacts.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sb.woohoo.ui.main.pojo.nlp.BaseModel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class ContactsData extends BaseModel<ContactsData> implements Parcelable, Serializable {
    public static final Creator<ContactsData> CREATOR = new Creator<ContactsData>() {
        @Override
        public ContactsData createFromParcel(Parcel in) {
            return new ContactsData(in);
        }

        @Override
        public ContactsData[] newArray(int size) {
            return new ContactsData[size];
        }
    };
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("sourceId")
    @Expose
    private String sourceId;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("isWoohooUser")
    @Expose
    private Boolean isWoohooUser;
    @SerializedName("phoneNos")
    @Expose
    private List<String> phoneNos = null;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("deletedAt")
    @Expose
    private Object deletedAt;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("__v")
    @Expose
    private Integer v;
    @SerializedName("liveFeedAccess")
    @Expose
    private Boolean isLiveFeedAccess;

    public ContactsData() {
    }

    protected ContactsData(Parcel in) {
        name = in.readString();
        sourceId = in.readString();
        email = in.readString();
        byte tmpIsWoohooUser = in.readByte();
        isWoohooUser = tmpIsWoohooUser == 0 ? null : tmpIsWoohooUser == 1;
        phoneNos = in.createStringArrayList();
        imageUrl = in.readString();
        id = in.readString();
        userId = in.readString();
        createdAt = in.readString();
        if (in.readByte() == 0) {
            v = null;
        } else {
            v = in.readInt();
        }
        byte tmpIsLiveFeedAccess = in.readByte();
        isLiveFeedAccess = tmpIsLiveFeedAccess == 0 ? null : tmpIsLiveFeedAccess == 1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getIsWoohooUser() {
        return isWoohooUser;
    }

    public void setIsWoohooUser(Boolean isWoohooUser) {
        this.isWoohooUser = isWoohooUser;
    }

    public List<String> getPhoneNos() {
        return phoneNos;
    }

    public void setPhoneNos(List<String> phoneNos) {
        this.phoneNos = phoneNos;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
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

        dest.writeString(name);
        dest.writeString(sourceId);
        dest.writeString(email);
        dest.writeByte((byte) (isWoohooUser == null ? 0 : isWoohooUser ? 1 : 2));
        dest.writeStringList(phoneNos);
        dest.writeString(imageUrl);
        dest.writeString(id);
        dest.writeString(userId);
        dest.writeString(createdAt);
        if (v == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(v);
        }
        dest.writeByte((byte) (isLiveFeedAccess == null ? 0 : isLiveFeedAccess ? 1 : 2));
    }

    public Boolean getLiveFeedAccess() {
        return isLiveFeedAccess;
    }

    public void setLiveFeedAccess(Boolean liveFeedAccess) {
        isLiveFeedAccess = liveFeedAccess;
    }

    @Override
    public void parseModel(HashMap<String, String> hashMap) {

    }
}
