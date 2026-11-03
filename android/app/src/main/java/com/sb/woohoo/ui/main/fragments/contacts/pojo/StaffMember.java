package com.sb.woohoo.ui.main.fragments.contacts.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.Hub;
import com.sb.woohoo.ui.main.fragments.surveillancemode.pojo.UserData;
import com.sb.woohoo.ui.main.pojo.nlp.BaseModel;

import java.io.Serializable;
import java.util.HashMap;

public class StaffMember extends BaseModel<ContactsData>  implements Parcelable, Serializable {
    public static final Creator<StaffMember> CREATOR = new Creator<StaffMember>() {
        @Override
        public StaffMember createFromParcel(Parcel in) {
            return new StaffMember(in);
        }

        @Override
        public StaffMember[] newArray(int size) {
            return new StaffMember[size];
        }
    };
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("deletedAt")
    @Expose
    private Object deletedAt;
    @SerializedName("hubId")
    @Expose
    private String hubId;
    @SerializedName("propertyId")
    @Expose
    private Integer propertyId;
    @SerializedName("userId")
    @Expose
    private Integer userId;
    @SerializedName("hub")
    @Expose
    private Hub hub;
    @SerializedName("user")
    @Expose
    private UserData user;
    private int drawableSource;

    public StaffMember(String name, int drawableSource) {
        this.name = name;
        this.drawableSource = drawableSource;
    }

    protected StaffMember(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        name = in.readString();
        email = in.readString();
        phoneNumber = in.readString();
        imageUrl = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        hubId = in.readString();
        if (in.readByte() == 0) {
            propertyId = null;
        } else {
            propertyId = in.readInt();
        }
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
        hub = in.readParcelable(Hub.class.getClassLoader());
    }

    public int getDrawableSource() {
        return drawableSource;
    }

    public Integer getId() {
        return id==null?0:id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getHubId() {
        return hubId;
    }

    public void setHubId(String hubId) {
        this.hubId = hubId;
    }

    public Integer getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Integer propertyId) {
        this.propertyId = propertyId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Hub getHub() {
        return hub;
    }

    public void setHub(Hub hub) {
        this.hub = hub;
    }

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(phoneNumber);
        dest.writeString(imageUrl);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeString(hubId);
        if (propertyId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(propertyId);
        }
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userId);
        }
        dest.writeParcelable(hub, flags);
    }

    @Override
    public void parseModel(HashMap<String, String> hashMap) {

    }
}