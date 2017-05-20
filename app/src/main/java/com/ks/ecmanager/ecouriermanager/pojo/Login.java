/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */
package com.ks.ecmanager.ecouriermanager.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Login
{
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("authentication_key")
    @Expose
    private String authentication_key;

    @SerializedName("do_mobile")
    @Expose
    private String do_mobile;

    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("do_name")
    @Expose
    private String do_name;

    @SerializedName("admin_id")
    @Expose
    private String admin_id;

    @SerializedName("group")
    @Expose
    private String group;

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public String getAuthentication_key ()
    {
        return authentication_key;
    }

    public void setAuthentication_key (String authentication_key)
    {
        this.authentication_key = authentication_key;
    }

    public String getDo_mobile ()
    {
        return do_mobile;
    }

    public void setDo_mobile (String do_mobile)
    {
        this.do_mobile = do_mobile;
    }

    public boolean getStatus ()
    {
        return status;
    }

    public void setStatus (boolean status)
    {
        this.status = status;
    }

    public String getDo_name ()
    {
        return do_name;
    }

    public void setDo_name (String do_name)
    {
        this.do_name = do_name;
    }

    public String getAdmin_id ()
    {
        return admin_id;
    }

    public void setAdmin_id (String admin_id)
    {
        this.admin_id = admin_id;
    }

    public String getGroup ()
    {
        return group;
    }

    public void setGroup (String group)
    {
        this.group = group;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [message = "+message+", authentication_key = "+authentication_key+", do_mobile = "+do_mobile+", status = "+status+", do_name = "+do_name+", admin_id = "+admin_id+", group = "+group+"]";
    }
}