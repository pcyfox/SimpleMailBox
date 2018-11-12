package com.simple.mail.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

public class Contact extends Person implements Parcelable {
    private static final String TAG = "Contact";
    @SerializedName("head_image")
    private String headImage;
    @SerializedName("user_id")
    private String userId;
    private boolean isMe;//是否为当前登录人

    public Contact() {

    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }


    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean me) {
        isMe = me;
    }


    @Override
    public String toString() {
        return "Contact{" +
                ", headImage='" + headImage + '\'' +
                ", isMe=" + isMe +
                ", id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", email_account='" + email_account + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }

    @Override
    public int hashCode() {
        if (userId == null) {
            return super.hashCode();
        }
        return userId.hashCode() * 3;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Contact) {
            Contact contact = (Contact) o;
            int otherHashCode = contact.hashCode();
            int thisHashCode = hashCode();
            Log.d(TAG, "equals otherHashCode: " + otherHashCode);
            Log.d(TAG, "equals thisHashCode: " + thisHashCode);
            return thisHashCode == otherHashCode;
        } else {
            return super.equals(o);
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.headImage);
        dest.writeString(this.userId);
        dest.writeByte(this.isMe ? (byte) 1 : (byte) 0);
    }

    private Contact(Parcel in) {
        super(in);
        this.headImage = in.readString();
        this.userId = in.readString();
        this.isMe = in.readByte() != 0;
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}
