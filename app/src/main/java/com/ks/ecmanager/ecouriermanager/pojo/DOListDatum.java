/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DOListDatum implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("value")
    @Expose
    private String value;

    public DOListDatum(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public DOListDatum() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", value = "+value+"]";
    }
}