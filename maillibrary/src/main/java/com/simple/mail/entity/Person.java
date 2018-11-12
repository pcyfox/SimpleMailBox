package com.simple.mail.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Contacts2 和Employee 的父类
 */
public class Person implements Parcelable {
    public static final int SELECT = 0;// 写信的时候，进入选择联系人界面
    public static final int NO_SELECT = 1;// 直接进入选择联系人界面
    public static final int MANAGE_PERSON = 2;// 点击管理界面进入
    public static final int EDIT = 3;// 编辑联系人(或者员工)
    public static final int ADD = 4;// 新建联系人(或者员工)
    public static final int MAX = 200;// 再添加“收件人”“抄送人”“密送人”时，最多可以添加的人数
    public static final int NEAREST_CONTACTS = 200;// 读取的最近联系人的个数
    public String id;
    @Expose
    public String email;
    @Expose
    public String name;
    //因为服务器有的地方用的是email有的地方用的是email_account，增加字段email_account，其实email_account和email是一样的
    public String email_account;

    public boolean isSelected;//选择联系人的时候是否被选中

    public Person() {
    }

    public Person(String name) {
        this.name = name;
    }

    public Person(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Person(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return email != null ? email.equals(person.email) : person.email == null;
    }

    @Override
    public int hashCode() {
        return email != null ? email.hashCode() : 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    protected Person(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.email = in.readString();
        this.isSelected = in.readByte() != 0;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }
}
