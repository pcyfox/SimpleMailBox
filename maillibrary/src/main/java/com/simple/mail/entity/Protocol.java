package com.simple.mail.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Protocol 通信协议
 */
public class Protocol implements Parcelable {
    public int id;
    public String protocl;
    public String server;
    public String port;
    public boolean ssl;

    public Protocol() {
        super();
    }

    public Protocol(String protocl, String server, String port, boolean ssl) {
        this.protocl = protocl;
        this.server = server;
        this.port = port;
        this.ssl = ssl;
    }

    public Protocol(int id, String protocl, String server, String port, boolean ssl) {
        this(protocl, server, port, ssl);
        this.id = id;
    }


    public String getProtocl() {
        return protocl;
    }

    public void setProtocl(String protocl) {
        this.protocl = protocl;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.protocl);
        dest.writeString(this.server);
        dest.writeString(this.port);
        dest.writeByte(this.ssl ? (byte) 1 : (byte) 0);
    }

    protected Protocol(Parcel in) {
        this.protocl = in.readString();
        this.server = in.readString();
        this.port = in.readString();
        this.ssl = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Protocol> CREATOR = new Parcelable.Creator<Protocol>() {
        @Override
        public Protocol createFromParcel(Parcel source) {
            return new Protocol(source);
        }

        @Override
        public Protocol[] newArray(int size) {
            return new Protocol[size];
        }
    };

    @Override
    public String toString() {
        return "Protocol{" +
                "protocl='" + protocl + '\'' +
                ", server='" + server + '\'' +
                ", port='" + port + '\'' +
                ", ssl=" + ssl +
                '}';
    }
}
