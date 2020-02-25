package com.warchaser.networklib.upload;


import com.warchaser.networklib.provider.NetWorkProvider;
import com.warchaser.networklib.util.RxJavaUtils;

import java.io.File;
import java.io.FileNotFoundException;

import io.reactivex.Flowable;

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

    public void uploadFile(String url, File file, String userId, String appId, UploadCallback<BaseUploadResp<UploadResponseBody>> callback){

        if(file == null || file.length() == 0){
            callback.onUploadFailed(new FileNotFoundException("File is Null or Empty!!!"));
            return;
        }

        final FileUploadSubscriber<BaseUploadResp<UploadResponseBody>> subscriber = new FileUploadSubscriber<BaseUploadResp<UploadResponseBody>>(callback);

        final UploadFileRequestBody uploadFileRequestBody = new UploadFileRequestBody(file, callback);

        final Flowable<BaseUploadResp<UploadResponseBody>> flowable = getService().uploadMultipleFiles(url, MultipartBodyBuilder.file2MultipartBody(file, userId, appId, uploadFileRequestBody), appId);

        RxJavaUtils.subscribe(flowable, subscriber);
    }

}
