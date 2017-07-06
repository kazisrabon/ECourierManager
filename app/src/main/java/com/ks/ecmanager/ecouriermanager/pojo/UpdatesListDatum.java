/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UpdatesListDatum implements Serializable {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("updates")
    @Expose
    private String updates;

    @SerializedName("updater")
    @Expose
    private String updater;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdates() {
        return updates;
    }

    public void setUpdates(String updates) {
        this.updates = updates;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }

    @Override
    public String toString() {
        return "UpdatesListDatum{" +
                "status='" + status + '\'' +
                ", updates='" + updates + '\'' +
                ", updater='" + updater + '\'' +
                '}';
    }
}