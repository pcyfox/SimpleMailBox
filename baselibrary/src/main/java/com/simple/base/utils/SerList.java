package com.simple.base.utils;

import java.io.Serializable;


public class SerList<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private java.util.List<T> List;

    public java.util.List<T> getList() {
        return List;
    }

    public void setList(java.util.List<T> List) {
        this.List = List;
    }

}
