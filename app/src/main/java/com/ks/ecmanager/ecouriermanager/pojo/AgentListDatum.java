/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AgentListDatum implements Serializable {

    @SerializedName("agent_id")
    @Expose
    private String agent_id;

    @SerializedName("agent_name")
    @Expose
    private String agent_name;

    @SerializedName("do_name")
    @Expose
    private String do_name;

    @SerializedName("do_id")
    @Expose
    private String do_id;

    public AgentListDatum(String agent_id, String agent_name, String do_name, String do_id) {
        this.agent_id = agent_id;
        this.agent_name = agent_name;
        this.do_name = do_name;
        this.do_id = do_id;
    }

    public AgentListDatum() {
    }

    public String getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(String agent_id) {
        this.agent_id = agent_id;
    }

    public String getAgent_name() {
        return agent_name;
    }

    public void setAgent_name(String agent_name) {
        this.agent_name = agent_name;
    }

    public String getDo_name() {
        return do_name;
    }

    public void setDo_name(String do_name) {
        this.do_name = do_name;
    }

    public String getDo_id() {
        return do_id;
    }

    public void setDo_id(String do_id) {
        this.do_id = do_id;
    }

    @Override
    public String toString() {
        return "AgentListDatum{" +
                "agent_id='" + agent_id + '\'' +
                ", agent_name='" + agent_name + '\'' +
                ", do_name='" + do_name + '\'' +
                ", do_id='" + do_id + '\'' +
                '}';
    }
}