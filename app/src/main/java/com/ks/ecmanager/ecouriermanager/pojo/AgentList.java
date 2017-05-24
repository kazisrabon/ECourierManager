/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AgentList {

    @SerializedName("status")
    @Expose
    private Boolean status;

    @SerializedName("msg")
    @Expose
    private String msg;

    @SerializedName("agent_list")
    @Expose
    private List<AgentDOListDatum> agent_list = new ArrayList<AgentDOListDatum>();

    @SerializedName("total_agents")
    @Expose
    private String total_agents;
    /**
     * @return The status
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }

    /**
     * @return The msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg The msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<AgentDOListDatum> getAgent_list() {
        return agent_list;
    }

    public void setAgent_list(List<AgentDOListDatum> agent_list) {
        this.agent_list = agent_list;
    }

    public String getTotal_agents() {
        return total_agents;
    }

    public void setTotal_agents(String total_agents) {
        this.total_agents = total_agents;
    }
}