package com.simple.mail.entity;


public class Image {
    public String path;
    public String name;
    public String cid;

    public Image(String path, String cid) {
        this.path = path;
        this.cid = cid;
    }

    public Image(String path, String name, String cid) {
        this.path = path;
        this.name = name;
        this.cid = cid;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        else {
            if (o instanceof Image) {
                Image image = (Image) o;
                if (image.cid.equals(this.cid) && image.path.equals(image.path)) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public String toString() {
        return "Image{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", cid='" + cid + '\'' +
                '}';
    }
}
