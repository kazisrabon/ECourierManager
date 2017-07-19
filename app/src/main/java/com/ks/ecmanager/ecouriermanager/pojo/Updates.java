/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.pojo;

/**
 * Created by Kazi Srabon on 7/19/2017.
 */

public class Updates {
    private String current_status;
    private String next_status;
    private String updaters;
    private String updates;

    public void setCurrent_status(String current_status) {
        this.current_status = current_status;
    }

    public void setNext_status(String next_status) {
        this.next_status = next_status;
    }

    public void setUpdaters(String updaters) {
        this.updaters = updaters;
    }

    public void setUpdates(String updates) {
        this.updates = updates;
    }

    public String getCurrent_status() {
        return current_status;
    }

    public String getNext_status() {
        return next_status;
    }

    public String getUpdaters() {
        return updaters;
    }

    public String getUpdates() {
        return updates;
    }
}
