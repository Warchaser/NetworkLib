package com.warchaser.networkexample.network;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 普通请求拦截器
 * 用于加入header等操作
 * */
final public class RequestInterceptor implements Interceptor {

    public RequestInterceptor(){

    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        //TODO 需要进一步重写，如加入header等
        Request original = chain.request();
        HttpUrl originalHttpUrl = original.url();

        HttpUrl.Builder builder = originalHttpUrl.newBuilder();

        //例子:可以全局拼接链接的形式传入参数
//        if(App.getInstance().isMockModel()){
//            builder.addQueryParameter("model","mock");
//        }
//
//        builder.addQueryParameter("deviceId", DeviceIDUtils.getDeviceId());
//        builder.addQueryParameter("number", CacheDataUtil.getWorkNum());
//        builder.addQueryParameter("accessToken", CacheDataUtil.getAccessToken());

        HttpUrl url = builder.build();

//        HttpUrl url = originalHttpUrl.newBuilder()
//                .addQueryParameter("model","")
//                .build();
        Request request = original.newBuilder().url(url).build();

        return chain.proceed(request);
    }
}
