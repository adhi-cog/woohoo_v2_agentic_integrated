package com.sb.woohoo.ui.main.fragments.contactsprofile.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.Hub;

import java.util.List;


/**
 * POJO for showing data of a Contact.
 */
public class ContactInfo implements Parcelable {
    public static final Creator<ContactInfo> CREATOR = new Creator<ContactInfo>() {
        @Override
        public ContactInfo createFromParcel(Parcel in) {
            return new ContactInfo(in);
        }

        @Override
        public ContactInfo[] newArray(int size) {
            return new ContactInfo[size];
        }
    };
    @SerializedName("_id")
    private String id;
    @SerializedName("user_id")
    private String user_id;
    @SerializedName("email")
    private String email;
    @SerializedName("name")
    private String name;
    @SerializedName("imageUrl")
    private String imageURL;
    @SerializedName("phone_nos")
    private List<String> phoneNumbers;
    @SerializedName("liveFeedAccess")
    @Expose
    private Boolean isLiveFeedAccess;
    private int drawableSource;
    private Hub hub;
    private String phoneNumber;

    protected ContactInfo(Parcel in) {
        id = in.readString();
        user_id = in.readString();
        email = in.readString();
        name = in.readString();
        imageURL = in.readString();
        phoneNumbers = in.createStringArrayList();
        byte tmpIsLiveFeedAccess = in.readByte();
        isLiveFeedAccess = tmpIsLiveFeedAccess == 0 ? null : tmpIsLiveFeedAccess == 1;
        drawableSource = in.readInt();
        hub = in.readParcelable(Hub.class.getClassLoader());
    }

    public ContactInfo() {
    }

    public Hub getHub() {
        return hub;
    }

    public void setHub(Hub hub) {
        this.hub = hub;
    }

    public int getDrawableSource() {
        return drawableSource;
    }

    public void setDrawableSource(int drawableSource) {
        this.drawableSource = drawableSource;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public Boolean getLiveFeedAccess() {
        return isLiveFeedAccess;
    }

    public void setLiveFeedAccess(Boolean liveFeedAccess) {
        isLiveFeedAccess = liveFeedAccess;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(user_id);
        dest.writeString(email);
        dest.writeString(name);
        dest.writeString(imageURL);
        dest.writeStringList(phoneNumbers);
        dest.writeString(phoneNumber);
        dest.writeByte((byte) (isLiveFeedAccess == null ? 0 : isLiveFeedAccess ? 1 : 2));
        dest.writeInt(drawableSource);
        dest.writeParcelable(hub, flags);
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}