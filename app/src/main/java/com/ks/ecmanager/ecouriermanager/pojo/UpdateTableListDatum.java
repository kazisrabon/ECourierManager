/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.pojo;

public class UpdateTableListDatum {

    private String current_status;
    private String next_status;
    private String updates;
    private String updaters;

    public UpdateTableListDatum(String current_status, String next_status, String updates, String updaters) {
        this.current_status = current_status;
        this.next_status = next_status;
        this.updates = updates;
        this.updaters = updaters;
    }

    public String getCurrent_status() {
        return current_status;
    }

    public String getNext_status() {
        return next_status;
    }

    public String getUpdates() {
        return updates;
    }

    public String getUpdaters() {
        return updaters;
    }
}