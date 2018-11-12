package com.simple.mail.entity;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;

/**
 * 收件人的各种信息
 */
public class Addresser implements Parcelable {
    @Expose
    public String email_account;
    @Expose
    public String email_name;
    @Expose
    public String email_password;
    @Expose
    public String first_mail_uid;
    @Expose
    public String id;
    @Expose
    public String is_receive_ssl;
    @Expose
    public String is_send_ssl;
    @Expose
    public String last_mail_uid;
    @Expose
    public String receive_port;
    @Expose
    public String receive_protocol;
    @Expose
    public String receive_server;
    @Expose
    public String send_port;
    @Expose
    public String send_protocol;
    @Expose
    public String send_server;
    @Expose
    public String signature;
    @Expose
    public String signature_id;
    @Expose
    public String user_id;

    public boolean isCompanyMail;

    public Protocol sendProtocol;
    public Protocol receiveProtocol;

    public Addresser(String id,String signature) {
        this.user_id = id;
        this.first_mail_uid = "";
        this.last_mail_uid = "";
        this.signature = signature;
    }

    public void setSendProtocol(Protocol protocol) {
        this.send_port = protocol.port;
        this.send_protocol = protocol.protocl;
        this.send_server = protocol.server;
        this.is_send_ssl = Integer.toString(protocol.isSsl() ? 1 : 0);
        this.sendProtocol = protocol;
    }

    public void setReceiveProtocol(Protocol protocol) {
        this.receive_port = protocol.port;
        this.receive_protocol = protocol.protocl;
        this.receive_server = protocol.server;
        this.is_receive_ssl = Integer.toString(protocol.isSsl() ? 1 : 0);
        this.receiveProtocol = protocol;
    }

    public void setSendProtocol() {
        this.sendProtocol = new Protocol(send_protocol, send_server, send_port, Integer.valueOf(is_send_ssl) == 1);
    }

    public void setReceiveProtocol() {
        this.receiveProtocol = new Protocol(receive_protocol, receive_server, receive_port, Integer.valueOf(is_receive_ssl) == 1);
    }

    public String getSignature() {
        return signature;
    }


    @Override
    public String toString() {
        return "Addresser{" +
                ", email_account='" + email_account + '\'' +
                ", email_name='" + email_name + '\'' +
                ", email_password='" + email_password + '\'' +
                ", first_mail_uid='" + first_mail_uid + '\'' +
                ", id='" + id + '\'' +
                ", is_receive_ssl='" + is_receive_ssl + '\'' +
                ", is_send_ssl='" + is_send_ssl + '\'' +
                ", last_mail_uid='" + last_mail_uid + '\'' +
                ", receive_port='" + receive_port + '\'' +
                ", receive_protocol='" + receive_protocol + '\'' +
                ", receive_server='" + receive_server + '\'' +
                ", send_port='" + send_port + '\'' +
                ", send_protocol='" + send_protocol + '\'' +
                ", send_server='" + send_server + '\'' +
                ", signature='" + signature + '\'' +
                ", signature_id='" + signature_id + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.email_account);
        dest.writeString(this.email_name);
        dest.writeString(this.email_password);
        dest.writeString(this.first_mail_uid);
        dest.writeString(this.id);
        dest.writeString(this.is_receive_ssl);
        dest.writeString(this.is_send_ssl);
        dest.writeString(this.last_mail_uid);
        dest.writeString(this.receive_port);
        dest.writeString(this.receive_protocol);
        dest.writeString(this.receive_server);
        dest.writeString(this.send_port);
        dest.writeString(this.send_protocol);
        dest.writeString(this.send_server);
        dest.writeString(this.signature);
        dest.writeString(this.signature_id);
        dest.writeString(this.user_id);
        dest.writeParcelable(this.sendProtocol, flags);
        dest.writeParcelable(this.receiveProtocol, flags);
    }

    private Addresser(Parcel in) {
        this.email_account = in.readString();
        this.email_name = in.readString();
        this.email_password = in.readString();
        this.first_mail_uid = in.readString();
        this.id = in.readString();
        this.is_receive_ssl = in.readString();
        this.is_send_ssl = in.readString();
        this.last_mail_uid = in.readString();
        this.receive_port = in.readString();
        this.receive_protocol = in.readString();
        this.receive_server = in.readString();
        this.send_port = in.readString();
        this.send_protocol = in.readString();
        this.send_server = in.readString();
        this.signature = in.readString();
        this.signature_id = in.readString();
        this.user_id = in.readString();
        this.sendProtocol = in.readParcelable(Protocol.class.getClassLoader());
        this.receiveProtocol = in.readParcelable(Protocol.class.getClassLoader());
    }

    public static final Parcelable.Creator<Addresser> CREATOR = new Parcelable.Creator<Addresser>() {
        @Override
        public Addresser createFromParcel(Parcel source) {
            return new Addresser(source);
        }

        @Override
        public Addresser[] newArray(int size) {
            return new Addresser[size];
        }
    };
}
