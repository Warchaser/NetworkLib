package com.warchaser.networkexample.network;

import com.google.gson.JsonObject;
import com.warchaser.networkexample.util.Constants;
import com.warchaser.networklib.provider.ResponseFormat;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 配合Retrofit2的网络请求抽象
 * 不要试图实现这个接口
 */
public interface CommonRequest {
    String BASE_URL = Constants.BASE_URL;

    @ResponseFormat(ResponseFormat.JSON)
    @GET(BASE_URL + "getUsersByIndex")
    Flowable<JsonObject> getUsersByIndex(@Query("pageNum") String pageNum, @Query("pageSize") String pageSize);

}
