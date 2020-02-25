package com.warchaser.networklib.upload;

import com.warchaser.networklib.provider.ResponseFormat;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface UploadRetrofitService {

    @POST
    @ResponseFormat(ResponseFormat.JSON)
    Flowable<BaseUploadResp<UploadResponseBody>> uploadMultipleFiles(@Url String url, @Body MultipartBody body, @Header("appID") String appId);

}
