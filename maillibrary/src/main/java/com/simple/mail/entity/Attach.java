package com.simple.mail.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.InputStream;

import javax.mail.BodyPart;

public class Attach implements Parcelable {

    public String attach_name;
    public String file_path;
    public String attach_size;
    public String email_id;//所在的邮件的emailid
    public int is_download=0;//邮件是否下载完成过,0 代表没有下载过
    public String attach_cid;
    public InputStream in;
    public BodyPart mpart;
    public boolean isLoading=false;//判断是否正在下载

    public Attach() {

    }

    public Attach(String attach_name, String file_path, String attach_size) {
        this.attach_name = attach_name;
        this.file_path = file_path;
        this.attach_size = attach_size;
    }

    public Attach(String attach_name, String file_path, InputStream in, BodyPart mpart, String attach_size) {
        this.attach_name = attach_name;
        this.file_path = file_path;
        this.attach_size = attach_size;
        this.mpart = mpart;
        this.in = in;
    }

    @Override
    public String toString() {
        return "Attach{" +
                "attach_name='" + attach_name + '\'' +
                ", file_path='" + file_path + '\'' +
                ", attach_size='" + attach_size + '\'' +
                ", is_download='" + is_download + '\'' +
                ", isLoading='" + isLoading + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.attach_name);
        dest.writeString(this.file_path);
        dest.writeString(this.attach_size);
    }

    protected Attach(Parcel in) {
        this.attach_name = in.readString();
        this.file_path = in.readString();
        this.attach_size = in.readString();
    }

    public static final Parcelable.Creator<Attach> CREATOR = new Parcelable.Creator<Attach>() {
        @Override
        public Attach createFromParcel(Parcel source) {
            return new Attach(source);
        }

        @Override
        public Attach[] newArray(int size) {
            return new Attach[size];
        }
    };
}
