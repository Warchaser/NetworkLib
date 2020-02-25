package com.warchaser.networklib.provider;

import androidx.annotation.NonNull;


import com.warchaser.networklib.download.DownloadInterceptor;
import com.warchaser.networklib.util.NLog;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public final class NetWorkProvider {
    private static final int CONNECT_TIMEOUT_MS = 45_000;
    private static final int READ_TIMEOUT_MS = 45_000;
    private static final String OK_HTTP_LOG = "OK_HTTP";

    private NetWorkProvider(){
        
    }

    /**
     * 提供一个OkHttp3对象
     * */
    @NonNull
    public static OkHttpClient getOkHttpClientWithInterceptors(Interceptor ... interceptors){

        if(interceptors.length == 0){
            return getOkHttpClient();
        }

        final OkHttpClient.Builder builder = getOkHttpClientBuilderDefault();

        for(Interceptor interceptor : interceptors){
            if(interceptor != null){
                builder.addInterceptor(interceptor);
            }
        }

        return builder.build();
    }

    @NonNull
    public static OkHttpClient getOkHttpClient(){
        return getOkHttpClientBuilderDefault().build();
    }

    private static OkHttpClient.Builder getOkHttpClientBuilderDefault(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        try {

//            RequestInterceptor interceptor = new RequestInterceptor();
            builder.connectTimeout(CONNECT_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                    .readTimeout(READ_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                    .addInterceptor(getCommonLoggingInterceptor());
        } catch (Exception | Error e){
            NLog.printStackTrace("NetWorkProvider", e);
        }

        return builder;
    }

    /**
     * 提供一个Retrofit2对象
     * */
    @NonNull
    public static Retrofit getRetrofit(OkHttpClient client, String baseUrl){

        Retrofit.Builder builder = new Retrofit.Builder();

        try {
            builder.baseUrl(baseUrl)
//                    .addConverterFactory(CustomMoshiConverterFactory.create().asLenient())
                    .addConverterFactory(MultipleConverter.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build();

        } catch (Exception | Error e){
            e.printStackTrace();
        }

        return builder.build();
    }

    @NonNull
    public static Retrofit getDownloadRetrofit(String baseUrl){
        DownloadInterceptor interceptor = new DownloadInterceptor();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(CONNECT_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .readTimeout(READ_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .addInterceptor(interceptor);

        return getRetrofit(builder.build(), baseUrl);
    }

    @NonNull
    public static Retrofit getUploadRetrofit(String baseUrl){
        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(CONNECT_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .readTimeout(READ_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        builder.addInterceptor(getCommonLoggingInterceptor());

        return getRetrofit(builder.build(), baseUrl);
    }

    private static HttpLoggingInterceptor getCommonLoggingInterceptor() {
        final HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public synchronized void log(@NonNull String message) {
                if (message.startsWith("{")) {
                    NLog.printJson(OK_HTTP_LOG, message);
                } else if (message.startsWith("<!DOCTYPE html>")) {
                    NLog.printHtml(OK_HTTP_LOG, message);
                } else {
                    NLog.e(OK_HTTP_LOG, message);
                }
            }
        });

        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return httpLoggingInterceptor;
    }


}
