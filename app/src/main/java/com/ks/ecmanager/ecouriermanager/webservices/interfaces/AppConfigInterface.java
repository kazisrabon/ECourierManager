/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.webservices.interfaces;

import com.ks.ecmanager.ecouriermanager.pojo.ResponseList;

import java.util.List;

import retrofit.Callback;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface AppConfigInterface {
    @GET("/{operation}")
    void getData(
            @Path("operation") String operation,
            Callback<List<ResponseList>> response
    );
}
