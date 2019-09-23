package com.warchaser.networklib.download;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Retrofit Download Api
 * */
public interface DownloadRetrofitService {

    /**
     * 文件下载
     */
    @Streaming
    @GET
    Flowable<ResponseBody> downloadFile(@Url String fileUrl);

}
