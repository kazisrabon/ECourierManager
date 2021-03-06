/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.webservices.interfaces;

import com.ks.ecmanager.ecouriermanager.pojo.Login;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import retrofit.http.Path;

public interface LoginInterface {
    @FormUrlEncoded
    @POST("/{operation}")
    public void getResult(
            @Path("operation") String operation,
            @FieldMap Map<String, String> params,
            Callback<Login> response
    );
}


