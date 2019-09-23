package com.warchaser.networklib.download;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 下载拦截器
 * */
final public class DownloadInterceptor implements Interceptor {

    public DownloadInterceptor(){

    }

    @Override
    public Response intercept(Chain chain) throws IOException, NullPointerException {

        Response originalResponse = chain.proceed(chain.request());

        ResponseBody body = originalResponse.body();

        if(body == null){
            throw new NullPointerException("originalResponse body is null!!");
        }

        MediaType type = body.contentType();

        if(type != null && "text/html".equals(type.toString())){
            throw new IOException("ResponseType not Correct!");
        }

        return originalResponse.newBuilder().body(new DownloadResponseBody(originalResponse.body())).build();
    }
}
