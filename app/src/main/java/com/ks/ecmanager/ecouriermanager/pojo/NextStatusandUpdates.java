/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.pojo;

/**
 * Created by Kazi Srabon on 7/27/2017.
 */

public class NextStatusandUpdates {
//    define_do => 0 = nothing_special 1 = sdo 2 = ddo
    private String next_status;
    private String updaters;
    private int define_do;

    public NextStatusandUpdates(String next_status, String updaters, int define_do) {
        this.next_status = next_status;
        this.updaters = updaters;
        this.define_do = define_do;
    }

    public String getNext_status() {
        return next_status;
    }

    public String getUpdaters() {
        return updaters;
    }

    public int getDefine_do() {
        return define_do;
    }
}
