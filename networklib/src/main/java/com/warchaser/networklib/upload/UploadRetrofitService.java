package com.warchaser.networklib.upload;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface UploadRetrofitService {

    @POST
    Flowable<ResponseBody> uploadMultipleFiles(@Url String url, @Body MultipartBody body);

}
