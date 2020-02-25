package com.warchaser.networklib.upload;

import android.util.ArrayMap;

import com.warchaser.networklib.provider.NetWorkProvider;
import com.warchaser.networklib.util.RxJavaUtils;

import org.reactivestreams.Subscriber;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import io.reactivex.Flowable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class UploadRequest {

    private volatile static UploadRequest mInstance;

    private UploadRetrofitService mService;

    private UploadRequest(){

    }

    public static UploadRequest getInstance(){
        if(mInstance == null){
            synchronized (UploadRequest.class){
                if(mInstance == null){
                    mInstance = new UploadRequest();
                }
            }
        }

        return mInstance;
    }

    private synchronized UploadRetrofitService getService(){
        if(mService == null){
            mService = NetWorkProvider.getUploadRetrofit("https://cloud.carautocloud.com/rcg/m85/").create(UploadRetrofitService.class);
        }

        return mService;
    }

    public void uploadFiles(){

    }

    public void uploadFile(String url, File file, String userId, String appId, UploadCallback<ResponseBody> callback){

        if(file == null || file.length() == 0){
            callback.onUploadFailed(new FileNotFoundException("File is Null or Empty!!!"));
            return;
        }

        final FileUploadSubscriber<ResponseBody> subscriber = new FileUploadSubscriber<ResponseBody>(callback);

        final UploadFileRequestBody uploadFileRequestBody = new UploadFileRequestBody(file, subscriber);

        final Flowable<ResponseBody> flowable = getService().uploadMultipleFiles(url, MultipartBodyBuilder.file2MultipartBody(file, userId, appId, uploadFileRequestBody));

        RxJavaUtils.subscribe(flowable, subscriber);
    }

}
