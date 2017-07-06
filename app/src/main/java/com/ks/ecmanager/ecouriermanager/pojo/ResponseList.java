/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.pojo;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ResponseList {

    private final String TAG = "ResponseList";

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("readable_status")
    @Expose
    private String readable_status;

    @SerializedName("viewer")
    @Expose
    private String viewer;

    @SerializedName("updates")
    @Expose
    private List<UpdatesListDatum> updates = new ArrayList<UpdatesListDatum>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReadable_status() {
        return readable_status;
    }

    public void setReadable_status(String readable_status) {
        this.readable_status = readable_status;
    }

    public String getViewer() {
        return viewer;
    }

    public void setViewer(String viewer) {
        this.viewer = viewer;
    }

    public List<UpdatesListDatum> getUpdates() {
        return updates;
    }

    public void setUpdates(List<UpdatesListDatum> updates) {
        this.updates = updates;
    }

    @Override
    public String toString() {
        String[] viewers = new String[0];
        String separateViewer = "";
        viewer = viewer.replace("{", "");
        viewer = viewer.replace("}", "");
        if (viewer.contains(",")){
            viewers = viewer.split(",");
        }
        for (String s: viewers) {
            separateViewer = " "+s;
            Log.e(TAG, separateViewer);
        }

        return "ResponseList{" +
                "status=" + status +
                ", readable_status=" + readable_status +
                ", viewers=" + viewer +
                '}';
    }
}