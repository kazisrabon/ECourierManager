/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.webservices.interfaces;

import com.ks.ecmanager.ecouriermanager.pojo.DoList;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import retrofit.http.Path;

public interface DoListInterface {
    @FormUrlEncoded
    @POST("/{operation}")
    public void getData(
            @Path("operation") String operation,
            @FieldMap Map<String, String> params,
            Callback<DoList> response
    );
}
