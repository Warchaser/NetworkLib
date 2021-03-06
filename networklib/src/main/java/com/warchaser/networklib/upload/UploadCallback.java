package com.warchaser.networklib.upload;

public abstract class UploadCallback<T> {

    public void onRequest(){

    }

    //上传成功
    public abstract void onUploadSuccess(T t);

    //上传失败
    public abstract void onUploadFailed(Throwable e);

    public abstract void onUploadFailed(T t);

    //上传进度回调
    public abstract void onProgress(long bytesWritten, long contentLength);

}
