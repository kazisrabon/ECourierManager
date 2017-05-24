/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DoList {

    @SerializedName("status")
    @Expose
    private Boolean status;

    @SerializedName("msg")
    @Expose
    private String msg;

    @SerializedName("do_list")
    @Expose
    private List<AgentDOListDatum> do_list = new ArrayList<AgentDOListDatum>();

    @SerializedName("total_dos")
    @Expose
    private String total_dos;
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

    public List<AgentDOListDatum> getDo_list() {
        return do_list;
    }

    public void setDo_list(List<AgentDOListDatum> do_list) {
        this.do_list = do_list;
    }

    public String getTotal_dos() {
        return total_dos;
    }

    public void setTotal_dos(String total_dos) {
        this.total_dos = total_dos;
    }
}